package com.school21.service.rl

import com.school21.model.Cell
import com.school21.model.Maze
import com.school21.service.MazeSolver
import com.school21.service.generator.EllerMazeGenerator
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class QLearningTrainerTest {
    private val maze = Maze(
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

    @Test
    fun `trainer learns routes to fixed exit from every start cell`() {
        val trainer = QLearningTrainer(Random(7))
        val runner = MazeAgentRunner()
        val solver = MazeSolver()
        val exit = Cell(1, 1)
        val agent = trainer.train(
            maze,
            exit,
            QLearningConfig(
                episodes = 6000,
                explorationRate = 0.25,
                maxStepsPerEpisode = 16,
            ),
        )

        for (row in 0 until maze.rows) {
            for (col in 0 until maze.cols) {
                val start = Cell(row, col)
                val learnedRoute = runner.buildRoute(agent, start)
                val optimalRoute = solver.solve(maze, start, exit)
                assertEquals(exit, learnedRoute.last())
                assertEquals(optimalRoute.size, learnedRoute.size)
            }
        }
    }

    @Test
    fun `trainer and runner build a route on larger generated maze`() {
        val maze = EllerMazeGenerator(Random(21)).generate(30, 30)
        val exit = Cell(29, 29)
        val trainer = QLearningTrainer(Random(21))
        val runner = MazeAgentRunner()

        val agent = trainer.train(maze, exit)
        val route = runner.buildRoute(agent, Cell(0, 0))

        assertEquals(exit, route.last())
    }
}
