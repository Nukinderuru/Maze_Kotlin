package com.school21.model

/**
 * Represents a trained agent designed to navigate a maze environment using Q-learning.
 *
 * @property maze The maze environment that the agent is trained to navigate.
 * This is a [Maze] object containing the maze's dimensions and wall configurations.
 * @property exit The designated exit cell within the maze. The agent's goal is to reach this cell.
 * @property qValues A 3D array representing the Q-values for all possible state-action pairs.
 * Each Q-value corresponds to the learned reward expectation for taking a specific action from a
 * specific state in the maze environment.
 *
 * @constructor Initializes the trained maze agent with its associated maze, exit point,
 * and its trained Q-values. Performs structural equality checks to ensure proper functionality
 * with other [TrainedMazeAgent] instances.
 */
data class TrainedMazeAgent(
    val maze: Maze,
    val exit: Cell,
    val qValues: Array<Array<DoubleArray>>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TrainedMazeAgent

        if (maze != other.maze) return false
        if (exit != other.exit) return false
        if (!qValues.contentDeepEquals(other.qValues)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = maze.hashCode()
        result = 31 * result + exit.hashCode()
        result = 31 * result + qValues.contentDeepHashCode()
        return result
    }
}
