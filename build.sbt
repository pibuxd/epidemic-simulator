
ThisBuild / version := "0.1.0-SNAPSHOT"

val _scalaVersion = "3.7.1"

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

val scala3Options = Seq(
  "-explain",
  "-Wsafe-init",
  "-Ycheck-all-patmat",
  "-Wunused:all",
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
      "org.scala-lang.modules" %% "scala-parallel-collections" % "1.2.0"
    ),
    scalacOptions ++= standardOptions ++ scala3Options
  )

testFrameworks += new TestFramework("munit.Framework")