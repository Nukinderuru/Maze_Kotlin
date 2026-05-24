package com.school21.model

import com.school21.service.ValidationConstants

/**
 * Represents a cave with specified dimensions and a boolean matrix indicating cell states.
 *
 * @property rows The number of rows in the cave. Must be at least [MIN_SIZE].
 * @property cols The number of columns in the cave. Must be at least [MIN_SIZE].
 * @property cells A 2D boolean array representing the state of each cell in the cave.
 * Each nested array must have a size equal to [cols], and the number of arrays must match [rows].
 *
 * @constructor Ensures the input parameters conform to cave constraints.
 * Throws an [IllegalArgumentException] if:
 * - [rows] or [cols] is less than [MIN_SIZE].
 * - The size of [cells] does not match [rows].
 * - Any nested array within [cells] does not match [cols].
 *
 * @see MIN_SIZE The minimum allowed value for [rows] and [cols].
 * @see MAX_SIZE The maximum allowed value for cave dimensions.
 */
data class Cave(
    val rows: Int,
    val cols: Int,
    val cells: Array<BooleanArray>
) {
    init {
        require(rows >= MIN_SIZE) { ValidationConstants.rowCountPositive("Cave") }
        require(cols >= MIN_SIZE) { ValidationConstants.columnCountPositive("Cave") }
        require(cells.size == rows) { ValidationConstants.matrixRowCountMatch("Cave") }
        require(cells.all { it.size == cols }) { ValidationConstants.matrixColumnCountMatch("cave") }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is Cave) {
            return false
        }
        return rows == other.rows && cols == other.cols && cells.contentDeepEquals(other.cells)
    }

    override fun hashCode(): Int {
        var result = rows
        result = 31 * result + cols
        result = 31 * result + cells.contentDeepHashCode()
        return result
    }

    companion object {
        const val MIN_SIZE = 1
        const val MAX_SIZE = 50
    }
}
