#!/bin/bash
#
# A simple script to run the 'simple-vertx' sample module.
# Assumes you have compiled the module, and started Okapi in another
# terminal window, and loaded it up with the tenant and hello module,
# for example by running:
# ../hello-vertx/runhello.sh

OKAPI=${1:-"http://localhost:9130"} # The usual place it runs on a single-machine setup
SLEEP=${2:-"1"} # Time to sleep between requests

# curl that fails if HTTP response code is not 2xx
curlf ()
{
  # -w\n to output a newline at the end
  # -s -S to show error messages, but no progress meter
  # -D - to show headers
  OUT=$(curl -w"\n" -s -S -D - "$@")
  echo "$OUT"
  # success if HTTP status code from range 200-299 in the first line
  echo "$OUT" | head -1 | grep --quiet '\b2[0-9][0-9]\b'
}

echo "Compiling the simple module"
mvn install || exit 1
echo OK

echo
echo "Dockerizing it"
docker build -t folio-simple-module . || exit 1
echo OK

echo
echo "Check that Okapi is running ..."
curlf $OKAPI/_/discovery/nodes || exit 1
echo "OK"
sleep $SLEEP

echo
echo "Declaring the simple module"

curlf -X POST \
    -H "Content-type: application/json" \
    -d @target/ModuleDescriptor.json \
    $OKAPI/_/proxy/modules || exit 1
echo OK
sleep $SLEEP

echo
echo "Deploying it on localhost"
curlf -X POST  \
  -H "Content-type: application/json" \
  -d @target/DeploymentDescriptor.json  \
  $OKAPI/_/discovery/modules || exit 1
echo OK
sleep $SLEEP

echo
echo "Enabling it for our tenant"
curlf -X POST \
  -H "Content-type: application/json" \
  -d @target/TenantModuleDescriptor.json \
  $OKAPI/_/proxy/tenants/testlib/modules || exit 1
echo OK
sleep $SLEEP

echo
echo "Checking that it works"
curlf -H "X-Okapi-Tenant: testlib" $OKAPI/simple || exit 1
echo OK
sleep $SLEEP

echo
echo "Checking a POST request "
curlf -X POST \
  -H "Content-type: application/json" \
  -H "X-Okapi-Tenant: testlib" \
  -d @target/TenantModuleDescriptor.json  \
  $OKAPI/simple || exit 1
echo OK
