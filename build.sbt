val Http4sVersion = "0.18.15"
val Specs2Version = "4.2.0"
val LogbackVersion = "1.2.3"
val ScalatestVersion = "3.0.4"
val CirceVersion = "0.9.3"

lazy val root = (project in file("."))
  .settings(
    organization := "com.petearthur",
    name := "petes-workflow-server",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.12.6",
    libraryDependencies ++= Seq(
      "org.http4s"      %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s"      %% "http4s-circe"        % Http4sVersion,
      "org.http4s"      %% "http4s-dsl"          % Http4sVersion,
      "io.circe" %% "circe-generic" % CirceVersion,
      "io.circe" %% "circe-java8" % CirceVersion,
      "org.scalactic" %% "scalactic" % ScalatestVersion % "test",
      "org.scalatest" %% "scalatest" % ScalatestVersion % "test",
      "ch.qos.logback"  %  "logback-classic"     % LogbackVersion
    )
  )

