import mill._, scalalib._

object zozzamas extends ScalaModule {
  def scalaVersion = "0.27.0-RC1"
  override def scalacOptions = Seq(
    "-Yexplicit-nulls",
    "-Ycheck-init",
    "-Xfatal-warnings",
    "-deprecation",
    "-noindent",
    "-source", "3.1"
  )
  override def ivyDeps = Agg(ivy"com.googlecode.lanterna:lanterna:3.0.4")

  object test extends Tests {
    def testFrameworks = Seq("com.novocode.junit.JUnitFramework")
    override def ivyDeps = Agg(
      ivy"com.novocode:junit-interface:0.11",
      ivy"org.junit.jupiter:junit-jupiter-engine:5.7.0",
      ivy"org.junit.jupiter:junit-jupiter-api:5.7.0"
    )
  }
}
