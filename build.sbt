import Dependencies._
import sbt.Keys.libraryDependencies
import com.typesafe.sbt.packager.docker._

lazy val buildSettings = Seq(
  organization := "com.patrickmcgeever",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.12.6",
  assemblyJarName in assembly := name.value + ".jar"
)

lazy val dockerSettings = Seq(
  maintainer in Docker := "Patrick McGeever <patrick@patrickmcgeever.com>",
  defaultLinuxInstallLocation in Docker := "/opt/cluster-example",
  dockerBaseImage := "openjdk:8u151-jre-alpine",
  dockerExposedPorts := Seq(8000),
  dockerUsername := Some("pmcgeever"),
  dockerUpdateLatest := true,
  dockerEntrypoint := Seq("/opt/cluster-example/bin/launch-app.sh"),
  mappings in Universal := {
    val filtered = (mappings in Universal).value.filter {
      case (_, fName) => !fName.endsWith(".jar")
    }
    val fatJar = (assembly in Compile).value
    filtered :+ (fatJar -> s"lib/${fatJar.getName}")
  },
  dockerCommands ++= Seq(
    ExecCmd("RUN", "chmod", "+x", "/opt/cluster-example/bin/launch-app.sh")
  )
)

lazy val serverDeps = Seq(
  akkaCluster
)

lazy val workerDeps = Seq(
  akkaCluster
)

lazy val common = project
  .settings(
    name := "cluster-example-common",
    buildSettings
  )

lazy val server = project
  .enablePlugins(DockerPlugin)
  .dependsOn(common)
  .settings(
    name := "cluster-example-server",
    buildSettings,
    mainClass in assembly := Some("com.patrickmcgeever.clusterexampleserver.LaunchServer"),
    dockerSettings,
    libraryDependencies := serverDeps
  )

lazy val worker = project
  .enablePlugins(DockerPlugin)
  .dependsOn(common)
  .settings(
    name := "cluster-example-worker",
    buildSettings,
    mainClass in assembly := Some("com.patrickmcgeever.clusterexampleworker.ClusterExampleWorker"),
    dockerSettings,
    libraryDependencies := workerDeps
  )

lazy val akkaClusterExample = (project in file("."))
  .aggregate(common, server, worker)
  .settings(
    name := "akka-cluster-example",
    buildSettings
  )
