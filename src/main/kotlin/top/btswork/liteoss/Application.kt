package top.btswork.liteoss

import io.ktor.server.application.Application
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.request.uri

fun main() {

  embeddedServer(
    CIO,
    host = "0.0.0.0",
    port = 8080,
    module = Application::module
  ).start(true)

}

fun Application.module() {
  install(testPlugin)
}

val testPlugin = createApplicationPlugin("Test") {
  onCall { call ->
    println(call.request.uri)
  }
}
