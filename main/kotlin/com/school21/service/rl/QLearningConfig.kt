package com.school21.service.rl

/**
 * Configuration class for implementing the Q-Learning algorithm.
 *
 * @property learningRate The rate at which the algorithm updates Q-values. Must be within the range [0, 1].
 * @property discountFactor The factor that determines the importance of future rewards. Must be within the range [0, 1].
 * @property explorationRate The probability of choosing a random action instead of the best-known action. Must be within the range [0, 1].
 * @property episodes The number of training episodes to run. Must be a positive integer.
 * @property maxStepsPerEpisode The maximum number of steps allowed per episode. Must be a positive integer.
 * @property goalReward The reward given for reaching the goal state.
 * @property movePenalty The penalty applied for each movement step towards the goal.
 * @property invalidMovePenalty The penalty applied for making an invalid move.
 */
data class QLearningConfig(
    val learningRate: Double = 0.1,
    val discountFactor: Double = 0.9,
    val explorationRate: Double = 0.2,
    val episodes: Int = 5000,
    val maxStepsPerEpisode: Int,
    val goalReward: Double = 100.0,
    val movePenalty: Double = -1.0,
    val invalidMovePenalty: Double = -5.0
) {
    init {
        require(learningRate in 0.0..1.0) { "Learning rate must be between 0 and 1" }
        require(discountFactor in 0.0..1.0) { "Discount factor must be between 0 and 1" }
        require(explorationRate in 0.0..1.0) { "Exploration rate must be between 0 and 1" }
        require(episodes > 0) { "Episode count must be positive" }
        require(maxStepsPerEpisode > 0) { "Max steps per episode must be positive" }
    }

    companion object {
        fun defaultFor(mazeRows: Int, mazeCols: Int): QLearningConfig {
            val area = mazeRows * mazeCols
            return QLearningConfig(
                episodes = maxOf(5000, area * 6),
                maxStepsPerEpisode = minOf(area * 4, 4000)
            )
        }
    }
}
