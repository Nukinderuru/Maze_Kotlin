package com.school21.service

import com.school21.model.Cell
import com.school21.model.Maze
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MazeSolverTest {
    private val solver = MazeSolver()

    @Test
    fun `solver finds shortest path in maze`() {
        val maze = Maze(
            rows = 3,
            cols = 3,
            rightWalls = arrayOf(
                booleanArrayOf(false, true, true),
                booleanArrayOf(false, true, true),
                booleanArrayOf(false, false, true),
            ),
            bottomWalls = arrayOf(
                booleanArrayOf(true, false, false),
                booleanArrayOf(false, true, false),
                booleanArrayOf(true, true, true),
            ),
        )

        val path = solver.solve(maze, Cell(0, 0), Cell(2, 2))

        assertEquals(Cell(0, 0), path.first())
        assertEquals(Cell(2, 2), path.last())
        assertEquals(7, path.size)
        for (index in 0 until path.lastIndex) {
            val current = path[index]
            val next = path[index + 1]
            val distance = kotlin.math.abs(current.row - next.row) + kotlin.math.abs(current.col - next.col)
            assertEquals(1, distance)
        }
    }

    @Test
    fun `solver returns one-cell path when start equals end`() {
        val maze = Maze(
            rows = 1,
            cols = 1,
            rightWalls = arrayOf(booleanArrayOf(true)),
            bottomWalls = arrayOf(booleanArrayOf(true)),
        )

        val path = solver.solve(maze, Cell(0, 0), Cell(0, 0))

        assertEquals(listOf(Cell(0, 0)), path)
    }

    @Test
    fun `solver rejects out of bounds coordinates`() {
        val maze = Maze(
            rows = 2,
            cols = 2,
            rightWalls = arrayOf(
                booleanArrayOf(false, true),
                booleanArrayOf(false, true),
            ),
            bottomWalls = arrayOf(
                booleanArrayOf(false, false),
                booleanArrayOf(true, true),
            ),
        )

        val exception = assertFailsWith<IllegalArgumentException> {
            solver.solve(maze, Cell(2, 0), Cell(0, 0))
        }

        assertEquals(ValidationConstants.rowRange("Start", 2), exception.message)
    }

    @Test
    fun `solver reports missing path in disconnected maze`() {
        val maze = Maze(
            rows = 2,
            cols = 2,
            rightWalls = arrayOf(
                booleanArrayOf(true, true),
                booleanArrayOf(true, true),
            ),
            bottomWalls = arrayOf(
                booleanArrayOf(true, true),
                booleanArrayOf(true, true),
            ),
        )

        val exception = assertFailsWith<IllegalStateException> {
            solver.solve(maze, Cell(0, 0), Cell(1, 1))
        }

        assertEquals(ValidationConstants.NO_PATH_EXISTS, exception.message)
    }
}
