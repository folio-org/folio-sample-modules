# Hello-vertx

A minimal "Hello, world" module written in Java, using the vert.x framework,
the same tools as we use for developing Okapi core.

The module supports a `GET` request to `/hello`, which just returns the
traditional greeting, in plain text. It also supports a POST request,
which accepts any Json structure, and returns another, more complex one.

## Files

There are two source files:
 - MainLauncher.java: a bit of template code to launch vert.x
 - MainVerticle.java: the "main program", a HTTP server that serves
   our requests.

There are Json structures, ready to POST to Okapi for setting up a demonstration
of this module:
 - module.json: A module description for the module
 - deploy.json: A module deployment descriptor for running the module under Okapi
 - tenant.json: to create a tenant for whom we can enable the module
 - enable.json: A small structure to enable the module for our test tenant

Other noteworthy files are:
 - Dockerfile: Docker setup
 - pom.xml: Maven config on how to build the project
 - log4j.properties: where you can control the logging
 - README.md: This file, explaining what is where and how to use the module

## Compiling
```
   mvn install
```
See that it says "BUILD SUCCESS" near the end

## Docker
Build the docker container with
```
   docker build -t indexdata/folio-hello-module .
```

Test that it runs with
```
   docker run -t -i -p 8080:8080 indexdata/folio-hello-module
```

And check it in another window:
```
   curl http://localhost:8080/hello
```

## Installing the module

First of all you need a running Okapi instance.
```
   cd .../okapi
   java -jar okapi-core/target/okapi-core-fat.jar dev
```

If you have no tenants defined, you need to add one
```
curl -w '\n' -X POST -D - \
  -H "Content-type: application/json" \
  -d @tenant.json  \
  http://localhost:9130/_/proxy/tenants
```

Next we need to deploy the module. There is a deploymentDescriptor in `deploy.json`
```
curl -w '\n' -X POST -D - \
  -H "Content-type: application/json" \
  -d @deploy.json  \
  http://localhost:9130/_/deployment/modules

```
Now the module should be running, probably on port 9131. You can check it with
```
curl -w '\n' -D - http://localhost:9131/hello
```


After that we need to declare the module to Okapi.
```
curl -w '\n' -X POST -D -   \
   -H "Content-type: application/json"   \
   -d @module.json \
   http://localhost:9130/_/proxy/modules
```
Then we need to enable the module for our tenant
```
curl -w '\n' -X POST -D -   \
    -H "Content-type: application/json"   \
    -d @enable.json \
    http://localhost:9130/_/proxy/tenants/our/modules
```

## Using the module

```
curl -w '\n' -H "X-Okapi-Tenant: our" http://localhost:9130/hello
```

Or you can post any valid Json, for example our enabling request
```
curl -w '\n' -X POST -D -   \
    -H "Content-type: application/json"   \
    -H "X-Okapi-Tenant: our" \
    -d @enable.json \
    http://localhost:9130/hello
```

## Cleaning up
```
curl -w '\n' -X DELETE  -D -    http://localhost:9130/_/proxy/tenants/our/modules/hello

curl -w '\n' -X DELETE  -D -    http://localhost:9130/_/proxy/modules/hello

curl -w '\n' -X DELETE   -D -    http://localhost:9130/_/deployment/modules/localhost-9131
```
Finally you can stop the Okapi, with a Ctrl-C in its terminal window.


## What next

As such this module is pretty useless. You can use it to understand what a Folio
module is, and as an example, or even a starting point, for creating your own
modules.
