package com.patrickmcgeever.clusterexampleserver

import akka.actor.{Actor, ActorLogging, ActorPath, ActorRef, ActorSystem, Props, Terminated}
import akka.cluster.client._
import com.patrickmcgeever.clusterexamplecommon.messages.{BackendRegistration, JobFailed, TransformationResult, WorkerJob}
import com.typesafe.config.ConfigFactory
import scala.collection.JavaConverters._

//class ClusterExampleServer extends Actor with ActorLogging {
//  var backends = IndexedSeq.empty[ActorRef]
//  var jobCounter = 0
//
//  def receive = {
//    case job@WorkerJob(_) if backends.isEmpty =>
//      sender() ! JobFailed("Service unavailable, try again later", job)
//
//    case job@WorkerJob(_) =>
//      jobCounter += 1
//      backends(jobCounter % backends.size) forward job
//
//    case TransformationResult(originalNum, result) => log.info(s"Result for $originalNum is $result")
//
//    case BackendRegistration if !backends.contains(sender()) =>
//      context watch sender()
//      backends = backends :+ sender()
//
//    case Terminated(a) =>
//      backends = backends.filterNot(_ == a)
//
//    case _ => log.info("I got some other message message!")
//  }
//}

//class ClusterClientListener(ref: ActorRef)(targetClient: ActorRef) extends Actor with ActorLogging {
  class ClusterClientListener(targetClient: ActorRef) extends Actor with ActorLogging {
    override def preStart(): Unit =
    targetClient ! SubscribeContactPoints

  def receive: Receive =
    receiveWithContactPoints(Set.empty)

  def receiveWithContactPoints(contactPoints: Set[ActorPath]): Receive = {
    case ContactPoints(cps) =>
      log.info(s"Received contact points: ${cps.seq.mkString(", ")}")
      context.become(receiveWithContactPoints(cps))
    // Now do something with the up-to-date "cps"
    case ContactPointAdded(cp) =>
      log.info(s"Contact point added: ${cp}")
      context.become(receiveWithContactPoints(contactPoints + cp))
    // Now do something with an up-to-date "contactPoints + cp"
    case ContactPointRemoved(cp) =>
      log.info(s"Contact point removed: ${cp}")
      context.become(receiveWithContactPoints(contactPoints - cp))
    // Now do something with an up-to-date "contactPoints - cp"
  }
}

//class ReceptionistListener(targetReceptionist: ActorRef) extends Actor with ActorLogging {
//  override def preStart(): Unit =
//    targetReceptionist ! SubscribeClusterClients
//
//  def receive: Receive =
//    receiveWithClusterClients(Set.empty)
//
//  def receiveWithClusterClients(clusterClients: Set[ActorRef]): Receive = {
//    case ClusterClients(cs) =>
//      log.info(s"Received cluster clients: ${cs.seq.map(_.path.address.toString).mkString("\n")}")
//      context.become(receiveWithClusterClients(cs))
//    // Now do something with the up-to-date "c"
//    case ClusterClientUp(c) =>
//      log.info(s"Cluster client up: ${c.path.address.toString}")
//      context.become(receiveWithClusterClients(clusterClients + c))
//    // Now do something with an up-to-date "clusterClients + c"
//    case ClusterClientUnreachable(c) =>
//      log.info(s"Cluster client un-reachable: ${c.path.address.toString}")
//      context.become(receiveWithClusterClients(clusterClients - c))
//    // Now do something with an up-to-date "clusterClients - c"
//  }
//}

object ClusterExampleServer {

  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.load()
    val system = ActorSystem("ClusterSystem", config)

    val seed = config.getStringList("akka.cluster.client.initial-contacts").asScala.head
//    val seedActorRef = system.actorSelection(seed).anchor

    val clusterClient = system.actorOf(ClusterClient.props(ClusterClientSettings(system)), "client")
    system.actorOf(Props(new ClusterClientListener(clusterClient)), name = "exampleServer")

//    val nums = Seq(
//      500000,
//      600000,
//      700000,
//      800000
//    )
//
//    nums.foreach { num =>
//      frontend ! WorkerJob(num)
//    }
  }
}
