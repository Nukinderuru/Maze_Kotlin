package com.school21.service.rl

import com.school21.model.AgentAction
import com.school21.model.Cell
import com.school21.model.Maze
import com.school21.model.TrainedMazeAgent
import kotlin.random.Random

/**
 * A trainer class implementing the Q-learning algorithm for training an agent to navigate a maze.
 *
 * The Q-learning algorithm allows an agent to learn an optimal policy for traversing the maze
 * by using iterative updates of Q-values based on observed rewards and transitions.
 *
 * @constructor Initializes the trainer with an optional random number generator.
 * When not provided, it defaults to [Random.Default].
 *
 * @param random The random number generator used for selecting actions during exploration.
 */
class QLearningTrainer(
    private val random: Random = Random.Default
) {
    /**
     * Trains an agent to navigate a maze using the Q-learning algorithm.
     *
     * @param maze The maze environment the agent will be trained on.
     * @param exit The target cell that the agent aims to reach in the maze.
     * @param config The configuration for the Q-learning training, including parameters such as learning rate,
     * exploration rate, and episode count. A default configuration is used if not specified.
     * @return A trained maze agent with the learned Q-values, which can be used to navigate the maze.
     */
    fun train(
        maze: Maze,
        exit: Cell,
        config: QLearningConfig = QLearningConfig.defaultFor(maze.rows, maze.cols)
    ): TrainedMazeAgent {
        val environment = MazeEnvironment(maze, exit)
        val qValues = Array(maze.rows) { Array(maze.cols) { DoubleArray(AgentAction.entries.size) } }
        val possibleStarts = environment.allStates().filter { it != exit }

        repeat(config.episodes) { episode ->
            val start = if (possibleStarts.isEmpty()) exit else possibleStarts.random(random)
            var currentState = start
            val explorationRate = (config.explorationRate * (1.0 - episode.toDouble() / config.episodes)).coerceAtLeast(0.01)

            repeat(config.maxStepsPerEpisode) {
                if (currentState == exit) {
                    return@repeat
                }

                val action = chooseAction(environment, qValues, currentState, explorationRate)
                val transition = environment.step(currentState, action, config)
                updateQValue(environment, qValues, currentState, action, transition, config)
                currentState = transition.nextState

                if (transition.reachedGoal) {
                    return@repeat
                }
            }
        }

        return TrainedMazeAgent(maze, exit, qValues)
    }

    private fun chooseAction(
        environment: MazeEnvironment,
        qValues: Array<Array<DoubleArray>>,
        state: Cell,
        explorationRate: Double,
    ): AgentAction {
        val availableActions = environment.availableActions(state)
        if (availableActions.isEmpty()) {
            return AgentAction.UP
        }
        return if (random.nextDouble() < explorationRate) {
            availableActions.random(random)
        } else {
            QValuePolicy.bestAction(qValues, state, availableActions)
        }
    }

    private fun updateQValue(
        environment: MazeEnvironment,
        qValues: Array<Array<DoubleArray>>,
        state: Cell,
        action: AgentAction,
        transition: MazeEnvironment.Transition,
        config: QLearningConfig,
    ) {
        val currentQ = qValues[state.row][state.col][action.ordinal]
        val futureBest = QValuePolicy.maxQValue(
            qValues,
            transition.nextState,
            environment.availableActions(transition.nextState),
        )
        val target = transition.reward + config.discountFactor * futureBest
        qValues[state.row][state.col][action.ordinal] = currentQ + config.learningRate * (target - currentQ)
    }
}
