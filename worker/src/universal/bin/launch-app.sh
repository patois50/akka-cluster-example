#!/usr/bin/env sh

if [ -z "$CLUSTER_IP" ]; then
    CLUSTER_IP=$(/sbin/ifconfig eth0 | grep 'inet addr:' | cut -d: -f2 | awk '{ print $1}')
fi

if [ -z "$1" ]; then
    exec java -jar /opt/cluster-example/lib/cluster-example-worker.jar
else
    exec "$@"
fi
