import java.util.jar.Attributes
import java.io.File

lazy val root = project
  .in(file("."))
  .settings(
    name := "zozzamas",
    version := sys.env.getOrElse("SPECIFICATION_VERSION", "0.1.0"),

    assemblyOutputPath in assembly := new File("target") / (assemblyJarName in assembly).value,

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
