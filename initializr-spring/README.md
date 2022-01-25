# initializr-spring
## Introduction

The [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/) Spring framework tutorial, ported such that it can be run in as a FOLIO module.

This example is intended as a demonstration of what is needed to transform a project created by [Spring Initializr](https://start.spring.io/) into something using FOLIO's frameworks.

The functionality of this example is similar to `hello-vertx` in theme:

The module supports a `GET` request to `/greeting`, which returns the traditional greeting with an incrementing id in JSON. If provided with a `name` parameter in the request, it will instead greet with that.

## Creation

This section documents the process of first creating the project and then transforming it into a project that conforms with observed FOLIO standards.

### Initialization

The project was initialized with [Spring Initializr](https://start.spring.io/) using the following settings:

- Project: Maven Project
- Language: Java
- Spring Boot: 2.6.2
- Project Metadata:
  - Group: `org.folio`
  - Artifact: `initalizr-spring`
  - Name: `initializr-spring`
  - Description: `Hello world demo project for FOLIO, using Spring`
  - Package name: `org.folio.initalizr-spring` (transformed into `org.folio.initalizrspring`)
  - Packaging: Jar
  - Java: 11
- Dependencies:
  - Spring Web

The following link will open Spring Initalizr with the above settings: [Configured Spring Initalizr](https://start.spring.io/#!type=maven-project&language=java&platformVersion=2.6.2&packaging=jar&jvmVersion=11&groupId=org.folio&artifactId=initializr-spring&name=initializr-spring&description=Hello%20world%20demo%20project%20for%20FOLIO%2C%20using%20Spring&packageName=org.folio.initializr-spring&dependencies=web).

The files `src\main\java\org\folio\initializrspring\Greeting.java` and `src\main\java\org\folio\initializrspring\GreetingController.java` were then filled out as described in the tutorial.

As it was not needed, the test file was deleted.

### Modifying the example to fit to FOLIO

#### Minimal Changes

`pom.xml`:
1. Add FOLIO's Maven repositories (`<dependencies>`, `<distributionManagement>`)
2. Add `folio-spring-base` to the dependencies
3. Remove the `spring-boot-starter-*` dependencies, as they are transitively pulled in by `folio-spring-base`

`application.yaml`:
1. Add `spring.application.name`
2. Add Postgres datasource
3. Add FOLIO banner (optional, but nice)
4. Add management endpoint
