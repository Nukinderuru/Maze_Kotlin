package com.school21.web

import com.school21.service.ValidationConstants
import com.school21.service.app.MazeApplicationService
import com.school21.web.dto.ErrorDto
import com.school21.web.dto.GenerateMazeRequest
import com.school21.web.dto.SolutionDto
import com.school21.web.dto.SolveMazeRequest
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.jvm.javaio.*

fun Route.mazeRoutes(service: MazeApplicationService) {
    route("/api/mazes") {
        post("/upload") {
            val multipart = call.receiveMultipart()
            var mazeDto: com.school21.web.dto.MazeDto? = null

            multipart.forEachPart { part ->
                if (mazeDto == null && part is PartData.FileItem) {
                    part.provider().toInputStream().use { inputStream ->
                        val maze = service.loadMaze(inputStream)
                        mazeDto = MazeWebMapper.toDto(maze)
                    }
                }
                part.dispose()
            }

            if (mazeDto == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorDto(ValidationConstants.WEB_MAZE_FILE_REQUIRED))
            } else {
                call.respond(mazeDto)
            }
        }

        post("/generate") {
            val request = call.receive<GenerateMazeRequest>()
            val maze = service.generateMaze(request.rows, request.cols)
            call.respond(MazeWebMapper.toDto(maze))
        }

        post("/solve") {
            val request = call.receive<SolveMazeRequest>()
            val maze = MazeWebMapper.fromDto(request.maze)
            val path = service.solveMaze(
                maze,
                MazeWebMapper.fromDto(request.start),
                MazeWebMapper.fromDto(request.end),
            )
            call.respond(SolutionDto(path.map(MazeWebMapper::toDto)))
        }
    }
}
