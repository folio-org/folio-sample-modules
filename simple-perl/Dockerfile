###
# Perl-based Okapi module docker
# To build:
#   docker build -t folio-simple-perl-module .
# To run:
#   docker run -t -i -p 8080:8080 folio-simple-perl-module
###

FROM alpine

# Install latest patch versions of packages: https://pythonspeed.com/articles/security-updates-in-docker/
RUN apk upgrade \
 && apk add \
      perl perl-net-server perl-json perl-cgi perl-lwp-useragent-determined \
 && rm -rf /var/cache/apk/*

# or:
#FROM perl:slim
## Install latest patch versions of packages: https://pythonspeed.com/articles/security-updates-in-docker/
#RUN apt-get update && apt-get -y upgrade && apt-get -y install libnet-server-perl libjson-perl libcgi-pm-perl libmodule-build-perl libwww-perl
#ENV PERL5LIB /usr/share/perl5

# Set the location of the script
ENV PERL_HOME /usr/okapi

EXPOSE 8080

# Copy your fat jar to the container
COPY ./simple.pl $PERL_HOME/

# Launch the module
WORKDIR $PERL_HOME
ENTRYPOINT ["perl", "simple.pl"]
