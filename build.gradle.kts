val kotlinVersion: String by project
val logbackVersion: String by project
val enableStatic: String by project

group = "top.btswork"
version = "0.0.0-SNAPSHOT"

plugins {
  id("io.ktor.plugin") version "3.0.1"
  id("org.jetbrains.kotlin.jvm") version "2.1.10"
  id("org.graalvm.buildtools.native") version "0.10.5"
}

dependencies {
  implementation("io.ktor:ktor-server-core")
  implementation("io.ktor:ktor-server-cio")
  implementation("ch.qos.logback:logback-classic:$logbackVersion")
  testImplementation("io.ktor:ktor-server-tests")
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.3")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
}

application {
  mainClass.set("top.btswork.liteoss.ApplicationKt")
}

repositories {
  mavenCentral()
}

graalvmNative {

  binaries {

    val list = arrayOf(
      "kotlin",
      "io.ktor",
      "kotlinx.io.SegmentPool",
      "kotlinx.io.bytestring.ByteString",
      "ch.qos.logback",
      "org.slf4j.helpers",
      "org.slf4j.LoggerFactory",
    )

    named("main") {
      verbose.set(true)
      if (enableStatic.toBoolean()) {
        buildArgs.add("--static")
        buildArgs.add("--libc=musl")
      }
      list.forEach {
        buildArgs.add("--initialize-at-build-time=$it")
      }
      buildArgs.add("-march=native")
      buildArgs.add("--install-exit-handlers")
      buildArgs.add("--report-unsupported-elements-at-runtime")
      buildArgs.add("-H:+ReportExceptionStackTraces")
      imageName.set("liteoss")
    }

    named("test") {
      verbose.set(true)
      list.forEach {
        buildArgs.add("--initialize-at-build-time=$it")
      }
      buildArgs.add("-march=native")
      buildArgs.add("--install-exit-handlers")
      buildArgs.add("--report-unsupported-elements-at-runtime")
      buildArgs.add("-H:+ReportExceptionStackTraces")
      val path = "${projectDir}/src/test/resources/META-INF/native-image/"
      buildArgs.add("-H:ResourceConfigurationFiles=${path}resource-config.json")
      buildArgs.add("-H:ReflectionConfigurationFiles=${path}reflect-config.json")
      imageName.set("liteoss-test")
    }

  }

  tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    testLogging {
      events("passed", "skipped", "failed")
    }
  }

}
