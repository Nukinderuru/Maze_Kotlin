package com.school21.app

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    val port = System.getProperty("web.port")?.toIntOrNull() ?: 8080
    embeddedServer(Netty, port = port, module = { webModule() }).start(wait = true)
}
