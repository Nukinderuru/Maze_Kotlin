package com.school21.web

import com.school21.service.ValidationConstants
import com.school21.service.exception.MazeFormatException
import com.school21.web.dto.ErrorDto
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond

fun Application.configureWebExceptionHandling() {
    install(StatusPages) {
        exception<MazeFormatException> { call, exception ->
            call.respond(HttpStatusCode.BadRequest, ErrorDto(exception.message ?: ValidationConstants.WEB_INVALID_MAZE_PAYLOAD))
        }
        exception<IllegalArgumentException> { call, exception ->
            call.respond(HttpStatusCode.BadRequest, ErrorDto(exception.message ?: ValidationConstants.WEB_INVALID_REQUEST))
        }
        exception<Throwable> { call, exception ->
            call.respond(HttpStatusCode.InternalServerError, ErrorDto(exception.message ?: ValidationConstants.WEB_UNEXPECTED_SERVER_ERROR))
        }
    }
}
