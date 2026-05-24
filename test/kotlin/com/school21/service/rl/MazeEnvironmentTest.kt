package com.school21.service.rl

import com.school21.model.AgentAction
import com.school21.model.Cell
import com.school21.model.Maze
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MazeEnvironmentTest {
    private val maze = Maze(
        rows = 2,
        cols = 2,
        rightWalls = arrayOf(
            booleanArrayOf(false, true),
            booleanArrayOf(true, true),
        ),
        bottomWalls = arrayOf(
            booleanArrayOf(false, false),
            booleanArrayOf(true, true),
        ),
    )

    @Test
    fun `canMove respects walls and bounds`() {
        val environment = MazeEnvironment(maze, Cell(1, 1))

        assertTrue(environment.canMove(Cell(0, 0), AgentAction.RIGHT))
        assertTrue(environment.canMove(Cell(0, 0), AgentAction.DOWN))
        assertFalse(environment.canMove(Cell(0, 0), AgentAction.LEFT))
        assertFalse(environment.canMove(Cell(1, 0), AgentAction.RIGHT))
    }

    @Test
    fun `step returns invalid move penalty when blocked`() {
        val environment = MazeEnvironment(maze, Cell(1, 1))
        val config = QLearningConfig.defaultFor(maze.rows, maze.cols)

        val transition = environment.step(Cell(1, 0), AgentAction.RIGHT, config)

        assertEquals(Cell(1, 0), transition.nextState)
        assertEquals(config.invalidMovePenalty, transition.reward)
        assertFalse(transition.reachedGoal)
    }

    @Test
    fun `step returns goal reward when exit is reached`() {
        val environment = MazeEnvironment(maze, Cell(1, 1))
        val config = QLearningConfig.defaultFor(maze.rows, maze.cols)

        val transition = environment.step(Cell(0, 1), AgentAction.DOWN, config)

        assertEquals(Cell(1, 1), transition.nextState)
        assertEquals(config.goalReward, transition.reward)
        assertTrue(transition.reachedGoal)
    }
}
