package com.school21.service

import com.school21.service.generator.CaveGenerator
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertTrue

class CaveGeneratorTest {
    @Test
    fun `initialize with zero chance creates empty cave`() {
        val cave = CaveGenerator(Random(1)).initialize(4, 4, 0)
        assertTrue(cave.cells.all { row -> row.all { !it } })
    }

    @Test
    fun `initialize with full chance creates solid cave`() {
        val cave = CaveGenerator(Random(1)).initialize(4, 4, 100)
        assertTrue(cave.cells.all { row -> row.all { it } })
    }
}
