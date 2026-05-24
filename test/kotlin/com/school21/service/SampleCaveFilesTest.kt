package com.school21.service

import com.school21.service.parser.CaveFileParser
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SampleCaveFilesTest {
    private val parser = CaveFileParser()

    @Test
    fun `all bundled sample caves parse successfully`() {
        val sampleFiles = listOf(
            Path.of("main/resources/caves/cave_04x04.txt"),
            Path.of("main/resources/caves/cave_10x10.txt"),
            Path.of("main/resources/caves/cave_20x20.txt")
        )

        val dimensions = sampleFiles.map { path ->
            val cave = parser.parse(path)
            cave.rows to cave.cols
        }

        assertEquals(listOf(4 to 4, 10 to 10, 20 to 20), dimensions)
    }

    @Test
    fun `bundled cave sample can be parsed from classpath resource`() {
        val inputStream = javaClass.classLoader.getResourceAsStream("caves/cave_20x20.txt")
        assertNotNull(inputStream)

        val cave = parser.parse(inputStream)

        assertEquals(20, cave.rows)
        assertEquals(20, cave.cols)
    }
}
