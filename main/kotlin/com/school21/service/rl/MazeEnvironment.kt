package com.school21.service.rl

import com.school21.model.AgentAction
import com.school21.model.Cell
import com.school21.model.Maze
import com.school21.service.ValidationConstants

/**
 * Represents a maze environment that agents can navigate through, with defined start and exit points.
 * This class provides functionality for simulating agent actions within the maze and tracking state transitions.
 *
 * @property maze The maze object representing the grid layout and wall configurations.
 * @property exit The cell representing the exit or goal within the maze.
 *
 * @constructor Ensures that the provided exit cell lies within the bounds of the maze dimensions.
 * Throws an [IllegalArgumentException] if the exit cell is outside the valid range.
 */
class MazeEnvironment(
    val maze: Maze,
    val exit: Cell
) {
    init {
        validateCell(exit, "End")
    }

    /**
     * Executes a single step in the maze environment, transitioning from the current state to the next state
     * based on the provided action. Determines the reward and whether the goal state has been reached.
     *
     * @param state The current position of the agent within the maze, represented as a [Cell].
     * @param action The action taken by the agent, represented as an instance of [AgentAction].
     * @param config The configuration governing the Q-Learning process, including rewards and penalties.
     * @return A [Transition] object containing the next state, the reward for the step, and a boolean indicating
     *         whether the goal state has been reached.
     */
    fun step(state: Cell, action: AgentAction, config: QLearningConfig): Transition {
        validateCell(state, "Start")
        val next = if (canMove(state, action)) {
            Cell(state.row + action.rowDelta, state.col + action.colDelta)
        } else {
            state
        }
        val reachedGoal = next == exit
        val reward = when {
            reachedGoal -> config.goalReward
            next == state -> config.invalidMovePenalty
            else -> config.movePenalty
        }
        return Transition(next, reward, reachedGoal)
    }

    /**
     * Generates a list of all possible states (cells) in the maze environment.
     *
     * @return A list of all cells in the maze, where each cell is defined
     *         by its row and column indices, spanning from (0, 0) to the
     *         maximum dimensions of the maze (maze.rows - 1, maze.cols - 1).
     */
    fun allStates(): List<Cell> {
        return buildList {
            for (row in 0 until maze.rows) {
                for (col in 0 until maze.cols) {
                    add(Cell(row, col))
                }
            }
        }
    }

    /**
     * Determines the list of possible actions that an agent can take from the given state
     * based on the maze's boundaries and walls.
     *
     * @param state The current position of the agent in the maze, represented as a [Cell].
     * @return A list of [AgentAction] objects representing all valid actions that the agent can perform
     *         from the given state.
     */
    fun availableActions(state: Cell): List<AgentAction> {
        validateCell(state, "Start")
        return AgentAction.entries.filter { canMove(state, it) }
    }

    /**
     * Determines if an agent can move in a specified direction from the given state.
     *
     * @param state The current position of the agent, represented as a [Cell].
     * @param action The action the agent intends to perform, represented as an [AgentAction].
     * @return `true` if the move is valid based on the maze's walls and bounds; `false` otherwise.
     */
    fun canMove(state: Cell, action: AgentAction): Boolean {
        return when (action) {
            AgentAction.UP -> state.row > 0 && !maze.bottomWalls[state.row - 1][state.col]
            AgentAction.DOWN -> state.row < maze.rows - 1 && !maze.bottomWalls[state.row][state.col]
            AgentAction.LEFT -> state.col > 0 && !maze.rightWalls[state.row][state.col - 1]
            AgentAction.RIGHT -> state.col < maze.cols - 1 && !maze.rightWalls[state.row][state.col]
        }
    }

    private fun validateCell(cell: Cell, prefix: String) {
        require(cell.row in 0 until maze.rows) { ValidationConstants.rowRange(prefix, maze.rows) }
        require(cell.col in 0 until maze.cols) { ValidationConstants.columnRange(prefix, maze.cols) }
    }

    /**
     * Represents the result of a state transition in the maze environment.
     *
     * @property nextState The next state (cell) the agent transitions to after performing an action.
     * @property reward The numerical reward associated with the transition, which may reflect the
     *         agent's progress towards the goal, penalties, or other reinforcement learning factors.
     * @property reachedGoal A boolean indicating whether the goal state has been reached as a
     *         result of the transition.
     */
    data class Transition(
        val nextState: Cell,
        val reward: Double,
        val reachedGoal: Boolean
    )
}
