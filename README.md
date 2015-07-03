# A SBT plugin for Axis to generate java sources from WSDL using WSDL2Java [![Build Status](https://travis-ci.org/stonexx/sbt-axis.svg?branch=master)](https://travis-ci.org/stonexx/sbt-axis)

Installation
------------

To use this plugin use the addSbtPlugin command within your project's `plugins.sbt` file:

```scala
resolvers += "teamscala-nexus-repository" at "http://repo.teamscala.io/content/groups/public/"

addSbtPlugin("io.teamscala.sbt" % "sbt-axis" % "0.2.3")
```

For example with `build.sbt`:

```scala
AxisKeys.wsdls += "http://example.com/service?wsdl"
```