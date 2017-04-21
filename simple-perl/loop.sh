#!/bin/bash
#
# Simple script to make many requests to the /simple service of the perl sample module
# This can be used to check if Okapi can do serious amounts of recursive calls,
# or just how it behaves under load

# Start Okapi up first.

DIR=/tmp/simple-perl-loop

STOP=$DIR/stoploop

rm -rf $DIR
mkdir -p $DIR

# Run tyhe example.sh to load our module, tenant, etc
echo "Setting up with example.sh"
./example.sh > $DIR/example.out 2>&1 || exit 1
echo "Starting $N clients"

for C in {1..20}   ## Edit number of clients here
do
  OUT=`mktemp -p $DIR`
  echo "Client $C writes to $OUT"
  (
    while [ ! -f $STOP ] &&
      curl -f -s -S \
        -H "X-Okapi-Tenant: testlib"  \
        http://localhost:9130/simple
    do
      echo -n `date`
      sleep 0.01
    done
    echo `date` "Stopped client $C" >> $STOP
    echo
    echo `date` "Done"
  ) >> $OUT 2>&1 &
done

wait
echo All clients stopped
