import java.util.jar.Attributes
import java.io.File

ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := "4.1.9"
ThisBuild / semanticdbIncludeInJar := false

val stagePackageOutput = settingKey[File]("Where to copy all libs and built artifact")
val stagePackage = taskKey[Unit]("Copy runtime dependencies and built artifact to 'stagePackageOutput'")

lazy val root = project
  .in(file("."))
  .settings(
    stagePackageOutput := baseDirectory.value / "target" / "lib",
    stagePackage := {
      val allLibs: List[File] = dependencyClasspath.in(Runtime).value.map(_.data).filter(_.isFile).toList
      val buildArtifact: File = packageBin.in(Runtime).value
      val jars: List[File] = buildArtifact :: allLibs
      val `mappings src->dest`: List[(File, File)] = jars.map(f => (f, stagePackageOutput.value / f.getName))
      val log = streams.value.log
      log.info(s"Copying to ${stagePackageOutput.value}:")
      log.info(s"${`mappings src->dest`.map(_._1).mkString("\n")}")
      IO.copy(`mappings src->dest`)
    })
  .settings(
    artifactName := { (sv: ScalaVersion, module: ModuleID, artifact: Artifact) =>
      s"${artifact.name}-${module.revision}.${artifact.extension}"
    },
      
    name := "zozzamas",
    version := sys.env.getOrElse("SPECIFICATION_VERSION", "0.1.0"),

    scalaVersion := "0.27.0-RC1",

    scalacOptions ++= Seq(
      "-Yexplicit-nulls",
      "-Ycheck-init",
      "-Xfatal-warnings",
      "-deprecation",
      "-noindent",
      "-source", "3.1"
    ),

    libraryDependencies ++= Seq(
      "org.junit.platform" % "junit-platform-runner" % "1.7.0" % "test",
      "org.junit.jupiter" % "junit-jupiter-engine" % "5.7.0" % "test",
      "org.junit.vintage" % "junit-vintage-engine" % "5.7.0" % "test",
      "com.novocode" % "junit-interface" % "0.11" % "test",
      "com.googlecode.lanterna" % "lanterna" % "3.0.3"
    ),

    testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-v", "-s"),

    packageOptions +=
      Package.ManifestAttributes(
        Attributes.Name.IMPLEMENTATION_VERSION -> sys.env.getOrElse("IMPLEMENTATION_VERSION", "0.1.0"),
        Attributes.Name.SPECIFICATION_VERSION -> version.value
      )
  )
