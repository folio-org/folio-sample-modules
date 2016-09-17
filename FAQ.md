# Frequently asked questions

## Docker Toolbox and localhost ports

For development machines using "Docker Toolbox" (instead of native)
there are some operational tweaks required.
Some ports need to be forwarded from VirtualBox (the VM provided with
Docker Toolbox).
Either use the VirtualBox Manager GUI (see some [tips](http://stackoverflow.com/a/36458215)
in response to a good explanation)
or use something like this
[script](http://code.haleby.se/2016/04/08/docker-machine-port-forwarding/).
Add a permanent rule for port 8080. However some ports need to be configured
while live. Start okapi as normal, and run its 'doc/okapi-examples.sh' which
will deploy two modules (on port 9131 and 9132). Now deploy the "hello-vertx"
module - this one is via docker. The okapi log will show it being deployed
on port 9133 and hang. Now add a rule for port 9133, and the log will show
Okapi continue to complete the deployment.
Remember to remove that rule to be able to re-deploy.
