# hello-spring

## Introduction

A minimal "Hello, world" FOLIO module written in Java, inspired by the `hello-vertx` example.
However, this example uses the Spring framework and OpenAPI. This module is intended as a minimal
use case of these technologies as they are used in FOLIO modules.

The API is exactly that of `hello-vertx`, with the small caveat that it validates the JSON that is
posted in the request. To see the API specification, refer to
[api.yaml](src/main/resources/api.yaml).

## Files

There are two source files in this project:
[HelloController.java](src/main/java/org/folio/hello/HelloController.java), which implements the API
specified by the OpenAPI specification, and
[HelloApplication.java](src/main/java/org/folio/hello/HelloApplication.java), which starts up the
Spring Boot Application.

Other notable files in this project include:

- [README.md](README.md), this file;
- [pom.xml](pom.xml), the Maven project config;
- [api.yaml](src/main/resources/api.yaml), the API specification;
- and both [descriptors/ModuleDescriptor-template.json](descriptors/ModuleDescriptor-template.json)
  and
  [descriptors/DeploymentDescriptor-template.json](descriptors/DeploymentDescriptor-template.json),
  which generate the descriptor files needed by Okapi.

## Compiling

```sh
mvn clean install
```

Should report a `BUILD SUCCESS` at the end.

## Running Locally

Despite not needing to actually access a PostgreSQL database, `folio-spring-base` has it as a
dependency, so in order for it to run properly, you must have the variables specified in
[descriptors/ModuleDescriptor-template.json](descriptors/ModuleDescriptor-template.json) set in your
environment to connect to a running PostgreSQL database. Then you can run

```sh
mvn spring-boot:run
```

To kick things off.

## Runing module in Okapi / FOLIO

The following will assume that you have a Okapi / FOLIO instance running (e.g. the `folio/testing`
Vagrantbox). Additionally, it will assume that you are located inside in the root of this project.

### Build module in Docker

Once the module is compiled, build the docker container inside the instance with

```sh
docker build -t docker.ci.folio.org/folio-hello-spring .
```

### Declare module

The following will declare the module to the running Okapi / FOLIO instance:

```sh
curl -w '\n' -X POST -D - \
  -H "Content-type: application/json" \
  -d @target/ModuleDescriptor.json \
  http://localhost:9130/_/proxy/modules
```

### Deploy module

Before the module can be deployed, the name of the node must be known. The following will find it
(usually `localhost` or, on Vagrant, `10.0.2.15`):

```sh
curl -w '\n' -X GET \
  http://localhost:9130/_/discovery/nodes
```

Once this is placed into the generated `target/DeploymentDescriptor.json`, we can deploy it onto the
node:

```sh
curl -w '\n' -X POST -D - \
  -H "Content-type: application/json" \
  -d @target/DeploymentDescriptor.json \
  http://localhost:9130/_/discovery/modules
```

If this fails, this means there was an issue deploying the module. You can examine the logs using
`docker logs --follow -n 100 CONTAINER_ID`, where the container ID is found with `docker ps`. Please
note that only the startup of the module will be in the main Okapi logs; after this, the module logs
are only found in its container.

### Enable module on tenant

Assuming tenant `diku`, the module is enabled on the tenant with:

```sh
curl -w '\n' -X POST -D - \
  -H "Content-type: application/json" \
  -d "[{"id":"folio-hello-spring-0.0.1-SNAPSHOT","action":"enable"}]" \
  http://localhost:9130/_/proxy/tenants/diku/install
```
