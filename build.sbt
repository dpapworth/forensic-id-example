name := """forensic-id-example"""

version := "1.0"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,

  "org.hamcrest" % "hamcrest-library" % "1.3" % "test"
)
