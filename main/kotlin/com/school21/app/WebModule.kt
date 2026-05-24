package com.school21.app

import com.school21.service.MazeSolver
import com.school21.service.app.MazeApplicationService
import com.school21.service.generator.EllerMazeGenerator
import com.school21.service.parser.MazeFileParser
import com.school21.web.configureWebExceptionHandling
import com.school21.web.mazeRoutes
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.http.content.staticResources
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.webModule() {
    install(ContentNegotiation) {
        json()
    }
    install(CORS)
    configureWebExceptionHandling()

    val service = MazeApplicationService(
        parser = MazeFileParser(),
        generator = EllerMazeGenerator(),
        solver = MazeSolver(),
    )

    routing {
        mazeRoutes(service)
        staticResources("/", "web")
        get("/") {
            call.respondRedirect("/index.html")
        }
    }
}
