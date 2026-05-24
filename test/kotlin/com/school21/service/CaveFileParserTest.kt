package com.school21.service

import com.school21.service.exception.MazeFormatException
import com.school21.service.parser.CaveFileParser
import kotlin.io.path.createTempFile
import kotlin.io.path.writeText
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CaveFileParserTest {
    private val parser = CaveFileParser()

    @Test
    fun `parse returns cave for valid file`() {
        val file = createTempFile(suffix = ".txt")
        file.writeText(
            """
            3 4
            0 1 0 1
            1 0 0 1
            0 1 1 0
            """.trimIndent()
        )

        val cave = parser.parse(file)

        assertEquals(3, cave.rows)
        assertEquals(4, cave.cols)
        assertContentEquals(booleanArrayOf(false, true, false, true), cave.cells[0])
    }

    @Test
    fun `parse rejects invalid row width`() {
        val file = createTempFile(suffix = ".txt")
        file.writeText(
            """
            2 3
            0 1 0
            1 1
            """.trimIndent()
        )

        val exception = assertFailsWith<MazeFormatException> { parser.parse(file) }
        assertEquals(ValidationConstants.matrixRowCount("cave", 3), exception.message)
    }
}
