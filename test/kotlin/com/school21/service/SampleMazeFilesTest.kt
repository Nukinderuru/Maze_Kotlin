package com.school21.service

import com.school21.service.parser.MazeFileParser
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SampleMazeFilesTest {
    private val parser = MazeFileParser()

    @Test
    fun `all bundled sample mazes parse successfully`() {
        val sampleFiles = listOf(
            Path.of("main/resources/mazes/maze_02x02.txt"),
            Path.of("main/resources/mazes/maze_04x04.txt"),
            Path.of("main/resources/mazes/maze_10x10.txt"),
            Path.of("main/resources/mazes/maze_20x20.txt"),
        )

        val dimensions = sampleFiles.map { path ->
            val maze = parser.parse(path)
            maze.rows to maze.cols
        }

        assertEquals(
            listOf(2 to 2, 4 to 4, 10 to 10, 20 to 20),
            dimensions,
        )
    }

    @Test
    fun `bundled sample can be parsed from classpath resource`() {
        val inputStream = javaClass.classLoader.getResourceAsStream("mazes/maze_20x20.txt")
        assertNotNull(inputStream)

        val maze = parser.parse(inputStream)

        assertEquals(20, maze.rows)
        assertEquals(20, maze.cols)
    }
}
