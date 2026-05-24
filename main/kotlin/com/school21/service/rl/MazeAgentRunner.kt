package com.school21.service.rl

import com.school21.model.Cell
import com.school21.model.TrainedMazeAgent
import com.school21.service.ValidationConstants

/**
 * Executes a maze navigation simulation for a trained agent and builds a route
 * from a starting cell to the exit of the maze.
 */
class MazeAgentRunner {
    /**
     * Constructs a route for a trained maze agent to navigate from a starting cell
     * to the designated exit cell in the maze.
     *
     * @param agent The trained maze agent that contains the maze structure, the exit cell,
     * and the Q-values used to determine the optimal path.
     * @param start The starting cell in the maze, represented as a [Cell]. This is where
     * the route begins.
     * @return A list of [Cell] objects representing the sequence of moves the agent takes
     * to navigate from the starting cell to the exit cell.
     * @throws IllegalStateException If the agent fails to build a route to the exit
     * within the predefined step limit.
     * @throws IllegalArgumentException If the starting cell is outside the valid bounds
     * of the maze.
     */
    fun buildRoute(agent: TrainedMazeAgent, start: Cell): List<Cell> {
        val environment = MazeEnvironment(agent.maze, agent.exit)
        require(start.row in 0 until agent.maze.rows) { ValidationConstants.rowRange("Start", agent.maze.rows) }
        require(start.col in 0 until agent.maze.cols) { ValidationConstants.columnRange("Start", agent.maze.cols) }

        val path = mutableListOf(start)
        val visited = mutableSetOf<Cell>()
        val routeConfig = QLearningConfig.defaultFor(agent.maze.rows, agent.maze.cols)

        if (searchRoute(agent, environment, start, routeConfig, visited, path)) {
            return path
        }

        throw IllegalStateException(ValidationConstants.AGENT_ROUTE_BUILD_FAILED)
    }

    private fun searchRoute(
        agent: TrainedMazeAgent,
        environment: MazeEnvironment,
        current: Cell,
        config: QLearningConfig,
        visited: MutableSet<Cell>,
        path: MutableList<Cell>,
    ): Boolean {
        if (current == agent.exit) {
            return true
        }

        visited.add(current)
        val orderedActions = QValuePolicy.orderedActions(
            agent.qValues,
            current,
            environment.availableActions(current),
        )

        for (action in orderedActions) {
            val next = environment.step(current, action, config).nextState
            if (next in visited) {
                continue
            }

            path.add(next)
            if (searchRoute(agent, environment, next, config, visited, path)) {
                return true
            }
            path.removeLast()
        }

        visited.remove(current)
        return false
    }
}
