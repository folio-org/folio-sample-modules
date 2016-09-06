# Simple-perl

A fairly small module written in Perl, just to show that modules can be
written in other languages.

This module is very similar to the `simple-vertx` module, it supports GET and
POST requests to `/hello`.

This module support the same GET and POST requests to `/hello`, as the
`hello-vertx` module. It also supports the same GET and POST requests to
`/simple` as the `simple-vertx` module. These requests make a call to `/hello`
to demonstrate how such calls can be made in Perl. Of course those calls could
go to any other module, but we use the same module here to keep things simple.



## Files

There is one source file: simple.pl

There are JSON structures, ready to POST to Okapi for setting up a demonstration
of this module:

 - ModuleDescriptor.json: A module description for the module.
 - DeploymentDescriptor-url.json: to tell Okapi that the server is already running at a URL
 - DeploymentDescriptor-exec.json: to tell Okapi to deploy the module directly
 - DeploymentDescriptor-docker.json: to tell Okapi to deploy the module as a Docker container
 - TenantModuleDescriptor.json: A small structure to enable the module for our test tenant.

Other useful files include:
 - README.md: This file, explaining what is where and how to use the module.
 - example.sh: A script that sets things up in Okapi and invokes the module.
 - Dockerfile: For packing the module in a docker.

## Overview

The module is a HTTP server, based on Net::Server::HTTP running in a preforked
configuration, as is its default. It serves GET and POST requests to `/hello`
and `/simple`. The get requests return plain text messages, and the POST
requests expect and return JSON structures.

There are a few details worth noticing:

* Okapi uses chunked transfer encoding, so the module has to be able to read
chunks of input data. There is a helper function for that.
* The requests come in to `process_http_request()` It responds directly to
a GET request to `/hello` and dispatches the rest to individual handlers.
* There are some helpers to produce HTTP responses, to read the POSTed data,
and to create HTTP requests.
* The actual handler functions are pretty short.


## Dependencies

The module uses some Perl libraries:
  * Net::Server::HTTP
  * JSON
  * Data::Dumper
  * CGI
  * LWP::UserAgent

On a Debian based system they all can be installed with
```
sudo apt-get install libnet-server-perl libjson-perl \
    libcgi-pm-perl libmodule-build-perl libwww-perl
```
<!-- TODO - How to install the perl modules on other platforms, like Windows -->
Because Okapi prefers to use chunked encoding, the `HTTP::Server::Simple::CGI`
module is not suitable for building modules.


## Setting things up

First of all you need a running Okapi instance, probably in a separate terminal
window.
<!-- Omitted, until the module will actually do a callback to Okapi!
There is one catch, Okapi needs to tell the modules how to contact it
back. Since the module run in a docker container, it can not refer to Okapi
by its default address at `localhost` as that would refer to the thing inside
the docker container. Instead we need to use the correct machine name. -->

```
   cd .../okapi
   export HOST=`hostname`
   java  \
    -Dokapiurl="http://$HOSTNAME:9130" \
    -Dloglevel=DEBUG \
    -jar okapi-core/target/okapi-core-fat.jar dev
```

You also need to define a tenant, who will be using this module later.

```
 curl -w '\n' -X POST -D - \
  -H "Content-type: application/json" \
  -d @TenantDescriptor.json  \
  http://localhost:9130/_/proxy/tenants || exit 1
```

We need to declare the module for Okapi, by posting a `ModuleDescriptor` to
`/_/proxy/modules`:

```
curl -w '\n' -X POST -D -   \
   -H "Content-type: application/json"   \
   -d @ModuleDescriptor.json \
   http://localhost:9130/_/proxy/modules || exit 1
```

We also need to enable the module for our tenant:

```
curl -w '\n' -X POST -D -   \
  -H "Content-type: application/json"   \
  -d @TenantModuleDescriptor.json \
  http://localhost:9130/_/proxy/tenants/testlib/modules || exit 1
```

## Running the module separately

To begin with, it is probably easier to run the module in its own terminal
window, where you can see what it outputs to STDERR.

```
./simple.pl
```

It produces some output about the type of the server, what port it listens on
(it defaults to 8080), and which user and group it is running under.

Now you can verify that it is running. In yet another console window try:

```
curl -D - -w '\n' http://localhost:8080/hello
```

And you should see the traditional greeting.

You can also try to POST some JSON data to it. Being too lazy to create a new
structure, we just reuse the TenantDescriptor.

```
curl -D - -w '\n' \
  -X POST -d@TenantDescriptor.json   \
  -H "Content-type: application/json" \
  -H "X-Okapi-Tenant: testlib"  \
   http://localhost:8080/hello
```

You should see that out comes a different JSON structure that incudes a field
called "greeting".

## Telling Okapi to use the module

Now we need to tell Okapi that we have the module running on port 8080. This is
done by posting a DeploymentDescriptor to /_/discovery/modules. The file
DeploymentDescriptor-url.json contains one that does actually not deploy anything,
but tells Okapi that we have one already deployed. Post it to Okapi:

```
curl -w '\n' -X POST -D - \
  -H "Content-type: application/json" \
  -d @DeploymentDescriptor-url.json  \
  http://localhost:9130/_/discovery/modules || exit 1
```

Now you can invoke the module through Okapi. Note that we need to pass a tenant
in the headers.

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

If you want to play with the module, you can edit the script, kill the module
with a Ctrl-C, and restart it, and issue the same requests to Okapi.

## Running the module inside Okapi

If you just need to use the module, for example when working on another one,
it will be easier if you let Okapi start it up for you.  We can do this by
using the provided DeploymentDescriptor-exec.json, which has a launchDescriptor
included that tells how to start the file.

There is one catch. Okapi needs to know the path to the script, and that needs
to be relative to the directory where Okapi itself is running. If you have
checked okapi and folio-sample-modules out at the same place, this will work.
Otherwise you will need to edit the path to the script.

```
curl -w '\n' -X POST -D - \
  -H "Content-type: application/json" \
  -d @DeploymentDescriptor-exec.json  \
  http://localhost:9130/_/discovery/modules || exit 1
```

Okapi starts the module, and assigns it a port to listen on. Typically this
would be 9131. You can check it running on that port, or just use it through
Okapi as before.

## Running in a docker

For production, it is preferable to run modules inside Docker containers.
There is a `Dockerfile` ready to go, all you need to do is to build the
Docker image:

```
docker build -t indexdata/folio-simple-perl-module .
```

To use the image, DeploymentDescriptor-docker.json is all set up with the right
command lines. Just POST that to /_/discovery, like before.

```
curl -w '\n' -X POST -D - \
  -H "Content-type: application/json" \
  -d @DeploymentDescriptor-docker.json  \
  http://localhost:9130/_/discovery/modules || exit 1
```

Now you should be able to use the module as before.

## What next

As such this module is pretty useless. You can use it to understand what a FOLIO
module is, and as an example, or even as a starting point, for creating your own
modules.
