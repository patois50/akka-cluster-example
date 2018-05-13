# TODO
* Add actual clustering code to worker and code to send job to cluster from server
* set up docker swarm to launch these docker containers

## Notes
* If you are packaging the app inside docker rpm etc then you do not have to use the assembly plugin, the JavaAppPackager plugin will do the job, it creates a launch script in bin/ your jar and dep jars in lib/

## Useful links
https://doc.akka.io/docs/akka/current/cluster-usage.html?language=scala
http://blog.michaelhamrah.com/2014/03/running-an-akka-cluster-with-docker-containers/
