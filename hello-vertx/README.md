# Hello-vertx

A minimal "Hello, world" module written in Java, using the Vert.x framework,
the same tools as we use for developing Okapi core.

The module supports a `GET` request to `/hello`, which just returns the
traditional greeting, in plain text. It also supports a POST request,
which accepts any JSON structure, and returns another, more complex one.

## Files

There is only one source file:

 - MainVerticle.java: the "main program", an HTTP server that serves
   our requests.

There are JSON structures, ready to POST to Okapi for setting up a demonstration
of this module:

 - ModuleDescriptor.json: A module description for the module.
 - DeploymentDescriptor.json: A module deployment descriptor for running the module under Okapi.
 - TenantDescriptor.json: To create a tenant for whom we can enable the module.
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
   docker build -t folio-hello-module .
```

Test that it runs with:

```
   docker run -t -i -p 8080:8080 folio-hello-module
```

And check it in another window:

```
   curl http://localhost:8080/hello
```

Now stop that test run.

## Installing the module

We are essentially following the
[Deploying Modules](https://github.com/folio-org/okapi/blob/master/doc/guide.md#example-1-deploying-and-using-a-simple-module)
sections of the Okapi Guide and Reference, which describe the process in detail.

First of all you need a running Okapi instance.
(Note that [specifying](../README.md#setting-things-up) an explicit 'okapiurl' might be needed.)

```
   cd .../okapi
   java -jar okapi-core/target/okapi-core-fat.jar dev
```

Since we just started Okapi, we have no tenants defined. So let's make one:

```
curl -w '\n' -X POST -D - \
  -H "Content-type: application/json" \
  -d @TenantDescriptor.json  \
  http://localhost:9130/_/proxy/tenants
```

We need to declare the module to Okapi:

```
curl -w '\n' -X POST -D -   \
   -H "Content-type: application/json"   \
   -d @ModuleDescriptor.json \
   http://localhost:9130/_/proxy/modules
```

The ModuleDescriptor tells Okapi what the module is called, what services it
provides, and how to deploy it. Note that the command line to start a docker
is:
```
docker run --cidfile=/tmp/docker.%p.cid -p %p:8080 folio-hello-module
```

Some small details to note about that:
  * Unlike in real production, we do not have a `-d` there. That would detach
STDOUT and STDERR, and we would not see its log.
  * The module listens on its default port 8080, and the Docker command line
maps that to whatever port Okapi assigns to the module.
  * We keep the docker id in a file in /tmp, so we know which docker to kill
when that time comes. The file name includes the port number, as that must be
unique within one node.



Next we need to deploy the module. There is a deployment descriptor in
`DeploymentDescriptor.json`. It tells Okapi to start the module on 'localhost'.


Deploy it via Okapi discovery:

```
curl -w '\n' -D - -s \
  -X POST \
  -H "Content-type: application/json" \
  -d @DeploymentDescriptor.json  \
  http://localhost:9130/_/discovery/modules
```

The module should now be running on the next available port, e.g. 9131.

Check the running module:

```
curl -w '\n' -D - http://localhost:9131/hello
```

and see the container with ```docker ps```

Then we need to enable the module for our test tenant:

```
curl -w '\n' -X POST -D -   \
    -H "Content-type: application/json"   \
    -d @TenantModuleDescriptor.json \
    http://localhost:9130/_/proxy/tenants/testlib/modules
```

## Using the module

```
curl -w '\n' -H "X-Okapi-Tenant: testlib" http://localhost:9130/hello
```

Or you can post any valid JSON, for example our enabling request:

```
curl -w '\n' -X POST -D -   \
    -H "Content-type: application/json"   \
    -H "X-Okapi-Tenant: testlib" \
    -d @TenantModuleDescriptor.json \
    http://localhost:9130/hello
```

## Restrict access

Follow the
[auth-module](https://github.com/folio-org/okapi/blob/master/doc/guide.md#example-2-adding-the-auth-module)
section of the Okapi Guide and Reference.

## Cleaning up

```
curl -w '\n' -X DELETE  -D -    http://localhost:9130/_/proxy/tenants/testlib/modules/hello

curl -w '\n' -X DELETE  -D -    http://localhost:9130/_/discovery/modules/hello/localhost-9131

curl -w '\n' -X DELETE  -D -    http://localhost:9130/_/proxy/modules/hello
```

and Okapi would have also removed the Docker container for that module.

Finally you can stop the Okapi, with a Ctrl-C in its terminal window.

## Scripting it
There is a little shell script to go through the setting up, as above. It is
called 'runhello.sh'.

## What next

As such this module is pretty useless. You can use it to understand what a FOLIO
module is, and as an example, or even as a starting point, for creating your own
modules. You may want to look at the slightly more complex module called
`simple-vertx`, and maybe reread the Okapi Guide.
