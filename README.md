# Folio-Sample-Modules

(NOTE - Still working on this piece of document. - Heikki)


This project contains some examples of Folio modules (currently only one, but
more will come later), and some general information about writing modules (in
this README).

<!--- TODO: Add a few words about what Folio is, for new readers. Keep it short! --->

## What is a module

A module is a stand-alone web service that follows some guidelines, so that
Okapi (Folio's API gateway) can forward requests to it.

At the moment we support several types of modules:
  * Server side
  * UI modules
  * Virtual module, with only dependencies and other metadata
<!--- TODO: We have not yet done anything about virtual modules, but I believe
such ought to work already. Sooner or later we will need to pay more attention
to those. The text is good enough as it stands --->

We may end up adding more types later.


### Module descriptor

A module must come with a Json file that contains a descriptor for the module.
Typically it is called ModuleDescriptor.json. This will tell what services
the module provides, if it depends on some other services (and their versions),
what permissions are needed to use the module, and a number of other things.

For exact definitions, see the RAML from the Okapi-core project. The various
example modules in this project may to be helpful too.

The main parts of a ModuleDescriptor are
* id - primary key, uniquely identifying this module
* name - A short name to be used in logs and some administrative UIs
* tags - A set of short strings that tell something about the module, see below
* provides - A set of interfaces, and their versions, that the module provides.
* requires - A set of interfaces, and their versions, that the module requires
* routingEntries - Tells which HTTP requests the module is serving, and which
permissions are needed to make such a request
* uiModuleDescriptor - Placeholder for module-specific configuration for the UI
modules


#### Module tags
We have not really started to use module tags in the system, but we are likely
to end up with at least the following:
* ServerModule - tells that this is a server side module
* UiModule - tells that this is a UI module
* VirtualModule - tells that this module is pure virtual, no code involved

We may later add tags for various other purposes.

#### Routing Entries
A routing entry tells Okapi which requests should be routed to the module (for
example, a GET request to /hello), in what order various modules should be
invoked for that path (so that we can invoke an auth check before the module
itself, and a logging module after it), and what permission bits will be needed
for making this request.



## Server side modules

The server side modules are typically something Okapi deploys on various nodes
on the cluster where it runs. Each of those is a separate process, offering
a HTTP endpoint that serves web service requests that conform to these guidelines.

(Side note: Okapi is designed to be quite flexible, so it does not have to be
used for deploying the modules, if the cluster management system provides
better tools for such. Nor does the thing have to live in a cluster at all, it
is quite possible to have complete system on a single workstation, for example
for development work.)

In order to avoid problems with system level dependencies, we have adopted a
policy of running modules in Docker containers. This way, each container can
have all the stuff the module needs, nicely isolated from the node itself, and
 rom other modules. But this is not a hard requirement, especially when
developing modules it may be nice to run them as processes on a workstation.


### Deployment and discovery

Okapi can deploy modules in two ways:
  * Exec'ing a given program (and killing its PID when undeploying)
  * By use of command lines for starting and stopping a service
Okapi knows what it has deployed on each node, and will route requests to
one of those. It is also possible to tell Okapi's discovery about processes
deployed directly, either on the cluster, or even externally.

The deployment options are specified in the DeploymentDescriptor.

Once deployed, Okapi's proxy part must be informed about the module, and then
the module can be enabled for tenants to use.

<!--- TODO
### Logging, Health, and Metrics
Write something about these
--->



### Writing a module

Folio is designed so that different modules can be written in different languages
with different tools.

#### Java and vert.x

So far we have only written server side modules in Java, using vert.x.

<!---
##### Development tools

TODO: write something about the necessary development tools, maven repos,
etc.
--->

##### Sample module
There is a very minimal "hello, world" module in the hello-vertx directory.
Written as an educational example, it may serve as a starting point for some
real module. Its README has some information on how to run it in a Docker
container.

The sample module uses log4j for its logging, the same way as Okapi itself
does, so its logs should be compatible.

##### Utilitiy libraries
There are several useful classes in Okapi itself. We plan to extract them into
a library jar, so they can be used in modules too.
<!--- TODO - Nice idea, but are we going to do it? --->

## UI modules
The UI modules are quite different from the server side modules. The system is
still under development, so it may be too early to write much about it.

<!--- TODO - Describe the way UI modules are written, and bundled --->

## Virtual modules
Virtual modules are pure metadata, with no code to write. All you need to do is
to create a good ModuleDescriptor, probably one that lists dependencies of other,
less virtual modules.

## Further reading

For more about Okapi, refer to its documentation and even the source code at
https://github.com/sling-incubator/okapi
<!--- TODO - Use the public address, when we have one --->

