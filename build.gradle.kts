val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project

val enableStatic: String by project

group = "top.btswork"
version = "1.0.0-SNAPSHOT"

plugins {
  id("io.ktor.plugin") version "2.3.12"
  id("org.jetbrains.kotlin.jvm") version "2.1.0"
//  id("org.graalvm.buildtools.native") version "0.9.28"
  id("org.graalvm.buildtools.native") version "0.10.3"
}

dependencies {
  implementation("io.ktor:ktor-server-core")
  implementation("io.ktor:ktor-server-cio")
  implementation("ch.qos.logback:logback-classic:$logbackVersion")
  testImplementation("io.ktor:ktor-server-tests")
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.3")
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

    named("main") {
      verbose.set(true)
      if (enableStatic.toBoolean()) {
        buildArgs.add("--static")
        buildArgs.add("--libc=musl")
      }
      buildArgs.add("--initialize-at-build-time=io.ktor,kotlin")
      buildArgs.add("--initialize-at-build-time=ch.qos.logback")
      buildArgs.add("--initialize-at-build-time=org.slf4j.helpers")
      buildArgs.add("--initialize-at-build-time=org.slf4j.LoggerFactory")
      buildArgs.add("-H:+InstallExitHandlers")
      buildArgs.add("-H:+ReportExceptionStackTraces")
      buildArgs.add("-H:+ReportUnsupportedElementsAtRuntime")
      imageName.set("liteoss")
    }

    named("test") {
      verbose.set(true)
      buildArgs.add("--initialize-at-build-time=io.ktor,kotlin")
      buildArgs.add("--initialize-at-build-time=ch.qos.logback")
      buildArgs.add("--initialize-at-build-time=org.slf4j.LoggerFactory")
      buildArgs.add("--initialize-at-build-time=org.slf4j.helpers.Reporter")
      buildArgs.add("-H:+InstallExitHandlers")
      buildArgs.add("-H:+ReportExceptionStackTraces")
      buildArgs.add("-H:+ReportUnsupportedElementsAtRuntime")
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
