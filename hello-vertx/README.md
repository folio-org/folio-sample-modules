# Hello-vertx

A minimal "Hello, world" module written in Java, using the vert.x framework,
the same tools as we use for developing Okapi core.


## Files

There are two source files:
 - MainLauncher.java, which is a bit of template code to launch vert.x
 - MainVerticle.java, which is the "main program", a HTTP server that serves
   our requests.

Other noteworthy files are:
 - Dockerfile: Docker setup
 - pom.xml: Maven config on how to build the project
 - log4j.properties: where you can control the logging


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
   curl http://localhost/hello
```

## Installing the module

First of all you need a running Okapi instance.
```
   cd .../okapi
   java -jar okapi-core/target/okapi-core-fat.jar dev
```

If you have no tenants defined, you need to add one
```
cat > /tmp/tenant1.json <<END
{
  "id" : "our",
  "name" : "our library",
  "description" : "Our Own Library"
}
END

curl -w '\n' -X POST -D - \
  -H "Content-type: application/json" \
  -d @/tmp/tenant1.json  \
  http://localhost:9130/_/proxy/tenants
```

Next we need to deploy the module. There is a deploymentDescriptor in `deploy.json`
```
curl -w '\n' -X POST -D - \
  -H "Content-type: application/json" \
  -d @deploy.json  \
  http://localhost:9130/_/deployment/modules

```

After that we need to declare the module and its stuff.
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
