ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.11"

lazy val root = (project in file("."))
  .settings(
    name := "ReflectDetect",
    libraryDependencies += "de.opal-project" % "framework_2.13" % "5.0.0"
  )
