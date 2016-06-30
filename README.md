# Folio-Sample-Modules

This project contains some examples of Folio modules (currently only one, but
more will come later), and some general information about writing modules (in
this README).

(NOTE - Still working on this piece of text. - Heikki)


<!-- TODO: Add a few words about what Folio is, for new readers. Keep it short! -->

## What is a module

A module is a stand-alone web service that follows some guidelines, so that
Okapi (Folio's API gateway) can forward requests to it.

At the moment we support several types of modules:
  * Server side
  * UI modules
  * Virtual module, with only dependencies and other metadata

We may end up adding more types later.


### Module descriptor

A module must come with a Json file that contains a descriptor for the module.
Typically it is called ModuleDescriptor.json. This will tell what services
the module provides, if it depends on some other services (and their versions),
what permissions are needed to use the module, and a number of other things.

<!-- TODO -->


## Server side modules

The server side modules are typically something Okapi deploys on various nodes
on the cluster where it runs. Each of those is a separate process, offering
a HTTP endpoint that serves web service requests that conform to these guidelines.

(Side note: Okapi is designed to be quite flexible, so it does not have to be
used for deploying the modules, if the cluster management system provides
better tools for such. Nor does the thing have to live in a cluster at all, it
is quite possible to have complete system on a single workstation, for example
for development work.)

In order to avoid problems with system dependencies, we have adopted a policy
of running modules in Docker containers. This way, each container can have all
the stuff the module needs, nicely isolated from the node itself, and from other
modules. But this is not a hard requirement, especially when developing modules
it may be nicer to run them as processes on the workstation.


### Deployment and discovery

Okapi can deploy modules in two ways:
  * Exec'ing a given program (and killing its PID when undeploying)
  * By use of command lines for starting and stopping a service
Okapi remembers what it has deployed on each node, and will route requests to
one of those. It is also possible to tell Okapi's discovery about processes
deployed directly, either on the cluster, or even externally.

Once deployed, Okapi's proxy part must be informed about the module, and then
the module can be enabled for tenants to use.


### Writing a module

Folio is designed so that different modules can be written in different languages
with different tools.

#### Java and vert.x

So far we have only written server side modules in Java, using vert.x.

##### Development tools

##### Sample module
There is a very minimal "hello, world" module in the hello-vertx directory.
Written as an educational example, it may serve as a starting point for some
real module.

##### Utilitiy libraries
There are several useful classes in Okapi itself. We plan to extract them into
a library jar, so they can be used in modules too.
<!-- TODO - Nice idea, but are we going to do it? -->

## UI modules
The UI modules are quite different from the server side modules.

## Virtual modules
Virtual modules are pure metadata, with no code to write. All you need to do is
to create a good ModuleDescriptor, probably one that lists dependencies of other,
less virtual modules.

## Further reading

For more about Okapi, refer to its documentation and even the source code at
https://github.com/sling-incubator/okapi
<!-- TODO - Use the public address, when we have one -->

