package com.school21.service.rl

import com.school21.model.AgentAction
import com.school21.model.Cell

/**
 * Provides a policy for selecting the best action based on Q-values
 * for a given state in a grid-like environment.
 *
 * The Q-values represent the expected rewards for taking specific actions
 * from a given state. This class determines the action with the highest
 * expected reward for the provided state.
 */
object QValuePolicy {
    fun bestAction(
        qValues: Array<Array<DoubleArray>>,
        state: Cell,
        availableActions: List<AgentAction>,
    ): AgentAction {
        require(availableActions.isNotEmpty()) { "At least one action must be available" }
        val scores = qValues[state.row][state.col]
        var bestAction = availableActions.first()
        var bestScore = scores[bestAction.ordinal]

        for (action in availableActions.drop(1)) {
            val score = scores[action.ordinal]
            if (score > bestScore) {
                bestScore = score
                bestAction = action
            }
        }

        return bestAction
    }

    /**
     * Computes the maximum Q-value for a given state and set of available actions.
     *
     * @param qValues A 3D array containing Q-values, where the first two dimensions
     * represent the grid (rows and columns) and the third dimension corresponds to
     * actions.
     * @param state The current state represented as a [Cell], specifying the row
     * and column within the Q-value grid.
     * @param availableActions A list of [AgentAction] objects representing the
     * actions that are available to the agent in the current state.
     * @return The maximum Q-value among the available actions for the given state.
     * Returns 0.0 if no actions are available.
     */
    fun maxQValue(
        qValues: Array<Array<DoubleArray>>,
        state: Cell,
        availableActions: List<AgentAction>,
    ): Double {
        if (availableActions.isEmpty()) {
            return 0.0
        }
        return availableActions.maxOf { action -> qValues[state.row][state.col][action.ordinal] }
    }

    /**
     * Determines the order of actions based on their Q-values for a given state and
     * returns the actions in descending order of preference.
     *
     * @param qValues A 3D array representing the Q-values indexed by row, column,
     * and action ordinal values. Each Q-value indicates the desirability of performing
     * a specific action in a specific state.
     * @param state The current state represented as a [Cell], consisting of row and column
     * indexes in the grid-like environment.
     * @param availableActions A list of [AgentAction]s that represent the possible
     * actions the agent can take from the given state.
     * @return A list of [AgentAction]s ordered by descending Q-values, indicating
     * the most desirable actions first.
     */
    fun orderedActions(
        qValues: Array<Array<DoubleArray>>,
        state: Cell,
        availableActions: List<AgentAction>,
    ): List<AgentAction> {
        return availableActions.sortedByDescending { action -> qValues[state.row][state.col][action.ordinal] }
    }
}
