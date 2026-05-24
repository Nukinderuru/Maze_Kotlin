package com.school21.service

import com.school21.model.Cave
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class CaveSimulatorTest {
    private val simulator = CaveSimulator()

    @Test
    fun `next step applies birth and death rules`() {
        val cave = Cave(
            3,
            3,
            arrayOf(
                booleanArrayOf(false, true, false),
                booleanArrayOf(false, false, true),
                booleanArrayOf(true, false, false)
            )
        )

        val next = simulator.nextStep(cave, birthLimit = 2, deathLimit = 3)

        assertContentEquals(booleanArrayOf(true, true, true), next.cells[1])
    }

    @Test
    fun `outside cells are treated as alive`() {
        val cave = Cave(
            1,
            1,
            arrayOf(booleanArrayOf(false))
        )

        val next = simulator.nextStep(cave, birthLimit = 3, deathLimit = 3)

        assertEquals(true, next.cells[0][0])
    }
}
