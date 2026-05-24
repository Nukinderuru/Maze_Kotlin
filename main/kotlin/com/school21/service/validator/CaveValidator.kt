package com.school21.service.validator

import com.school21.model.Cave
import com.school21.service.ValidationConstants

/**
 * Validates various properties and constraints related to cave generation and behavior.
 *
 * This object provides utility methods to ensure that inputs for cave-related operations,
 * such as dimensions, limits, probabilities, and delays, adhere to defined constraints.
 *
 * Throws:
 * - [IllegalArgumentException] if any validation constraint is violated.
 *
 * Methods:
 * - `validateDimensions(rows: Int, cols: Int)`:
 *   Ensures that the row and column dimensions of a cave are within acceptable ranges.
 * - `validateLimit(limit: Int, name: String)`:
 *   Verifies that a given limit (e.g., number of iterations or similar constraints)
 *   is within the range from 0 to 7.
 * - `validateChance(chance: Int)`:
 *   Validates that the provided chance (as a percentage) falls between 0 and 100.
 * - `validateDelay(delayMs: Long)`:
 *   Ensures that a specified delay value is a positive number.
 */
object CaveValidator {
    const val MIN_CHANCE = 0
    const val MAX_CHANCE = 100
    const val MIN_LIFE_AND_DEATH = 0
    const val MAX_LIFE_AND_DEATH = 7

    /**
     * Validates the dimensions of a cave to ensure they fall within the allowable range.
     *
     * This method checks whether the given row and column values are within the boundaries defined
     * by [Cave.MIN_SIZE] and [Cave.MAX_SIZE]. If the dimensions are invalid, an exception is thrown.
     *
     * @param rows The number of rows in the cave. Must be between [Cave.MIN_SIZE] and [Cave.MAX_SIZE].
     * @param cols The number of columns in the cave. Must be between [Cave.MIN_SIZE] and [Cave.MAX_SIZE].
     * @throws IllegalArgumentException If the row or column size is outside the valid range.
     */
    fun validateDimensions(rows: Int, cols: Int) {
        require(rows in Cave.MIN_SIZE..Cave.MAX_SIZE) {
            ValidationConstants.caveRowRange(Cave.MIN_SIZE, Cave.MAX_SIZE)
        }
        require(cols in Cave.MIN_SIZE..Cave.MAX_SIZE) {
            ValidationConstants.caveColumnRange(Cave.MIN_SIZE, Cave.MAX_SIZE)
        }
    }

    /**
     * Validates that the given limit falls within an acceptable range.
     *
     * This method ensures that the provided limit is within the range of [MIN_LIFE_AND_DEATH] to [MAX_LIFE_AND_DEATH] (inclusive).
     * If the limit is outside this range, an [IllegalArgumentException] is thrown
     * with a message indicating the name of the parameter.
     *
     * @param limit The integer value to validate, representing the limit being checked.
     * @param name A descriptive name for the parameter being validated, included in the error message.
     * @throws IllegalArgumentException If the limit is not within the range of [MIN_LIFE_AND_DEATH] to [MAX_LIFE_AND_DEATH] (inclusive).
     */
    fun validateLimit(limit: Int, name: String) {
        require(limit in MIN_LIFE_AND_DEATH..MAX_LIFE_AND_DEATH) {
            ValidationConstants.limitRange(name, MIN_LIFE_AND_DEATH, MAX_LIFE_AND_DEATH)
        }
    }

    /**
     * Validates that the given chance parameter is within the allowed range.
     *
     * This method ensures that the provided chance value is between [MIN_CHANCE] and [MAX_CHANCE].
     * If the validation fails, it throws an [IllegalArgumentException].
     *
     * @param chance The percentage value to validate, representing a probability.
     * Must be between [MIN_CHANCE] and [MAX_CHANCE], inclusive.
     * @throws IllegalArgumentException If the chance value is outside the valid range.
     */
    fun validateChance(chance: Int) {
        require(chance in MIN_CHANCE..MAX_CHANCE) { ValidationConstants.chanceRange(MIN_CHANCE, MAX_CHANCE) }
    }

    /**
     * Validates that the specified delay value is a positive integer.
     *
     * This method ensures that the delay for an operation, measured in milliseconds,
     * is greater than zero. If the delay value is invalid, an exception is thrown.
     *
     * @param delayMs The delay in milliseconds. Must be a positive integer.
     * @throws IllegalArgumentException If the delay is not greater than zero.
     */
    fun validateDelay(delayMs: Long) {
        require(delayMs > 0) { ValidationConstants.AUTO_STEP_DELAY_POSITIVE }
    }
}
