package com.school21.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class MazeTest {
    @Test
    fun `mazes with identical wall contents are equal`() {
        val left = Maze(
            rows = 2,
            cols = 2,
            rightWalls = arrayOf(
                booleanArrayOf(false, true),
                booleanArrayOf(true, true),
            ),
            bottomWalls = arrayOf(
                booleanArrayOf(true, false),
                booleanArrayOf(true, true),
            ),
        )
        val right = Maze(
            rows = 2,
            cols = 2,
            rightWalls = arrayOf(
                booleanArrayOf(false, true),
                booleanArrayOf(true, true),
            ),
            bottomWalls = arrayOf(
                booleanArrayOf(true, false),
                booleanArrayOf(true, true),
            ),
        )

        assertEquals(left, right)
        assertEquals(left.hashCode(), right.hashCode())
    }

    @Test
    fun `mazes with different wall contents are not equal`() {
        val left = Maze(
            rows = 1,
            cols = 2,
            rightWalls = arrayOf(booleanArrayOf(false, true)),
            bottomWalls = arrayOf(booleanArrayOf(true, true)),
        )
        val right = Maze(
            rows = 1,
            cols = 2,
            rightWalls = arrayOf(booleanArrayOf(true, true)),
            bottomWalls = arrayOf(booleanArrayOf(true, true)),
        )

        assertNotEquals(left, right)
    }
}
