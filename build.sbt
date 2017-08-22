import sbt.Keys.version
import sbt._

val JDK = "1.8"
val buildNumber = scala.util.Properties.envOrNone("version").map(v => "." + v).getOrElse("")
val hydraUtilsVersion = "0.1.0" + buildNumber

lazy val defaultSettings = Seq(
  organization := "pluralsight",
  bintrayOrganization := Some("pluralsight"),
  licenses += ("Apache-2.0", url("https://opensource.org/licenses/Apache-2.0")),
  version := hydraUtilsVersion,
  scalaVersion := "2.12.2",
  description := "Hydra Utils",
  libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  excludeDependencies += "org.slf4j" % "slf4j-log4j12",
  excludeDependencies += "log4j" % "log4j",
  logLevel := Level.Info,
  scalacOptions ++= Seq("-encoding", "UTF-8", "-feature", "-language:_", "-deprecation", "-unchecked"),
  javacOptions in Compile ++= Seq("-encoding", "UTF-8", "-source", JDK, "-target", JDK,
    "-Xlint:unchecked", "-Xlint:deprecation", "-Xlint:-options"),

  resolvers += Resolver.mavenLocal,
  resolvers += "Scalaz Bintray Repo" at "https://dl.bintray.com/scalaz/releases",
  resolvers += "lightshed-maven" at "http://dl.bintray.com/content/lightshed/maven",

  ivyLoggingLevel in ThisBuild := UpdateLogging.Quiet,
  ivyScala := ivyScala.value map (_.copy(overrideScalaVersion = true)),
  isSnapshot := true,
  coverageEnabled := true,
  coverageExcludedPackages := ".*Exception*"
)

lazy val moduleSettings = defaultSettings ++ Test.testSettings

lazy val root = Project(
  id = "hydra-utils",
  base = file("."),
  settings = defaultSettings ++ Seq(libraryDependencies ++= Dependencies.coreDeps)
)