###
# vert.x docker example using a Java verticle packaged as a fatjar
# To build:
#   docker build -t folio-hello-module .
# To run:
#   docker run -t -i -p 8080:8080 folio-hello-module
###

FROM folioci/alpine-jre-openjdk17:latest

# Install latest patch versions of packages: https://pythonspeed.com/articles/security-updates-in-docker/
USER root
RUN apk upgrade --no-cache
USER folio

ENV VERTICLE_FILE folio-hello-vertx-fat.jar

# Set the location of the verticles
ENV VERTICLE_HOME /usr/verticles

# Copy your fat jar to the container
COPY target/${VERTICLE_FILE} ${VERTICLE_HOME}/${VERTICLE_FILE}

# Expose this port locally in the container.
EXPOSE 8080
