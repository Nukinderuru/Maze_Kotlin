package com.school21.service

import com.school21.service.exception.MazeFormatException
import com.school21.service.parser.MazeFileParser
import kotlin.io.path.createTempFile
import kotlin.io.path.writeText
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MazeFileParserTest {
    private val parser = MazeFileParser()

    @Test
    fun `parse returns maze for valid file`() {
        val file = writeMazeFile(
            """
            4 4
            0 0 0 1
            1 0 1 1
            0 1 0 1
            0 0 0 1

            1 0 1 0
            0 0 1 0
            1 1 0 1
            1 1 1 1
            """.trimIndent(),
        )

        val maze = parser.parse(file)

        assertEquals(4, maze.rows)
        assertEquals(4, maze.cols)
        assertContentEquals(booleanArrayOf(false, false, false, true), maze.rightWalls[0])
        assertContentEquals(booleanArrayOf(true, true, true, true), maze.bottomWalls[3])
    }

    @Test
    fun `parse accepts blank lines around matrices`() {
        val file = writeMazeFile(
            """

            2 2

            0 1
            1 1


            1 0
            1 1

            """.trimIndent(),
        )

        val maze = parser.parse(file)

        assertEquals(2, maze.rows)
        assertEquals(2, maze.cols)
    }

    @Test
    fun `parse rejects dimensions above allowed maximum`() {
        val file = writeMazeFile(
            """
            51 2
            0 1
            1 1
            1 0
            1 1
            """.trimIndent(),
        )

        val exception = assertFailsWith<MazeFormatException> {
            parser.parse(file)
        }

        assertEquals(ValidationConstants.mazeRowRange(1, 50), exception.message)
    }

    @Test
    fun `parse rejects malformed matrix width`() {
        val file = writeMazeFile(
            """
            2 3
            0 1
            1 1 0
            1 0 1
            1 1 1
            """.trimIndent(),
        )

        val exception = assertFailsWith<MazeFormatException> {
            parser.parse(file)
        }

        assertEquals(ValidationConstants.matrixRowCount("right wall", 3), exception.message)
    }

    @Test
    fun `parse rejects non binary cell values`() {
        val file = writeMazeFile(
            """
            1 1
            2
            1
            """.trimIndent(),
        )

        val exception = assertFailsWith<MazeFormatException> {
            parser.parse(file)
        }

        assertEquals(ValidationConstants.MAZE_BINARY_ONLY, exception.message)
    }

    @Test
    fun `parse rejects missing matrix rows`() {
        val file = writeMazeFile(
            """
            2 2
            0 1
            1 1
            1 0
            """.trimIndent(),
        )

        val exception = assertFailsWith<MazeFormatException> {
            parser.parse(file)
        }

        assertEquals(
            ValidationConstants.mazeFileRowCounts(2),
            exception.message,
        )
    }

    private fun writeMazeFile(contents: String) = createTempFile(suffix = ".txt").also {
        it.writeText(contents)
    }
}
