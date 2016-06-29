# Hello-vertx

A minimal "Hello, world" module written in Java, using the vert.x framework,
the same tools as we use for developing Okapi core.

There are two source files:
 - MainLauncher.java, which is a bit of template code to launch vert.x
 - MainVerticle.java, which is the "main program", a HTTP server that serves
   our requests.

Other noteworthy files are:
 - Dockerfile: Docker setup
 - pom.xml: Maven config on how to build the project
 - log4j.properties: where you can control the logging

