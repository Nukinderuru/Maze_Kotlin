package com.school21.web

import com.school21.app.webModule
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MazeRoutesTest {
    @Test
    fun `generate endpoint returns maze json`() = testApplication {
        application { webModule() }

        val response = client.post("/api/mazes/generate") {
            contentType(ContentType.Application.Json)
            setBody("""{"rows":4,"cols":5}""")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("\"rows\":4"))
        assertTrue(body.contains("\"cols\":5"))
    }

    @Test
    fun `solve endpoint returns solution path`() = testApplication {
        application { webModule() }

        val payload = """
            {
              "maze": {
                "rows": 2,
                "cols": 2,
                "rightWalls": [[false,true],[false,true]],
                "bottomWalls": [[false,false],[true,true]]
              },
              "start": {"row": 1, "col": 1},
              "end": {"row": 2, "col": 2}
            }
        """.trimIndent()

        val response = client.post("/api/mazes/solve") {
            contentType(ContentType.Application.Json)
            setBody(payload)
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("\"path\""))
        assertTrue(body.contains("\"row\":1"))
        assertTrue(body.contains("\"row\":2"))
    }

    @Test
    fun `upload endpoint parses maze file`() = testApplication {
        application { webModule() }

        val mazeFile = """
            2 2
            0 1
            1 1

            1 0
            1 1
        """.trimIndent()

        val response = client.post("/api/mazes/upload") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("file", mazeFile.toByteArray(), io.ktor.http.Headers.build {
                            append(HttpHeaders.ContentDisposition, "filename=maze.txt")
                            append(HttpHeaders.ContentType, ContentType.Text.Plain.toString())
                        })
                    },
                ),
            )
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("\"rows\":2"))
        assertTrue(body.contains("\"cols\":2"))
    }

    @Test
    fun `upload endpoint rejects malformed maze`() = testApplication {
        application { webModule() }

        val malformed = "1 1\n2\n1"

        val response = client.post("/api/mazes/upload") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("file", malformed.toByteArray(), io.ktor.http.Headers.build {
                            append(HttpHeaders.ContentDisposition, "filename=maze.txt")
                            append(HttpHeaders.ContentType, ContentType.Text.Plain.toString())
                        })
                    },
                ),
            )
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("message"))
    }
}
