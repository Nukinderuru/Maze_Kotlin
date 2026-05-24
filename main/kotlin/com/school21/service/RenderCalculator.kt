package com.school21.service

import com.school21.service.validator.MazeValidator

/**
 * Represents shared rendering metrics for grid-based views.
 *
 * This data class encapsulates properties necessary for rendering a maze,
 * including the size of the entire field, the thickness of the walls, and
 * the dimensions of individual cells within the maze. These metrics are
 * typically derived from the maze's dimensions and other rendering constants.
 *
 * @property fieldSize The overall size of the rendering field.
 * @property wallThickness The thickness of the walls between cells.
 * @property cellWidth The width of an individual cell within the maze.
 * @property cellHeight The height of an individual cell within the maze.
 */
data class RenderMetrics(
    val fieldSize: Double,
    val wallThickness: Double,
    val cellWidth: Double,
    val cellHeight: Double
)

/**
 * Provides utility methods for calculating rendering metrics for grid-based views.
 *
 * This object includes constants for rendering parameters and a method to calculate
 * the necessary metrics to correctly render a maze based on its dimensions.
 *
 * Constants:
 * - [FIELD_SIZE]: The size of the entire rendering field (e.g., canvas).
 * - [WALL_THICKNESS]: The thickness of walls between cells in the rendered maze.
 *
 * Functions:
 * - `calculate(rows: Int, cols: Int)`: Computes rendering metrics, such as cell dimensions,
 *   by taking the number of rows and columns of the maze as input.
 *
 * Throws:
 * - [IllegalArgumentException] if the provided dimensions are outside the acceptable range
 *   defined in [com.school21.service.validator.MazeValidator.validateDimensions].
 */
object RenderCalculator {
    const val FIELD_SIZE = 500.0
    const val WALL_THICKNESS = 2.0

    /**
     * Calculates the rendering metrics for a maze based on its dimensions.
     *
     * This method performs the necessary validation on the maze's dimensions
     * and computes the metrics required for rendering the maze, such as the size
     * of each cell and the thickness of its walls.
     *
     * @param rows The number of rows in the maze. Must be within the valid range defined by `Maze.MIN_SIZE` and `Maze.MAX_SIZE`.
     * @param cols The number of columns in the maze. Must be within the valid range defined by `Maze.MIN_SIZE` and `Maze.MAX_SIZE`.
     * @return An instance of [RenderMetrics] containing the calculated rendering metrics.
     * @throws IllegalArgumentException If the provided dimensions are outside the valid range.
     */
    fun calculate(rows: Int, cols: Int): RenderMetrics {
        MazeValidator.validateDimensions(rows, cols)
        return RenderMetrics(
            fieldSize = FIELD_SIZE,
            wallThickness = WALL_THICKNESS,
            cellWidth = FIELD_SIZE / cols,
            cellHeight = FIELD_SIZE / rows
        )
    }
}
