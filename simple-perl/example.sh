#! /bin/bash
#
# A simple script to run the 'simple-perl' sample module
# Assumes you have started Okapi in another temrinal window
# with something like
#  export HOST=`hostname`
#  java -Dloglevel=DEBUG \
#    -Dokapiurl="http://$HOSTNAME:9130" \
#    -jar okapi-core/target/okapi-core-fat.jar dev

OKAPI=${1:-"http://localhost:9130"}   # The usual place it runs on a single-machine setup
SLEEP=${2:-"0.2"} # Time to sleep between requests


#echo
#echo "Dockerixing it"
#docker build -t indexdata/folio-simple-module . || exit 1
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
  http://localhost:9130/_/proxy/tenants || exit 1
echo OK
sleep $SLEEP


echo
echo "Declaring the simple module"

curl -w '\n' -X POST -D -   \
   -H "Content-type: application/json"   \
   -d @ModuleDescriptor.json \
   http://localhost:9130/_/proxy/modules || exit 1
echo OK
sleep $SLEEP

echo
echo "Deploying it on localhost"
curl -w '\n' -X POST -D - \
  -H "Content-type: application/json" \
  -d @DeploymentDescriptor.json  \
  http://localhost:9130/_/discovery/modules || exit 1
echo OK
sleep $SLEEP

echo
echo "Enabling it for our tenant"
curl -w '\n' -X POST -D -   \
    -H "Content-type: application/json"   \
    -d @TenantModuleDescriptor.json \
    http://localhost:9130/_/proxy/tenants/testlib/modules || exit 1
echo OK
sleep $SLEEP

echo "Checking that it works"
curl -w '\n' -D - -H "X-Okapi-Tenant: testlib" \
  http://localhost:9130/hello?p=x || exit 1
echo OK
sleep $SLEEP

echo
echo "Checking a POST request "
curl -w '\n' -X POST -D - \
   -H "Content-type: application/json"  \
   -H "X-Okapi-Tenant: testlib" \
   -d @TenantModuleDescriptor.json  \
   http://localhost:9130/hello?p=y || exit 1
echo OK








