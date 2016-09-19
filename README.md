# A SBT plugin for Axis to generate java sources from WSDL using WSDL2Java [![Build Status](https://travis-ci.org/stonexx/sbt-axis.svg?branch=Axis1)](https://travis-ci.org/stonexx/sbt-axis)

Installation
------------

To use this plugin use the addSbtPlugin command within your project's `plugins.sbt` file:

```scala
resolvers += Resolver.url("bintray-stonexx-sbt-plugins", url("http://dl.bintray.com/stonexx/sbt-plugins"))(Resolver.ivyStylePatterns)

addSbtPlugin("com.github.stonexx.sbt" % "sbt-axis" % "0.1.3")
```

For example with `build.sbt`:

```scala
AxisKeys.wsdls += "http://example.com/service?wsdl"
```

Run the following at the sbt console:
```scala
show axisWsdl2java
```
