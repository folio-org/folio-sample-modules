## Example Okapi Module in NodeJS Express

This is a sample module that provides CRUD on simple "Thing" objects for Okapi. It includes a ModuleDescriptor, and a Deployment Descriptor desgined to be used when the module has been started independently and is running on port 3000. Okapi is expected to be running on port 9130, on the IP address 192.168.0.2.


### Install vagrant stable box
### Start the vagrant stable box running
### Install the Thing module (only needed once)
```
npm install
```
### Start the Thing module
```
npm start
```
### Register the running Thing module with the running Okapi
```
curl -w '\n' -X POST -H "Content-Type:application/json" -d @./ModuleDescriptor.json http://192.168.0.2:9130/_/proxy/modules
curl -i -w '\n' -X POST -H "Content-Type:application/json" -d @./ExternalDeploymentDescriptor.json http://192.168.0.2:9130/_/discovery/modules
curl -i -w '\n' -X POST -H "Content-Type:application/json" -d @./TenantAssociationDescriptor.json http://192.168.0.2:9130/_/proxy/tenants/diku/modules
```


