#!/bin/bash
#
# A simple script to run the 'simple-vertx' sample module.
# Assumes you have compiled the module, and started Okapi in another
# terminal window, and loaded it up with the tenant and hello module,
# for example by running:
# ../hello-vertx/runhello.sh

OKAPI=${1:-"http://localhost:9130"}   # The usual place it runs on a single-machine setup
SLEEP=${2:-"1"} # Time to sleep between requests
CURLOPTS="-w\n -D - "   # -w to output a newline after, -D - to show headers

echo "Compiling the simple module"
mvn install || exit 1
echo OK

echo
echo "Dockerizing it"
docker build -t folio-simple-module . || exit 1
echo OK


echo "Check that Okapi is running ..."
curl $CURLOPTS $OKAPI/_/discovery/nodes || exit 1
echo "OK"
sleep $SLEEP

echo
echo "Declaring the simple module"

curl $CURLOPTS -X POST \
   -H "Content-type: application/json"   \
   -d @ModuleDescriptor.json \
   $OKAPI/_/proxy/modules || exit 1
echo OK
sleep $SLEEP

echo
echo "Deploying it on localhost"
curl $CURLOPTS -X POST  \
  -H "Content-type: application/json" \
  -d @DeploymentDescriptor.json  \
  $OKAPI/_/discovery/modules || exit 1
echo OK
sleep $SLEEP

echo
echo "Enabling it for our tenant"
curl $CURLOPTS -X POST \
    -H "Content-type: application/json"   \
    -d @TenantModuleDescriptor.json \
    $OKAPI/_/proxy/tenants/testlib/modules || exit 1
echo OK
sleep $SLEEP

echo "Checking that it works"
curl $CURLOPTS -H "X-Okapi-Tenant: testlib" $OKAPI/simple || exit 1
echo OK
sleep $SLEEP

echo
echo "Checking a POST request "
curl $CURLOPTS -X POST \
   -H "Content-type: application/json"  \
   -H "X-Okapi-Tenant: testlib" \
   -d @TenantModuleDescriptor.json  \
   $OKAPI/simple || exit 1
echo OK

