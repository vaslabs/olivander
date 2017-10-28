name := "olivander"

mainClass in (Compile, run) := Some("org.vaslabs.example.Main")

version := "0.1"

scalaVersion := "2.12.4"

val akkaVersion = "2.5.6"
val circeVersion = "0.8.0"
val monocleVersion = "1.4.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-http" % "10.0.10",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.0.10" % Test,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
  "com.typesafe.akka" %% "akka-cluster" % "2.5.6",
  "com.typesafe.akka" %% "akka-cluster-sharding" % "2.5.6",
  "org.scalatest" %% "scalatest" % "3.0.1" % Test,
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "io.circe" %% "circe-java8" % circeVersion,
  "de.heikoseeberger" %% "akka-http-circe" % "1.16.0",
  "com.github.pureconfig" %% "pureconfig" % "0.7.2",
  "de.knutwalker" %% "akka-stream-circe" % "3.4.0",
  "de.knutwalker" %% "akka-http-circe" % "3.4.0",
  "de.knutwalker" %% "akka-stream-json" % "3.3.0",
  "de.knutwalker" %% "akka-http-json" % "3.3.0",
  "com.gilt" %% "gfc-aws-kinesis-akka" % "0.15.1",
  "com.gilt" %% "gfc-aws-kinesis" % "0.15.1",
  "com.whisk" %% "docker-testkit-scalatest" % "0.9.5" % "test",
  "com.whisk" %% "docker-testkit-impl-spotify" % "0.9.5" % "test"
)
