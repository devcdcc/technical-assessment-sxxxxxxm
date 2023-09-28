ThisBuild / organization := "com.siriusxm"
ThisBuild / scalaVersion := "3.3.1"

lazy val root = (project in file(".")).settings(
  name := "cart-service",
  libraryDependencies ++= Seq(
    // zio "core" modules
    "dev.zio" %% "zio"          % "2.0.18",
    "dev.zio" %% "zio-streams"  % "2.0.18",
    // zio testing library
    "dev.zio" %% "zio-test"     % "2.0.18",
    "dev.zio" %% "zio-test-sbt" % "2.0.18",
    // zio json compatibility
    "dev.zio" %% "zio-json"     % "0.6.2",
    // zio prelude (datatype helpers)
    "dev.zio" %% "zio-prelude"  % "1.0.0-RC21",
    // http client
    "dev.zio" %% "zio-http"     % "3.0.0-RC2"
  )
)

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")

ThisBuild / idePackagePrefix := Some("com.siriusxm")
