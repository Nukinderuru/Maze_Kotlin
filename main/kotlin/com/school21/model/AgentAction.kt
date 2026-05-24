package com.school21.model

/**
 * Represents an action that an agent can perform in a grid-like environment.
 *
 * @property rowDelta The change in the row position resulting from the action.
 * A negative value indicates an upward movement, and a positive value indicates a downward movement.
 * @property colDelta The change in the column position resulting from the action.
 * A negative value indicates a leftward movement, and a positive value indicates a rightward movement.
 *
 * The actions available are:
 * - `UP`: Moves the agent one cell upward.
 * - `DOWN`: Moves the agent one cell downward.
 * - `LEFT`: Moves the agent one cell leftward.
 * - `RIGHT`: Moves the agent one cell rightward.
 */
enum class AgentAction(
    val rowDelta: Int,
    val colDelta: Int
) {
    UP(-1, 0),
    DOWN(1, 0),
    LEFT(0, -1),
    RIGHT(0, 1)
}
