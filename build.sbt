import Dependencies._
import build._

lazy val `fs2-example` = Project(
  id = "fs2-example",
  base = file(".")
).settings(
  commonSettings,
  Seq(
    libraryDependencies ++= fs2 ++ scalatest,
    scalafmtOnCompile := true
  )
)
