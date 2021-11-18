#!/bin/bash
#
# A simple script to run the 'simple-perl' sample module.
# Assumes you have started Okapi in another terminal window
# with something like:
#  export OKAPIHOST=`hostname`
#  java -Dloglevel=DEBUG \
#    -Dokapiurl="http://$OKAPIHOST:9130" \
#    -jar okapi-core/target/okapi-core-fat.jar dev

OKAPI=${1:-"http://localhost:9130"} # The usual place it runs on a single-machine setup
SLEEP=${2:-"0.2"} # Time to sleep between requests
DEPLOY=${3:-"DeploymentDescriptor-exec.json"} # Which Deployment to use

# curl that fails if HTTP response code is not 2xx
curlf ()
{
  # -w"\n" to output a newline at the end
  # -s -S to show error messages, but no progress meter
  # -D - to show headers
  OUT=$(curl -w"\n" -s -S -D - "$@")
  echo "$OUT"
  # success if HTTP status code from range 200-299 in the first line
  echo "$OUT" | head -1 | grep --quiet '\b2[0-9][0-9]\b'
}

#echo
#echo "Dockerizing it"
#docker build -t folio-simple-perl-module .
#echo OK

echo "Check that Okapi is running ..."
curlf $OKAPI/_/discovery/nodes || exit 1
echo "OK"
sleep $SLEEP

echo
echo "Creating a tenant"
curlf -X POST \
  -H "Content-type: application/json" \
  -d @TenantDescriptor.json  \
  $OKAPI/_/proxy/tenants || exit 1
echo OK
sleep $SLEEP

echo
echo "Declaring the simple module"

curlf -X POST \
  -H "Content-type: application/json" \
  -d @ModuleDescriptor.json \
  $OKAPI/_/proxy/modules || exit 1
echo OK
sleep $SLEEP

echo
echo "Deploying it on localhost"
curlf -X POST \
  -H "Content-type: application/json" \
  -d @$DEPLOY  \
  $OKAPI/_/discovery/modules || exit 1
echo OK
sleep $SLEEP

echo
echo "Enabling it for our tenant"
curlf -X POST \
  -H "Content-type: application/json" \
  -d @TenantModuleDescriptor.json \
  $OKAPI/_/proxy/tenants/testlib/modules || exit 1
echo OK
sleep $SLEEP

echo
echo "Checking that it works"
curlf -H "X-Okapi-Tenant: testlib" \
  $OKAPI/hello || exit 1
echo OK
sleep $SLEEP

echo
echo "Checking a POST request"
curlf -X POST \
  -H "Content-type: application/json" \
  -H "X-Okapi-Tenant: testlib" \
  -d @TenantModuleDescriptor.json \
  $OKAPI/hello || exit 1
echo OK

echo
echo "Making a request to /simple, which will call /hello"
curlf -H "X-Okapi-Tenant: testlib" \
  $OKAPI/simple || exit 1
echo OK
sleep $SLEEP

