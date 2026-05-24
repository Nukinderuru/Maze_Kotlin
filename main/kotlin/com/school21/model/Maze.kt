package com.school21.model

import com.school21.service.ValidationConstants

/**
 * Represents a rectangular maze with specified dimensions and wall configurations.
 *
 * @property rows The number of rows in the maze. Must be at least [MIN_SIZE].
 * @property cols The number of columns in the maze. Must be at least [MIN_SIZE].
 * @property rightWalls A 2D array indicating the presence of right-side walls for each cell.
 * Each nested array must have a size equal to [cols], and the number of arrays must match [rows].
 * @property bottomWalls A 2D array indicating the presence of bottom-side walls for each cell.
 * Each nested array must have a size equal to [cols], and the number of arrays must match [rows].
 *
 * @constructor Checks the validity of the input parameters, ensuring they conform to the maze constraints.
 * Throws an [IllegalArgumentException] if:
 * - [rows] or [cols] is less than [MIN_SIZE].
 * - The size of [rightWalls] or [bottomWalls] does not match [rows].
 * - Any nested array within [rightWalls] or [bottomWalls] does not match [cols].
 *
 * @see MIN_SIZE The minimum allowed value for [rows] and [cols].
 * @see MAX_SIZE The maximum allowed value for maze dimensions.
 */
data class Maze(
    val rows: Int,
    val cols: Int,
    val rightWalls: Array<BooleanArray>,
    val bottomWalls: Array<BooleanArray>
) {
    init {
        require(rows >= MIN_SIZE) { ValidationConstants.rowCountPositive("Maze") }
        require(cols >= MIN_SIZE) { ValidationConstants.columnCountPositive("Maze") }
        require(rightWalls.size == rows) { ValidationConstants.rightWallRowCountMatch() }
        require(bottomWalls.size == rows) { ValidationConstants.bottomWallRowCountMatch() }
        require(rightWalls.all { it.size == cols }) {
            ValidationConstants.matrixColumnCountMatch("maze")
        }
        require(bottomWalls.all { it.size == cols }) {
            ValidationConstants.matrixColumnCountMatch("maze")
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is Maze) {
            return false
        }
        return rows == other.rows &&
            cols == other.cols &&
            rightWalls.contentDeepEquals(other.rightWalls) &&
            bottomWalls.contentDeepEquals(other.bottomWalls)
    }

    override fun hashCode(): Int {
        var result = rows
        result = 31 * result + cols
        result = 31 * result + rightWalls.contentDeepHashCode()
        result = 31 * result + bottomWalls.contentDeepHashCode()
        return result
    }

    companion object {
        const val MIN_SIZE = 1
        const val MAX_SIZE = 50
    }
}
