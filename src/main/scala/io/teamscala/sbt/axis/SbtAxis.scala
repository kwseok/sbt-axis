package io.teamscala.sbt.axis

import org.apache.axis.utils._
import org.apache.axis.wsdl._
import org.apache.axis.wsdl.gen._
import sbt.Keys._
import sbt._

import scala.collection.JavaConverters._

object Import {

  val axis = config("axis")

  object AxisKeys {

    val wsdl2java              = taskKey[Seq[File]]("Runs WSDL2Java")
    val wsdlUris               = settingKey[Seq[String]]("Uri to WSDLs")
    val packageName            = settingKey[Option[String]]("Package to create Java files under, corresponds to -p / --package option in WSDL2Java")
    val dataBindingName        = settingKey[Option[String]]("Data binding framework name. Possible values include \"adb\", \"xmlbeans\", \"jibx\".")
    val timeout                = settingKey[Option[Int]]("Timeout used when generating sources")
    val otherArgs              = settingKey[Seq[String]]("Other arguments to pass to WSDL2Java")
    val wsdl4jVersion          = settingKey[String]("The version of axis wsdl4j module.")
    val javaxActivationVersion = settingKey[String]("The version of javax activation module.")
    val javaxMailVersion       = settingKey[String]("The version of javax mail module.")
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
    wsdlUris in axis := Nil,
    packageName in axis := None,
    dataBindingName in axis := None,
    timeout in axis := Some(45),
    otherArgs in axis := Nil,
    wsdl2java in axis := (streams in axis, wsdlUris in axis, sourceManaged in axis, packageName in axis, dataBindingName in axis, timeout in axis, otherArgs in axis).map(runWsdlToJavas).value,
    sourceGenerators in Compile += (wsdl2java in axis).taskValue,
    managedSourceDirectories in Compile += (sourceManaged in axis).value,
    cleanFiles += (sourceManaged in axis).value,

    version in axis := "1.4",
    wsdl4jVersion in axis := "1.5.1",
    javaxActivationVersion in axis := "1.1.1",
    javaxMailVersion in axis := "1.4",
    libraryDependencies ++= Seq(
      "axis" % "axis" % (version in axis).value,
      "axis" % "axis-saaj" % (version in axis).value,
      "axis" % "axis-wsdl4j" % (wsdl4jVersion in axis).value,
      "javax.activation" % "activation" % (javaxActivationVersion in axis).value,
      "javax.mail" % "mail" % (javaxMailVersion in axis).value
    )
  )

  private case class WSDL2JavaSettings(dest: File,
                                       packageName: Option[String],
                                       dataBindingName: Option[String],
                                       timeout: Option[Int],
                                       otherArgs: Seq[String])

  private def runWsdlToJavas(streams: TaskStreams,
                             wsdlUris: Seq[String],
                             dest: File,
                             packageName: Option[String],
                             dataBindingName: Option[String],
                             timeout: Option[Int],
                             otherArgs: Seq[String]): Seq[File] = {

    val settings = WSDL2JavaSettings(dest, packageName, dataBindingName, timeout, otherArgs)
    wsdlUris.flatMap(runWsImport(streams, _, settings)).distinct
  }

  private def makeArgs(wsdlUri: String, settings: WSDL2JavaSettings): Seq[String] =
    settings.packageName.toSeq.flatMap(p => Seq("--package", p)) ++
    settings.dataBindingName.toSeq.flatMap(n => Seq("-d", n)) ++
    Seq("-O", settings.timeout.map(_.toString).getOrElse("-1")) ++
    Seq("-o", settings.dest.getAbsolutePath) ++
    settings.otherArgs ++
    Seq(wsdlUri)

  private def runWsImport(streams: TaskStreams, wsdlUri: String, settings: WSDL2JavaSettings): Seq[File] = {
    streams.log.info("Generating Java from " + wsdlUri)

    streams.log.debug("Creating dir " + settings.dest)
    settings.dest.mkdirs()

    val args = makeArgs(wsdlUri, settings)
    streams.log.debug("wsimport " + args.mkString(" "))
    try new WSDL2JavaWrapper().execute(args.toArray)
    catch {
      case t: Throwable =>
        streams.log.error("Problem running WSDL2Java " + args.mkString(" "))
        throw t
    }
    (settings.dest ** "*.java").get
  }

}

class WSDL2JavaWrapper extends WSDL2Java {

  def execute(args: Array[String]) {
    // Extremely ugly hack because the "options" static field in WSDL2Java
    // shadows the "options" instance field in WSDL2. It is the field
    // in WSDL2 that we need because the command line options
    // defined in subclasses get copied to it.
    // The result is that options defined in WSDL2 ( timeout, Debug )
    // are not available otherwise.  (MOJO-318)
    val field = classOf[WSDL2].getDeclaredField("options")

    val options = field.get(this).asInstanceOf[Array[CLOptionDescriptor]]

    for (option <- new CLArgsParser(args, options).getArguments.asScala)
      parseOption(option.asInstanceOf[CLOption])

    validateOptions()

    parser.run(wsdlURI)

  }

}
