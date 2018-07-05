import sbt._

object Dependencies {
  val fs2 = Seq(
    "co.fs2" %% "fs2-core" % "0.10.5",
  )

  val scalatest = Seq(
    "org.scalatest" %% "scalatest" % "3.0.3" % "test",
  )

  val runtimeLogging = Seq(
    "ch.qos.logback" % "logback-classic" % "1.2.3" % "runtime",
  )
}
