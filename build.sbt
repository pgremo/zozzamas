import java.util.jar.Attributes
import java.io.File

enablePlugins(JavaAppPackaging)
enablePlugins(JDKPackagerPlugin)

mappings in (Compile, packageDoc) := Seq()

lazy val root = project
  .in(file("."))
  .settings(
    artifactName := { (sv: ScalaVersion, module: ModuleID, artifact: Artifact) =>
      s"${artifact.name}-${module.revision}.${artifact.extension}"
    },
      
    name := "zozzamas",
    version := sys.env.getOrElse("SPECIFICATION_VERSION", "0.1.0"),

    scalaVersion := "3.0.0-M3",

    scalacOptions ++= Seq(
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
      "com.googlecode.lanterna" % "lanterna" % "3.0.4"
    ),

    testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-v", "-s"),

    packageOptions +=
      Package.ManifestAttributes(
        Attributes.Name.IMPLEMENTATION_VERSION -> sys.env.getOrElse("IMPLEMENTATION_VERSION", "0.1.0"),
        Attributes.Name.SPECIFICATION_VERSION -> version.value
      )
  )
