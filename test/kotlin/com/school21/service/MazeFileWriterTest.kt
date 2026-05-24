package com.school21.service

import com.school21.service.generator.EllerMazeGenerator
import com.school21.service.parser.MazeFileParser
import kotlin.io.path.createTempFile
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class MazeFileWriterTest {
    private val generator = EllerMazeGenerator(Random(123))
    private val writer = MazeFileWriter()
    private val parser = MazeFileParser()

    @Test
    fun `writer output can be parsed back into the same maze`() {
        val maze = generator.generate(6, 8)
        val output = createTempFile(suffix = ".txt")

        writer.write(maze, output)

        val parsedMaze = parser.parse(output)
        assertEquals(maze, parsedMaze)
    }
}
