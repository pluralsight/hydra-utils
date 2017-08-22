import sbt._


object Dependencies {

  val scalaTestVersion = "3.0.1"
  val slf4jVersion = "1.7.21"
  val log4jVersion = "2.7"
  val kxbmapConfigVersion = "0.4.4"
  val typesafeConfigVersion = "1.3.0"
  val kafkaVersion = "0.10.2.0"
  val scalazVersion = "7.2.9"
  val scalaCacheVersion = "0.9.3"
  val jacksonVersion = "2.8.4"
  val reflectionsVersion = "0.9.10"
  val akkaVersion = "2.5.4"

  object Compile {

    val scalaConfigs = "com.github.kxbmap" %% "configs" % kxbmapConfigVersion

    val typesafeConfig = "com.typesafe" % "config" % typesafeConfigVersion

    val scalaz = "org.scalaz" %% "scalaz-core" % scalazVersion

    val kafka = Seq(
      "org.apache.kafka" %% "kafka" % kafkaVersion,
      "org.apache.kafka" % "kafka-clients" % kafkaVersion,
      "net.manub" %% "scalatest-embedded-kafka" % "0.12.0" % "test")

    val log4J = Seq(
      "org.apache.logging.log4j" % "log4j-slf4j-impl" % log4jVersion,
      "org.apache.logging.log4j" % "log4j-core" % log4jVersion,
      "org.apache.logging.log4j" % "log4j-api" % log4jVersion,
      "org.apache.logging.log4j" % "log4j-1.2-api" % log4jVersion)

    val guavacache = "com.github.cb372" %% "scalacache-guava" % scalaCacheVersion

    val courier = "ch.lightshed" %% "courier" % "0.1.4"

    val jackson = Seq(
      "com.fasterxml.jackson.core" % "jackson-core" % jacksonVersion,
      "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion
    )

    val akka = Seq("com.typesafe.akka" %% "akka-actor" % akkaVersion,
      "com.typesafe.akka" %% "akka-slf4j" % akkaVersion)

    val reflections = "org.reflections" % "reflections" % reflectionsVersion
  }

  object Test {
    val akkaTest = "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test"
    val scalaTest = "org.scalatest" %% "scalatest" % scalaTestVersion % "test"
    val scalaMock = "org.scalamock" %% "scalamock-scalatest-support" % "3.5.0" % "test"
    val junit = "junit" % "junit" % "4.12" % "test"
    val mailerTest = "org.jvnet.mock-javamail" % "mock-javamail" % "1.9" % "test"
  }


  import Compile._
  import Test._

  val testDeps = Seq(scalaTest, junit, scalaMock, mailerTest, akkaTest)

  val baseDeps = log4J ++ Seq(scalaz, scalaConfigs, courier, reflections) ++ jackson ++ testDeps

  val coreDeps = baseDeps ++ Seq(guavacache) ++ akka ++ kafka

  val overrides = Set(log4J, typesafeConfig)
}

