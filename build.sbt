ThisBuild / version := "0.1.0-SNAPSHOT"

val _scalaVersion = "3.7.1"
val akkaVersion = "2.8.5"
val akkaHttpVersion = "10.5.3"

name := "EpidemicSimulator"
ThisBuild / scalaVersion := _scalaVersion
ThisBuild / versionScheme := Some("semver-spec")
Test / scalaVersion := _scalaVersion

val standardOptions = Seq(
  "-deprecation",
  "-unchecked",
  "-feature",
  "-encoding",
  "utf8"
)

developers := List(
  Developer(
    "jzajac04",
    "Jan Zając",
    "jzajac04@gmail.com",
    url("https://github.com/jzajac04"))
)

lazy val root = (project in file("."))
  .settings(
    name := "EpidemicSimulator",
    libraryDependencies ++= Seq(
      "org.scala-lang.modules" %% "scala-parallel-collections" % "1.2.0",
      "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "io.spray" %% "spray-json" % "1.3.6",
      "org.scalatest" %% "scalatest" % "3.2.18" % Test
    ),
    scalacOptions ++= standardOptions
  )
