sbtPlugin := true

organization := "io.teamscala.sbt"

name := "sbt-axis"

version := "0.2.2"

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  "org.apache.axis2" % "axis2-kernel" % "1.6.2",
  "org.apache.axis2" % "axis2-java2wsdl" % "1.6.2",
  "org.apache.axis2" % "axis2-adb" % "1.6.2",
  "org.apache.axis2" % "axis2-jaxbri" % "1.6.2",
  "org.apache.axis2" % "axis2-adb-codegen" % "1.6.2",
  "org.apache.axis2" % "axis2-codegen" % "1.6.2",
  "org.apache.axis2" % "axis2-xmlbeans" % "1.6.2",
  "commons-logging" % "commons-logging" % "1.1.1",
  "commons-discovery" % "commons-discovery" % "0.2",
  "commons-codec" % "commons-codec" % "1.10"
)
