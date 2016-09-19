package com.github.stonexx.sbt.axis

import java.net.MalformedURLException

import org.apache.axis2.wsdl._
import org.apache.commons.codec.digest.DigestUtils.md5Hex
import sbt.Keys._
import sbt._

import scala.util.Try

object Import {

  val axis = config("axis")

  object AxisKeys {

    val wsdl2java                  = TaskKey[Seq[File]]("axis-wsdl2java", "Runs WSDL2Java")
    val wsdls                      = SettingKey[Seq[String]]("axis-wsdls", "Uri to WSDLs")
    val packageName                = SettingKey[Option[String]]("axis-package-name", "Package to create Java files under, corresponds to -p / --package option in WSDL2Java")
    val dataBindingName            = SettingKey[Option[String]]("axis-data-binding-name", "Data binding framework name. Possible values include \"adb\", \"xmlbeans\", \"jibx\".")
    val otherArgs                  = SettingKey[Seq[String]]("axis-other-args", "Other arguments to pass to WSDL2Java")
    val wsdl4jVersion              = SettingKey[String]("axis-wsdl4j-version", "The version of wsdl4j module.")
    val apacheNeethiVersion        = SettingKey[String]("axis-apache-neethi-version", "The version of Apache Neethi.")
    val apacheXMLSchemaCoreVersion = SettingKey[String]("axis-apache_xml_schema_core_version", "The version of Apache XMLSchema Core.")
    val apacheAxiomImplVersion     = SettingKey[String]("axis-apache-axiom-impl-version", "The version of Apache Axiom implementation.")

  }

}

object SbtAxis extends AutoPlugin {

  override def requires = sbt.plugins.JvmPlugin
  override def trigger = AllRequirements

  val autoImport = Import

  import autoImport._
  import AxisKeys._

  override def projectSettings: Seq[Setting[_]] = Seq(
    sourceManaged in axis := sourceManaged(_ / "axis").value,
    wsdls := Nil,
    packageName := None,
    dataBindingName := None,
    otherArgs := Nil,
    wsdl2java := (streams, wsdls, sourceManaged in axis, packageName, dataBindingName, otherArgs).map(runWsdlToJavas).value,
    sourceGenerators in Compile += wsdl2java.taskValue,
    managedSourceDirectories in Compile += (sourceManaged in axis).value / "main",
    cleanFiles += (sourceManaged in axis).value,
    clean in axis := IO.delete((sourceManaged in axis).value),

    version in axis := "1.7.3",
    libraryDependencies ++= Seq(
      "axis2",
      "axis2-transport-local",
      "axis2-transport-http"
    ).map("org.apache.axis2" % _ % (version in axis).value),

    wsdl4jVersion := "1.6.3",
    apacheNeethiVersion := "3.0.3",
    apacheXMLSchemaCoreVersion := "2.2.1",
    apacheAxiomImplVersion := "1.2.19",
    libraryDependencies ++= Seq(
      "wsdl4j" % "wsdl4j" % wsdl4jVersion.value,
      "org.apache.neethi" % "neethi" % apacheNeethiVersion.value,
      "org.apache.ws.xmlschema" % "xmlschema-core" % apacheXMLSchemaCoreVersion.value,
      "org.apache.ws.commons.axiom" % "axiom-impl" % apacheAxiomImplVersion.value
    )
  )

  private case class WSDL2JavaSettings(
    dest: File,
    packageName: Option[String],
    dataBindingName: Option[String],
    otherArgs: Seq[String]
  )

  private def runWsdlToJavas(
    streams: TaskStreams,
    wsdlUris: Seq[String],
    basedir: File,
    packageName: Option[String],
    dataBindingName: Option[String],
    otherArgs: Seq[String]
  ): Seq[File] = {

    val cachedir = basedir / "cache"
    val wsdlFiles = wsdlUris.map { wsdlUri =>
      Try(url(wsdlUri)).map(wsdlUrl => IO.urlAsFile(wsdlUrl).getOrElse {
        val wsdlFile = cachedir / (md5Hex(wsdlUri) + ".wsdl")
        if (!wsdlFile.exists) IO.download(wsdlUrl, wsdlFile)
        wsdlFile
      }).recover {
        case e: MalformedURLException => file(wsdlUri)
      }.get
    }

    val cachedFn = FileFunction.cached(cachedir, FilesInfo.lastModified, FilesInfo.exists) { _ =>
      val settings = WSDL2JavaSettings(basedir / "main", packageName, dataBindingName, otherArgs)
      wsdlUris.foreach(runWsImport(streams, _, settings))
      (settings.dest ** "*.java").get.toSet
    }
    cachedFn(wsdlFiles.toSet).toSeq
  }

  private def makeArgs(wsdlUri: String, settings: WSDL2JavaSettings): Seq[String] =
    settings.packageName.toSeq.flatMap(p => Seq("-p", p)) ++
      settings.dataBindingName.toSeq.flatMap(n => Seq("-d", n)) ++
      Seq("-o", settings.dest.getAbsolutePath) ++
      Seq("-S", ".") ++
      settings.otherArgs ++
      Seq("-uri", wsdlUri)

  private def runWsImport(streams: TaskStreams, wsdlUri: String, settings: WSDL2JavaSettings): Unit = {
    streams.log.info("Generating Java from " + wsdlUri)

    streams.log.debug("Creating dir " + settings.dest)
    settings.dest.mkdirs()

    val args = makeArgs(wsdlUri, settings)
    streams.log.info("wsimport " + args.mkString(" "))
    try WSDL2Code.main(args.toArray)
    catch {
      case t: Throwable =>
        streams.log.error("Problem running WSDL2Java " + args.mkString(" "))
        throw t
    }
  }

}
