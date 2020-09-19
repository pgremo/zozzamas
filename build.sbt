lazy val root = project
  .in(file("."))
  .settings(
    name := "zozzamas",
    version := "0.1.0",

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
    )
  )
