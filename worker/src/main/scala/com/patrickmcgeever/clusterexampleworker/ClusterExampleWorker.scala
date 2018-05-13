package com.patrickmcgeever.clusterexampleworker

import language.postfixOps
import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import com.patrickmcgeever.clusterexamplecommon.messages.WorkerJob
import com.typesafe.config.ConfigFactory
import ClusterExampleWorker._

//#backend
class ClusterExampleWorker extends Actor with ActorLogging {

  val cluster = Cluster(context.system)

  // subscribe to cluster changes
  // re-subscribe when restart
  override def preStart(): Unit = {
    log.debug("Starting up cluster listener...")
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents, classOf[ClusterDomainEvent])
  }

  override def postStop(): Unit = cluster.unsubscribe(self)

//  def receive = {
//    case TransformationJob(text) => sender() ! TransformationResult(text.toUpperCase)
//    case state: CurrentClusterState =>
//      state.members.filter(_.status == MemberStatus.Up) foreach register
//    case MemberUp(member) =>
//      log.info("Member is Up: {}", member.address)
//      register(member)
//    case UnreachableMember(member) =>
//      log.info("Member detected as unreachable: {}", member)
//    case MemberRemoved(member, previousStatus) =>
//      log.info(
//        "Member is Removed: {} after {}",
//        member.address, previousStatus)
//  }

//  def register(member: Member): Unit =
//    if (member.hasRole("frontend"))
//      context.actorSelection(RootActorPath(member.address) / "user" / "frontend") !
//        BackendRegistration

  def receive = {
    case state: CurrentClusterState ⇒
      log.debug("Current members: {}", state.members.mkString(", "))
    case MemberUp(member) =>
      log.debug("Member is Up: {}", member.address)
    case UnreachableMember(member) =>
      log.debug("Member detected as unreachable: {}", member)
    case MemberRemoved(member, previousStatus) =>
      log.debug("Member is Removed: {} after {}", member.address, previousStatus)
    case LeaderChanged(member) => log.info("Leader changed: " + member)
    case WorkerJob(number) => sender() ! primesUnder(number).sum
    case any: MemberEvent => log.info("Member Event: " + any.toString) // ignore
  }
}

object ClusterExampleWorker {

  // Taken from:
  // https://stackoverflow.com/questions/9711785/find-prime-numbers-using-scala-help-me-to-improve
  def primesUnder(n: Long): List[Long] = {
    require(n >= 2)
    def rec(i: Long, primes: List[Long]): List[Long] = {
      if (i >= n) primes
      else if (prime(i, primes)) rec(i + 1, i :: primes)
      else rec(i + 1, primes)
    }
    rec(2, List()).reverse
  }

  private def prime(num: Long, factors: List[Long]): Boolean = factors.forall(num % _ != 0)

  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.load()
    val system = ActorSystem(config.getString("clustering.cluster.name"), config)
    system.actorOf(Props[ClusterExampleWorker], name = "worker")
  }
}
