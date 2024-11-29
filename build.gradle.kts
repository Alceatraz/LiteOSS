val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project

val enableStatic: String by project

group = "top.btswork"
version = "1.0.0-SNAPSHOT"

plugins {
  id("io.ktor.plugin") version "3.0.1"
  id("org.jetbrains.kotlin.jvm") version "2.1.0"
  id("org.graalvm.buildtools.native") version "0.10.3"
}

dependencies {
  implementation("io.ktor:ktor-server-core")
  implementation("io.ktor:ktor-server-cio")
  implementation("org.slf4j:slf4j-simple:2.0.16")
  // implementation("ch.qos.logback:logback-classic:$logbackVersion")
}

application {
  mainClass.set("top.btswork.liteoss.ApplicationKt")
}

repositories {
  mavenCentral()
}

graalvmNative {

  binaries {

    named("main") {
      verbose.set(true)
      if (enableStatic.toBoolean()) {
        buildArgs.add("--static")
        buildArgs.add("--libc=musl")
      }

      val list = arrayOf(
        "io.ktor,kotlin",
        "kotlinx.io.SegmentPool",
        "ch.qos.logback",
        "org.slf4j.helpers",
        "org.slf4j.LoggerFactory",
      )

      list.forEach {
        buildArgs.add("--initialize-at-build-time=$it")
      }

      buildArgs.add("-H:+InstallExitHandlers")
      buildArgs.add("-H:+ReportExceptionStackTraces")
      buildArgs.add("-H:+ReportUnsupportedElementsAtRuntime")
      imageName.set("liteoss")
    }
  }

  tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    testLogging {
      events("passed", "skipped", "failed")
    }
  }

}
