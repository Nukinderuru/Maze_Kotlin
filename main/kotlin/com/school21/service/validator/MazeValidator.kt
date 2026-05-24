package com.school21.service.validator

import com.school21.model.Maze
import com.school21.service.ValidationConstants

/**
 * Validates maze dimensions to ensure they fall within the acceptable range.
 *
 * This utility object provides a method to validate the number of rows and columns
 * in a maze configuration. The dimensions are verified against the predefined
 * minimum and maximum size constraints specified in the [Maze] class.
 *
 * Throws an [IllegalArgumentException] if the dimensions are out of bounds.
 *
 * Example usage includes:
 * - Validating dimensions in a file parser when loading a maze file.
 * - Validating dimensions when calculating maze render metrics.
 *
 * Functions:
 * - `validateDimensions(rows: Int, cols: Int)`:
 *   Ensures that the specified number of rows and columns fall within
 *   [Maze.MIN_SIZE] and [Maze.MAX_SIZE].
 */
object MazeValidator {
    fun validateDimensions(rows: Int, cols: Int) {
        require(rows in Maze.MIN_SIZE..Maze.MAX_SIZE) { ValidationConstants.mazeRowRange(Maze.MIN_SIZE, Maze.MAX_SIZE) }
        require(cols in Maze.MIN_SIZE..Maze.MAX_SIZE) { ValidationConstants.mazeColumnRange(Maze.MIN_SIZE, Maze.MAX_SIZE) }
    }
}
