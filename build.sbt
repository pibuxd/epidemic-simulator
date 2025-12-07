ThisBuild / version := "0.1.0-SNAPSHOT"

val _scalaVersion = "2.13.15"

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
      "com.typesafe.akka" %% "akka-actor-typed" % "2.6.20",
      "com.typesafe.akka" %% "akka-stream" % "2.6.20",
      "com.typesafe.akka" %% "akka-http" % "10.2.10",
      "io.spray" %% "spray-json" % "1.3.6"
    ),
    scalacOptions ++= standardOptions
  )
