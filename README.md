# Folio-Sample-Modules

Copyright (C) 2016-2022 The Open Library Foundation

This software is distributed under the terms of the Apache License,
Version 2.0. See the file "[LICENSE](LICENSE)" for more information.

# Introduction

This project contains examples of FOLIO modules (currently a server-side
Vert.x-based module, but more, e.g. a UI/front-end module will come later),
and some general information about writing, packaging and describing modules.

<!-- TODO: Add a few words about what FOLIO is, for new readers. Keep it short! -->

For background understanding, see the
[Okapi Guide and Reference](https://github.com/folio-org/okapi/blob/master/doc/guide.md).

<!-- ../okapi/doc/md2toc -l 2 -h 4 README.md -->
* [What is a module](#what-is-a-module)
    * [Module descriptor](#module-descriptor)
        * [Module tags](#module-tags)
        * [Handlers](#handlers)
* [Server-side modules](#server-side-modules)
    * [Deployment and discovery](#deployment-and-discovery)
    <!-- TODO: * [Logging, Health, and Metrics](#logging-health-and-metrics) -->
    * [Writing a module](#writing-a-module)
        * [Java/Vert.x and Node.js](#javavertx-and-nodejs)
        * [Development environment](#development-environment)
        * [Setting things up](#setting-things-up)
        * [Sample module: hello-vertx](#sample-module-hello-vertx)
        * [Sample module: hello-spring](#sample-module-hello-spring)
        * [Sample module: simple-vertx](#sample-module-simple-vertx)
        * [Sample module: simple-perl](#sample-module-simple-perl)
        * [Utility libraries](#utility-libraries)
        * [Starting your own module](#starting-your-own-module)
    * [Running your module](#running-your-module)
        * [Run it yourself](#run-it-yourself)
        * [Let Okapi start the module](#let-okapi-start-the-module)
        * [Run in a Docker](#run-in-a-docker)
* [UI modules](#ui-modules)
* [Virtual modules](#virtual-modules)
* [Further reading](#further-reading)

## What is a module

A module is a stand-alone unit of functionality that follows FOLIO's ecosystem
guidelines (interfaces and schemas) and conventions; so that Okapi
(FOLIO's middleware/API gateway) can forward requests to it and Stripes
(FOLIO's UI Toolkit) can produce a user interface for it.

At the moment we support several types of modules:

  * Server-side modules
  * User-interface modules
  * Virtual modules, with only dependencies and other metadata

<!--- TODO: We have not yet done anything about virtual modules,
but I believe such ought to work already. Sooner or later
we will need to pay more attention to those. The text is
good enough as it stands -->

FOLIO is an open-ended system and we may end up adding more module types later.


### Module descriptor

A module must come with a JSON file that contains a descriptor for the module.
Typically it is called `ModuleDescriptor.json`. This will tell what services
the module provides, if it depends on some other services (and their versions),
what permissions are needed to use the module, and a number of other things.

For exact definitions, see the RAML from the Okapi-core project. The various
example modules in this project may be helpful too.

The main parts of a ModuleDescriptor are:
* id -- Primary key, uniquely identifying this module.
* name -- A short name to be used in logs and some administrative UIs.
* tags -- A set of short strings that tell something about the module. See below.
* provides -- A set of interfaces, and their versions, that the module provides.
* requires -- A set of interfaces, and their versions, that the module requires.
* handlers -- Tells which HTTP requests the module is serving, and which
permissions are needed to make such a request.
* uiModuleDescriptor -- Placeholder for module-specific configuration for the UI
modules.
* deploymentDescriptor -- Tells how the module is to be deployed (started).

#### Module tags

We have not really started to use module tags in the system, but we are likely
to end up with at least the following:
* ServerModule -- Tells that this is a server side module.
* UiModule -- Tells that this is a UI module.
* VirtualModule -- Tells that this module is purely virtual, no code involved.

We may later add tags for various other purposes.

#### Handlers

The handlers array tells Okapi which requests should be routed to the module
(for example, a GET request to /hello), in what order various modules should
be invoked for that path (so that we can invoke an authentication check before
the module itself, and a logging module after it), and what permission bits
will be needed for making this request.


## Server-side modules

The server-side modules are typically something Okapi deploys on various nodes
on the cluster where it runs. Each of those is a separate process, offering
an HTTP endpoint that serves web service requests that conform to these guidelines.

(Side note: Okapi is designed to be quite flexible, so it does not have to be
used for deploying the modules, if the cluster management system provides
better tools for such. Nor does the thing have to live in a cluster at all, it
is quite possible to have a complete system on a single workstation, for example
for development work.)

In order to avoid problems with system-level dependencies, we have adopted a
policy of running modules in Docker containers. This way, each container can
have all the stuff the module needs, nicely isolated from the node itself, and
from other modules. But this is not a hard requirement, especially when
developing modules it is possible to run them as standard processes on
a workstation.


### Deployment and discovery

Okapi can deploy modules in several ways:

  * Exec'ing a given program (and killing its PID when undeploying).
  * By use of command lines for starting and stopping a service.
  * Using Docker API calls.

Okapi knows what it has deployed on each node, and will route requests to
one of those. It is also possible to tell Okapi's discovery about processes
deployed directly, either on the cluster, or even externally.

The deployment options are specified in the DeploymentDescriptor.

Once deployed, Okapi's proxy part must be informed about the module, and then
the module can be enabled for tenants to use.

<!-- TODO
### Logging, Health, and Metrics
Write something about these
-->


### Writing a module

FOLIO is designed so that different modules can be written in different
languages with different tools.

#### Language Choice

Within FOLIO, server-side modules are primarily written in Java (with some
in Node.js).  These are used a lot within FOLIO, therefore, we have build
libraries and utilities to work with them (especially for standard scaffolding
and boiler-plate code).

Currently, a lot of these modules are written in Java using Vert.x. FOLIO has two Vert.x based frameworks:
* [raml-module-builder](https://github.com/folio-org/raml-module-builder) for RAML files
* [folio-vertx-lib](https://github.com/folio-org/folio-vertx-lib) for OpenAPI files

Some newer modules are written with the
[Spring Way](https://docs.google.com/presentation/d/1YgDCBimLTQ1ou-fPhvyKbWpVkec3Goa8lyJJe2hcLHk/edit)
philosophy.  This uses Spring Boot, a more popular Java framework, as well as
openapi, making it easier to take advantage of more modern Java features.
If you are looking to start a new FOLIO module, you will probably want to
use this methodology and framework as opposed to something RMB/Vert.x based.

FOLIO is language agnostic because modules and Okapi communicate over APIs only.

However, FOLIO has implemented a
[Technical Designs and Decisions](https://wiki.folio.org/display/DD/Technical+Designs+and+Decisions)
process to provide consistency in FOLIO and to minimize the use of alternative tech stacks.

#### Development environment

These are some notes to assist developers to prepare for their local development.

To build and run the local instance of Okapi, see the
[Okapi Guide and Reference](https://github.com/folio-org/okapi/blob/master/doc/guide.md)
and the
[Contribution guidelines for Okapi](https://github.com/folio-org/okapi/blob/master/CONTRIBUTING.md).

So that will require:
 * Apache Maven 3.3.1 or later.
 * Java 11 JDK

As shown in the Okapi Guide and these samples, the command-line http client
`curl` is used extensively for demonstration and development.

As explained above, using Docker is not necessary, but certainly is useful,
and these samples do use it. So take the plunge. Okapi cleans up its own
deployments, but be sure to keep the docker space clean.

Both Docker and Maven can utilise a local repository, to enable faster
and more reliable turnaround.

For development machines using "Docker Toolbox" (instead of native)
see our [FAQ](FAQ.md#docker-toolbox-and-localhost-ports) about "localhost" ports.

As explained in the Okapi Guide, Okapi uses HTTP 1.1 with
chunked encoding to make the connections to the modules.

(See notes below for additional requirements for developing UI modules.)

#### Setting things up
There is a little bit that needs to be set up, depending on what kind of
system you are working on.

It may be necessary to tell the Docker daemon to listen on
a HTTP port, and not just a local socket, since the vertx HTTP client we use
for talking to Docker can not talk to local sockets.

We need to specify how the modules may talk back to Okapi. Especially if they
run in Docker containers (as we do in most examples) some tricks may be needed,
since the default address `http://localhost:9130/` does not work from inside a
Docker container.

Speaking of docker, the example scripts create new docker images freely. At
some point you need to clean them up. A quick command to do that (at least on
Linux) is `docker images -q |xargs docker rmi`. This tries to remove all docker
images, but fails on some of the more important ones (since we did not specify
`-f` for the `docker rmi` command). Docker may need to do some extra work next
time you build images, but not too much.

<!--  TODO - this does not seem to belong here, but on some more high-level
document on module development.

 Although these examples are meant to be run locally, at some point we wish
to keep our Docker images in a repository. Obviously you will need login
credentials for the repository before you can push your modules there. The
relevant commands are
```
 - docker login # once on every new machine
 - docker images -q |xargs docker rmi  # Kill lots of old images
 - docker build -t folio-hello-module  # Build the image
 - docker tag a31a1b728e79 folio-hello-module:v1.1.1  # Local tag
 - docker tag a31a1b728e79 folio-docker-registry.indexdata.com/folio-hello-module:v1.1.1
 - docker push folio-docker-registry.indexdata.com/folio-hello-module:v1.1.1
```
-->

See notes about
[configuring Docker](https://docs.docker.com/engine/installation/#platform-support-matrix)
for all operating systems.

##### Linux

With recent versions of Linux using 'systemd'
(e.g. from Debian 8 Jessie, Ubuntu 15.04, etc.)
[configure](https://docs.docker.com/engine/installation/#platform-support-matrix) the Docker daemon:

  * Edit the file
`/etc/systemd/system/multi-user.target.wants/docker.service`
  * Locate the line that says
`#ExecStart=/usr/bin/docker -d -H fd:// $DOCKER_OPTS`
  * Edit it to say `ExecStart=/usr/bin/dockerd -H tcp://127.0.0.1:4243 -H fd://`
  * `sudo systemctl daemon-reload`
  * `sudo service docker restart`

Debian is a bit behind with the latest versions of Docker. You may want to follow
the instruction at https://docs.docker.com/engine/installation/linux/debian/ to
get the latest and finest. Especially if you plan to be pushing docker images
to a repository. The one in Debian should be enough to work through these
examples.

One good way to start Okapi is with:
```
   cd .../okapi
   export OKAPIHOST=`hostname`
   java  \
      -Dokapiurl="http://$OKAPIHOST:9130" \
      -Dloglevel=DEBUG \
      -jar okapi-core/target/okapi-core-fat.jar dev
```

Other ways to get a value for OKAPIHOST are:
  * `export OKAPIHOST=$(ifconfig docker0 | awk '/inet addr:/ { print $2 }' | cut -d: -f 2)`
  * On many Debian installations it may be 172.17.0.1 or 172.17.41.1
  * Use the IP address of the public interface of your workstation with something
like `ip addr show eth0`

<!-- TODO: Check what works on a Mac, and document here
##### Mac
TODO: What about the Docker daemon on a Mac?

There is a known problem with Docker on Mac, the docker images can not talk to
the host machine. One possible solution is to define a new IP for the loopback
interface, for example 10.99.88.77 (How to do that on a Mac). There can also be
issues with the firewall...
```
   cd .../okapi
   export OKAPIHOST="10.99.88.77"
   java  \
      -Dokapiurl="http://$OKAPIHOST:9130" \
      -Dloglevel=DEBUG \
      -jar okapi-core/target/okapi-core-fat.jar dev
```

-->

<!-- TODO - What is needed on other Linuxes, Mac, Windows, others -->

#### Sample module: hello-vertx

There is a very minimal "hello, world" module in the hello-vertx directory.
Written as an educational example, it may serve as a starting point for a
server-side FOLIO module.
Its README has some information about its structure and how to run it in a
Docker container.

The sample module uses Apache Log4j for its logging, the same way as Okapi itself
does, so its logs should be compatible.

#### Sample module: hello-spring

This is a reimplementation of hello-vertx, but using the Java Spring Boot
framework and OpenAPI specification.

#### Sample module: simple-vertx

This is a slightly more complex example, again based on Java and vert.x. It has
a little bit more structure, and it uses the hello-vertx module to demonstrate
how to make calls to other modules.

#### Sample module: simple-perl
This is another very simple module, written in Perl, just to show that everything
does not have to be Java.
<!-- TODO - At some point we need to have a more complex example in Perl, with
permissions, logging, instrumentation, and all possible bells and whistles -->

#### Utility libraries

There are several useful classes and utilities for writing modules in Okapi
and the core Domain Models project. We have started to extract some of them
into a separate module, okapi-common, for easier reuse.

#### Starting your own module

Assume that you want to write your own module. Here is one way to get started.
We take the hello-vertx (or hello-spring) module as a starting point, and
produce a new module that we call my-module. These examples are written for
Linux, but something similar ought to work on any other platform.

First, make sure you have all the development tools you need. Check out Okapi
itself, and these folio-sample-modules. We assume all your projects live under
a projects directory, here denoted by .../proj. For convenience we keep the
root directory in an environment variable $ROOTDIR.

```
  cd .../proj
  export ROOTDIR=`pwd`
  git clone --recursive https://github.com/folio-org/okapi.git
```

Next build Okapi itself:

```
  cd $ROOTDIR/okapi
  mvn install
```

Check that you see the `BUILD SUCCESS` line near the end of the output. Next
check out the folio-sample-modules to get the module we want to
start from, and make a new copy of it:

```
  cd $ROOTDIR
  git clone https://github.com/folio-org/folio-sample-modules.git
  cp -a folio-sample-modules/hello-spring/ mymodule # spring way
  cp -a folio-sample-modules/hello-vertx/ mymodule # vert.x
```

Open the project in your favourite IDE, in this example NetBeans. Use its
rename function to rename the display name and ArtifactId to "mymodule".
You should probably rename the source package to something else like
"org.foo.mymopdule", unless you are starting up a new FOLIO sample module.

Now you can compile the module in your IDE or with `mvn install`. Again,
check for the "BUILD SUCCESS" message.

Next, edit the `ModuleDescriptor.json`. Find all occurrences of "hello" and
change them to "mymodule".

Edit also the Dockerfile. If you are using `hello-vertx`, change the
`ENV VERTICLE_FILE` line to refer to `mymodule-fat.jar` and edit the comments
in the beginning.  If you are using `hello-spring`, modify the `APP_FILE ENV`.

Now you can walk through the examples in README.md, substituting "mymodule" for
"hello" where proper. You should be able to create the docker image, see that
it can run in isolation, start Okapi, launch the module and access it.

Congratulations, you have your own module! Now you just need to make it do
what ever you want it to do, and for that we can not give detailed instructions.
Some useful hints:
  * You probably want to make the module respond to some other path(s) than
`/hello`. Change the `RoutingEntries` in the ModuleDescriptor, and, for vert.x,
the vertx router in the MainVerticle.java file.  For spring, modify the `api.yaml`
per openapi specifications.
  * You probably should move the actual processing methods away from the current
main class, likely into a class of its own, and make the routes/controller point
to it.  Most likely you will create other classes to support your operations.
  * Rewrite most of the README to reflect _your_ module.

### Running your module
There are three different ways to run your module. They differ in the way that
the module is started up, everything else is the same. In all cases you need
to declare the module to Okapi and enable it for your test tenant, who has to
exist. The methods differ in the LaunchDescriptor you give to Okapi. This can
be done in two ways, either inside the ModuleDescriptor, as we did in the
hello-vertx module, or in the DeploymentDescriptor, as we did in simple-perl
module. Where ever the LaunchDescriptor comes from, it needs to specify how
the module gets started.

#### Run it yourself
You are responsible for starting and stopping the module, possibly from your
IDE or a separate console window. You can choose the port yourself, usually
8080 is a good default. You should not provide a LaunchDescriptor at all, since
Okapi is not launching the service for you. Instead you need to provide the
URL where your module can be reached, often something like `http://localhost:8080`

This is a good way while you are working with your module. You set Okapi up once
and leave it running. You can start and stop your module as many times as you
like, and see all its debug output.

#### Let Okapi start the module
If you put a LaunchDescriptor in your ModuleDescriptor, Okapi will start the
module for you, every time you request it to be deployed. This way, you can
write a small curl script to get everything up and running. If you do this
kind of thing in production environment, you probably have the modules running
at some fixed location, and can put an absolute path in the LaunchDescriptor.
But while developing, it is nicer to be flexible, and use relative paths, so
everything can be run under your home directory. That is no problem, if Okapi
and your module are running under the same project directory. Then you can
specify something like "../folio-sample-modules/simple-perl/simple.pl" as the
exec line, and Okapi will find the module all right.

#### Run in a Docker
In a cloud based production environment, we recommend running all modules
in their own Docker containers. That way, there is no need to be precise with
the paths. You can distribute modules as Docker images, maybe using a public
(or your own private) Docker repository. The drawback is that you need to
create the Docker image. See the hello-vertx module for a simple example of
how this works.

## UI modules

The UI modules are quite different from the server-side modules, and rely
on the browser technology stack (React/Redux). The system's API is
still under development. Refer to the FOLIO user-interface toolkit,
called [Stripes](http://dev.folio.org/doc/#user-interface).

<!-- TODO - Describe the way UI modules are written, and bundled -->

## Virtual modules

Virtual modules are pure metadata, with no code to write. All that is needed is
to create a good ModuleDescriptor, e.g. one that lists dependencies of other,
concrete modules.

## Further reading

For more about Okapi, refer to its documentation and even the source code at
https://github.com/folio-org/okapi

Also consult the README for the
[hello-vertx](https://github.com/folio-org/folio-sample-modules/tree/master/hello-vertx)
example which covers some useful ground.

Aside from the sample modules in this repository, there are
various other
[server-side](http://dev.folio.org/source-code/#server-side) modules and
[client-side](http://dev.folio.org/source-code/#client-side) UI modules,
which would also be instructive to browse.

Consult the individual repositories' documentation for the status of
the code in these modules.

See project [OKAPI](https://issues.folio.org/browse/OKAPI)
at the [FOLIO issue tracker](http://dev.folio.org/community/guide-issues).

Other FOLIO Developer documentation is at [dev.folio.org](http://dev.folio.org/)
