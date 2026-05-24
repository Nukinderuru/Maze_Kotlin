package com.school21.service.rl

import com.school21.model.Cell
import com.school21.model.Maze
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MazeAgentRunnerTest {
    @Test
    fun `runner returns one-cell route when start is exit`() {
        val maze = Maze(
            rows = 1,
            cols = 1,
            rightWalls = arrayOf(booleanArrayOf(true)),
            bottomWalls = arrayOf(booleanArrayOf(true)),
        )
        val agent = QLearningTrainer(Random(3)).train(maze, Cell(0, 0), QLearningConfig(episodes = 1, maxStepsPerEpisode = 1))
        val route = MazeAgentRunner().buildRoute(agent, Cell(0, 0))

        assertEquals(listOf(Cell(0, 0)), route)
    }

    @Test
    fun `runner rejects invalid start coordinates`() {
        val maze = Maze(
            rows = 1,
            cols = 1,
            rightWalls = arrayOf(booleanArrayOf(true)),
            bottomWalls = arrayOf(booleanArrayOf(true)),
        )
        val agent = QLearningTrainer(Random(3)).train(maze, Cell(0, 0), QLearningConfig(episodes = 1, maxStepsPerEpisode = 1))

        val exception = assertFailsWith<IllegalArgumentException> {
            MazeAgentRunner().buildRoute(agent, Cell(1, 0))
        }

        assertEquals(com.school21.service.ValidationConstants.rowRange("Start", 1), exception.message)
    }
}
