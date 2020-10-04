import java.util.jar.Attributes
import java.io.File

val stagePackageOutput             = settingKey[File]("Where to copy all libs and built artifact")
val stagePackage  = taskKey[Unit]("Copy runtime dependencies and built artifact to 'stagePackageOutput'")

lazy val root = project
  .in(file("."))
  .settings(
    stagePackageOutput              := baseDirectory.value / "target" / "lib",
    stagePackage   := {
      val allLibs:                List[File]          = dependencyClasspath.in(Runtime).value.map(_.data).filter(_.isFile).toList
      val buildArtifact:          File                = packageBin.in(Runtime).value
      val jars:                   List[File]          = buildArtifact :: allLibs
      val `mappings src->dest`:   List[(File, File)]  = jars.map(f => (f, stagePackageOutput.value / f.getName))
      val log                                         = streams.value.log
      log.info(s"Copying to ${stagePackageOutput.value}:")
      log.info(s"${`mappings src->dest`.map(_._1).mkString("\n")}")
      IO.copy(`mappings src->dest`)
    })
  .settings(
    name := "zozzamas",
    version := sys.env.getOrElse("SPECIFICATION_VERSION", "0.1.0"),

    scalaVersion := "0.27.0-RC1",

    scalacOptions ++= Seq(
      "-Yexplicit-nulls",
      "-Ycheck-init",
      "-Xfatal-warnings",
      "-deprecation"
    ),

    libraryDependencies ++= Seq(
      "com.novocode" % "junit-interface" % "0.11" % "test",
      "com.googlecode.lanterna" % "lanterna" % "3.0.3"
    ),
    
    packageOptions += 
      Package.ManifestAttributes(
        Attributes.Name.IMPLEMENTATION_VERSION -> sys.env.getOrElse("IMPLEMENTATION_VERSION", "0.1.0"), 
        Attributes.Name.SPECIFICATION_VERSION -> version.value
      )
  )
