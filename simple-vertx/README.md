# Simple-vertx

A fairly small module written in Java, using the Vert.x framework,
the same tools as we use for developing Okapi core.

This module is very similar to the `hello-vertx` module, it supports a `GET`
request to `/simple`, which returns a simple string. The difference is that
it will invoke the `hello` module to get the greeting. It is probably a good
idea to work through the `hello` example before studying this one.

The key differences include:
* It makes a call to the 'hello' module, instead of producing a constant response.
* In the POST handler, it parses the JSON it gets from hello, and adds its own
message to it.
* The POST handler is operating on the whole body of the message, unlike the
hello module, which pipes its request into its response asynchronously.
* The code is split in two classes: MainVerticle.java and SimpleWebService.java
* We need to set up the URL to make calls back to Okapi, from within a Docker
container.


## Files

There are two source files:

 - MainVerticle.java: the "main program", an HTTP server that serves
   our requests.
 - SimpleWebService.java: The web service handlers.

There are JSON structures, ready to POST to Okapi for setting up a demonstration
of this module:

 - ModuleDescriptor.json: A module description for the module.
 - DeploymentDescriptor: to tell where to deploy the module (localhost, in this case).
 - TenantModuleDescriptor.json: A small structure to enable the module for our test tenant.

Other noteworthy files are:

 - Dockerfile: Docker setup.
 - pom.xml: Maven config on how to build the project.
 - log4j.properties: Configuration for controlling the logging.
 - README.md: This file, explaining what is where and how to use the module.

## Compiling

```
   mvn install
```

See that it says "BUILD SUCCESS" near the end.

## Docker

Build the docker container with:

```
   docker build -t indexdata/folio-simple-module .
```

## Installing the module

First of all you need a running Okapi instance, probably in a separate terminal
window. There is one catch, Okapi needs to tell the modules how to contact it
back. Since the module run in a docker container, it can not refer to Okapi
by its default address at `localhost` as that would refer to the thing inside
the docker container. Instead we need to use the correct machine name.
```
   cd .../okapi
   export HOST=`hostname`
   java  \
      -Dokapiurl="http://$HOSTNAME:9130" \
      -Dloglevel=DEBUG \
      -jar okapi-core/target/okapi-core-fat.jar dev
```

You also need to define a tenant, and have the hello module running. If you do
not want to follow the instructions in its README, the easiest way is to use
the `runhello.sh` script that comes with the hello module.

```
    cd .../folio-sample-modules/hello-vertx
    mvn install
    ./runhello.sh
```

The script sets everything up, and should end with a hello message.

The process of running the simple module is the same as with the hello module,
we POST a ModuleDescription, we deploy the module, and enable it for our tenant.
All this is encapsulated in the `runsimple.sh` script.

```
    cd .../folio-sample-modules/hello-vertx
    mvn install
    ./runsimple.sh
```


## Using the module

```
curl -w '\n' -H "X-Okapi-Tenant: testlib" http://localhost:9130/simple
```

Or you can post any valid JSON, for example our enabling request:

```
curl -w '\n' -X POST -D -   \
    -H "Content-type: application/json"   \
    -H "X-Okapi-Tenant: testlib" \
    -d @TenantModuleDescriptor.json \
    http://localhost:9130/hello
```

## Cleaning up

```
curl -w '\n' -X DELETE  -D -    http://localhost:9130/_/proxy/tenants/testlib/modules/hello

curl -w '\n' -X DELETE  -D -    http://localhost:9130/_/proxy/modules/hello

curl -w '\n' -X DELETE  -D -    http://localhost:9130/_/deployment/modules/localhost-9131
```

Finally you can stop the Okapi, with a Ctrl-C in its terminal window.


## What next

As such this module is pretty useless. You can use it to understand what a FOLIO
module is, and as an example, or even as a starting point, for creating your own
modules.
