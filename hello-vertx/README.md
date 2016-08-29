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
   docker build -t indexdata/folio-hello-module .
```

Test that it runs with:

```
   docker run -t -i -p 8080:8080 indexdata/folio-hello-module
```

And check it in another window:

```
   curl http://localhost:8080/hello
```

## Installing the module

First of all you need a running Okapi instance:

```
   cd .../okapi
   java -jar okapi-core/target/okapi-core-fat.jar dev
```

If you have no tenants defined, then you need to add one:

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


Next we need to deploy the module. There is a deployment descriptor in `DeploymentDescriptor.json`

```
curl -w '\n' -X POST -D - \
  -H "Content-type: application/json" \
  -d @DeploymentDescriptor.json  \
  http://localhost:9130/_/deployment/modules
```

Now the module should be running on the next available port.
That will be 9131 if using the simple instructions above (or 9133 if using
okapi/doc/okapi-examples.sh to establish an initial set of test tenants and
test modules).

Check the running module:

```
curl -w '\n' -D - http://localhost:9131/hello
```


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

## Cleaning up

```
curl -w '\n' -X DELETE  -D -    http://localhost:9130/_/proxy/tenants/testlib/modules/hello

curl -w '\n' -X DELETE  -D -    http://localhost:9130/_/proxy/modules/hello

curl -w '\n' -X DELETE  -D -    http://localhost:9130/_/deployment/modules/localhost-9131
```

Finally you can stop the Okapi, with a Ctrl-C in its terminal window.


## What next

As such this module is pretty useless. You can use it to understand what a Folio
module is, and as an example, or even as a starting point, for creating your own
modules.
