#!/bin/bash
#
# A simple script to run the 'simple-perl' sample module.
# Assumes you have started Okapi in another terminal window
# with something like:
#  export HOST=`hostname`
#  java -Dloglevel=DEBUG \
#    -Dokapiurl="http://$HOSTNAME:9130" \
#    -jar okapi-core/target/okapi-core-fat.jar dev

OKAPI=${1:-"http://localhost:9130"} # The usual place it runs on a single-machine setup
SLEEP=${2:-"0.2"} # Time to sleep between requests
DEPLOY=${3:-"DeploymentDescriptor-exec.json"} # Which Deployment to use

#echo
#echo "Dockerizing it"
#docker build -t folio-simple-perl-module .
#echo OK

echo "Check that Okapi is running ..."
curl -w '\n' $OKAPI/_/discovery/nodes || exit 1
echo "OK"
sleep $SLEEP

echo
echo "Creating a tenant"

curl -w '\n' -X POST -D - \
  -H "Content-type: application/json" \
  -d @TenantDescriptor.json  \
  $OKAPI/_/proxy/tenants || exit 1
echo OK
sleep $SLEEP

echo
echo "Declaring the simple module"

curl -w '\n' -X POST -D - \
  -H "Content-type: application/json" \
  -d @ModuleDescriptor.json \
  $OKAPI/_/proxy/modules || exit 1
echo OK
sleep $SLEEP

echo
echo "Deploying it on localhost"
curl -w '\n' -X POST -D - \
  -H "Content-type: application/json" \
  -d @$DEPLOY  \
  $OKAPI/_/discovery/modules || exit 1
echo OK
sleep $SLEEP

echo
echo "Enabling it for our tenant"
curl -w '\n' -X POST -D - \
  -H "Content-type: application/json" \
  -d @TenantModuleDescriptor.json \
  $OKAPI/_/proxy/tenants/testlib/modules || exit 1
echo OK
sleep $SLEEP

echo "Checking that it works"
curl -w '\n' -D - -H "X-Okapi-Tenant: testlib" \
  $OKAPI/hello || exit 1
echo OK
sleep $SLEEP

echo
echo "Checking a POST request "
curl -w '\n' -X POST -D - \
  -H "Content-type: application/json" \
  -H "X-Okapi-Tenant: testlib" \
  -d @TenantModuleDescriptor.json  \
  $OKAPI/hello || exit 1
echo OK

echo "Making a request to /simple, which will call /hello"
curl -w '\n' -D - -H "X-Okapi-Tenant: testlib" \
  $OKAPI/simple || exit 1
echo OK
sleep $SLEEP
