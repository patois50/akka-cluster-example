#!/usr/bin/env sh

if [ -z "$1" ]; then
    exec java -jar /opt/cluster-example/lib/cluster-example-server.jar
else
    exec "$@"
fi
