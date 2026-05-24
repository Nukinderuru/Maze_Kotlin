package com.school21.service

import com.school21.model.Maze
import com.school21.service.generator.EllerMazeGenerator
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class EllerMazeGeneratorTest {
    @Test
    fun `generator creates matrices with requested dimensions`() {
        val maze = EllerMazeGenerator(Random(7)).generate(4, 6)

        assertEquals(4, maze.rows)
        assertEquals(6, maze.cols)
        assertEquals(4, maze.rightWalls.size)
        assertTrue(maze.rightWalls.all { it.size == 6 })
        assertEquals(4, maze.bottomWalls.size)
        assertTrue(maze.bottomWalls.all { it.size == 6 })
    }

    @Test
    fun `generator closes outer border`() {
        val maze = EllerMazeGenerator(Random(9)).generate(8, 5)

        for (row in 0 until maze.rows) {
            assertTrue(maze.rightWalls[row][maze.cols - 1])
        }
        for (col in 0 until maze.cols) {
            assertTrue(maze.bottomWalls[maze.rows - 1][col])
        }
    }

    @Test
    fun `generator creates perfect mazes for representative sizes`() {
        val generator = EllerMazeGenerator(Random(42))
        val mazes = listOf(
            generator.generate(1, 1),
            generator.generate(1, 5),
            generator.generate(5, 1),
            generator.generate(2, 2),
            generator.generate(5, 7),
            generator.generate(10, 10),
            generator.generate(50, 50),
        )

        for (maze in mazes) {
            assertPerfectMaze(maze)
        }
    }

    @Test
    fun `generator rejects invalid dimensions`() {
        val generator = EllerMazeGenerator(Random(1))

        assertFailsWith<IllegalArgumentException> {
            generator.generate(0, 5)
        }
        assertFailsWith<IllegalArgumentException> {
            generator.generate(5, 51)
        }
    }

    private fun assertPerfectMaze(maze: Maze) {
        val visited = BooleanArray(maze.rows * maze.cols)
        val queue = ArrayDeque<Int>()
        queue.addLast(0)
        visited[0] = true
        var visitedCount = 0
        var edgeCount = 0

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            visitedCount++
            val row = current / maze.cols
            val col = current % maze.cols

            if (col < maze.cols - 1 && !maze.rightWalls[row][col]) {
                edgeCount++
                val right = current + 1
                if (!visited[right]) {
                    visited[right] = true
                    queue.addLast(right)
                }
            }

            if (col > 0 && !maze.rightWalls[row][col - 1]) {
                val left = current - 1
                if (!visited[left]) {
                    visited[left] = true
                    queue.addLast(left)
                }
            }

            if (row < maze.rows - 1 && !maze.bottomWalls[row][col]) {
                edgeCount++
                val bottom = current + maze.cols
                if (!visited[bottom]) {
                    visited[bottom] = true
                    queue.addLast(bottom)
                }
            }

            if (row > 0 && !maze.bottomWalls[row - 1][col]) {
                val top = current - maze.cols
                if (!visited[top]) {
                    visited[top] = true
                    queue.addLast(top)
                }
            }
        }

        assertEquals(maze.rows * maze.cols, visitedCount)
        assertEquals(maze.rows * maze.cols - 1, edgeCount)
    }
}
