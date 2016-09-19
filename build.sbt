sbtPlugin := true

organization := "com.github.stonexx.sbt"

name := "sbt-axis"

scalaVersion := "2.10.4"

val axisVersion = "1.4"

libraryDependencies ++= Seq(
  "axis" % "axis" % axisVersion,
  "axis" % "axis-saaj" % axisVersion,
  "axis" % "axis-wsdl4j" % "1.5.1",
  "javax.activation" % "activation" % "1.1.1",
  "javax.mail" % "mail" % "1.4",
  "commons-logging" % "commons-logging" % "1.2",
  "commons-discovery" % "commons-discovery" % "0.5",
  "commons-codec" % "commons-codec" % "1.10"
)

licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0.html"))
