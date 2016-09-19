sbtPlugin := true

organization := "com.github.stonexx.sbt"

name := "sbt-axis"

scalaVersion := "2.10.5"

val axis2Version = "1.7.3"

libraryDependencies ++= Seq(
  "org.apache.axis2" % "axis2-kernel" % axis2Version,
  "org.apache.axis2" % "axis2-java2wsdl" % axis2Version,
  "org.apache.axis2" % "axis2-adb" % axis2Version,
  "org.apache.axis2" % "axis2-jaxbri" % axis2Version,
  "org.apache.axis2" % "axis2-adb-codegen" % axis2Version,
  "org.apache.axis2" % "axis2-codegen" % axis2Version,
  "org.apache.axis2" % "axis2-xmlbeans" % axis2Version,
  "commons-logging" % "commons-logging" % "1.2",
  "commons-discovery" % "commons-discovery" % "0.5",
  "commons-codec" % "commons-codec" % "1.10"
)

licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0.html"))
