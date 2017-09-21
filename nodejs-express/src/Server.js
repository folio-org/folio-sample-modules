var express = require("express");
var parseArgs = require("minimist");
var bodyParser = require('body-parser');

var argv = parseArgs(process.argv.slice(2));
var port = 3000;

//Read the "p" argument on the command line to set the port
if (argv.p) {
  port = parseInt(argv.p);
  if (isNaN(port)) {
    throw "Invalid port specifier: " + argv.p;
  }
}

//We will use an array to store all of our things
var thing_list = [];

//Given a thing by name, return the index in our thing_list
function get_thing_index(name) {
  for (var i = 0; i < thing_list.length; i++) {
    var thing = thing_list[i];
    if (thing.name == name) {
      return i;
    }
  }
  return -1;
}

//Given a thing by name, return the object
function get_thing(name) {
  for (var i = 0; i < thing_list.length; i++) {
    var thing = thing_list[i];
    if (thing.name == name) {
      return thing;
    }
  }
  return null;
}

//Given a new thing object, add it to the master list
function add_thing(thing) {
  if (thing.name == null) {
    return null;
  }
  if (get_thing(thing.name) != null) {
    return null;
  }
  thing_list.push(thing);
  return thing;
}

//Given a thing object, remove it from the master list
function delete_thing(thing) {
  var index = get_thing_index(thing.name);
  if (index != -1) {
    thing_list.splice(index, 1);
  }
}

//Strip the sensitive fields out of a thing
function sanitize_thing(thing) {
  var new_thing = {};
  new_thing.name = thing.name;
  new_thing.purpose = thing.purpose;
  return new_thing;
}

//Return a list of things with the sensitive fields removed
function get_sanitized_thing_list() {
  return thing_list.map(sanitize_thing);
}

//Function to check to see if we have the permission set to see secret fields
function check_sensitive_permission(req) {
  var permissions_list = [];
  try {
    permissions_list = JSON.parse(req.get('X-Okapi-Permissions'));
  } catch (e) {
    //Do nothing, just don't populate the list
  }
  if (permissions_list != null && permissions_list.indexOf("thing.see_sensitive") != -1) {
    return true;
  }
  return false;
}

//Function used by the router to handle the /things URL
function handle_things(req, res) {
  res.type('application/json');
  var see_sensitive = check_sensitive_permission(req);
  if (req.method == "GET") {
    res.status(200);
    if (see_sensitive) {
      res.json(thing_list);
    } else {
      res.json(get_sanitized_thing_list());
    }
  } else if (req.method == "POST") {
    var new_thing = req.body;
    if (get_thing(new_thing.name) != null) {
      res.status(400).json({
        "error": "A thing by that name already exists"
      });
    } else {
      var added_thing = add_thing(new_thing);
      if (added_thing != null) {
        res.status(201).json(added_thing);
      } else {
        res.status(400).json({
          "error": "Invalid thing to add"
        });
      }
    }
  } else {
    res.status(400).json({
      "error": "Unsupported request"
    });
  }
}

//Function used by the router to handle the /things/<thing_name> URL
function handle_one_thing(req, res) {
  var name = req.params.name;
  res.type('application/json');
  var see_sensitive = check_sensitive_permission(req);
  if (req.method == "GET") {
    console.log("Attempting to get the thing by name");
    var thing = get_thing(name);
    if (thing == null) {
      res.status(404).json({
        "error": "No thing by the name: '" + name + "' could be found"
      });
    } else {
      res.status(200);
      if (see_sensitive) {
        res.json(thing);
      } else {
        res.json(sanitize_thing(thing));
      }
    }
  } else if (req.method == "PUT") {
    var thing = get_thing(name);
    if (thing == null) {
      res.status(404).json({
        "error": "No thing by that name could be found"
      });
    } else {
      new_thing = req.body;
      thing.purpose = new_thing.purpose;
      thing.secret_power = new_thing.secret_power;
      res.status(200).json(thing);
    }
  } else if (req.method == 'DELETE') {
    var thing = get_thing(name);
    if (thing == null) {
      res.status(404).json({
        "error": "No thing by that name could be found"
      });
    } else {
      delete_thing(thing);
      res.status(200).json({});
    }
  } else {
    res.status(400).json({
      "error": "Unsupported request"
    });
  }
}

var app = express();

//We need the bodyParser in order to read POST and PUT bodies
app.use(bodyParser.json());

var router = express.Router();

//Map the express router to the root of our app
router.use(function(req, res, next) {
  console.log("/" + req.method);
  next();
});

//Assign function handlers to the routes
router.all('/things', handle_things);
router.all('/things/:name', handle_one_thing);
app.use("/", router);
app.listen(port, function() {
  console.log("Listening on port " + port);
});
