name := "Lift 3 Memoize Todo"

version := "0.1.0"

organization := "AGYNAMIX"

scalaVersion := "2.12.6"

resolvers ++= Seq(
  "snapshots"     at "https://oss.sonatype.org/content/repositories/snapshots",
  "staging"       at "https://oss.sonatype.org/content/repositories/staging",
  "releases"      at "https://oss.sonatype.org/content/repositories/releases"
)

enablePlugins(JettyPlugin)
 
unmanagedResourceDirectories in Test += baseDirectory.value / "src/main/webapp"

scalacOptions ++= Seq("-deprecation", "-unchecked")

libraryDependencies ++= {
  val liftVersion = "3.3.0"
  val liftEdition = liftVersion.split("\\.").take(2).mkString(".")
  Seq(
    "net.liftweb"       %% "lift-webkit"            % liftVersion,
    "net.liftmodules"   %% s"lift-jquery-module_$liftEdition" % "2.10",
    "ch.qos.logback"    % "logback-classic"         % "1.2.3",
    "org.specs2"        %% "specs2-core"            % "3.9.4"            % "test",
    "javax.servlet"     % "javax.servlet-api"       % "3.0.1"            % "provided"
  )
}

scalacOptions in Test ++= Seq("-Yrangepos")
