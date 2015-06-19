sbtPlugin := true

organization := "io.teamscala.sbt"

name := "sbt-axis"

version := "0.1.2"

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  "axis" % "axis" % "1.4",
  "axis" % "axis-saaj" % "1.4",
  "axis" % "axis-wsdl4j" % "1.5.1",
  "javax.activation" % "activation" % "1.1.1",
  "javax.mail" % "mail" % "1.4",
  "commons-logging" % "commons-logging" % "1.0.4",
  "commons-discovery" % "commons-discovery" % "0.2",
  "commons-codec" % "commons-codec" % "1.10"
)
