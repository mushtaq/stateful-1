
ThisBuild / scalaVersion     := "2.12.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.thoughtworks"
ThisBuild / organizationName := "ThoughtWorks"

lazy val root = (project in file("."))
  .settings(
    name := "stateful",
    libraryDependencies ++= Seq(
      Akka.`akka-stream-typed`,
      Libs.`ammonite`,
      Libs.`scalatest` % Test,
    )
  )
