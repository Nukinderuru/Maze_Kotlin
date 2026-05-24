package com.school21.service.generator

import com.school21.model.Cave
import com.school21.service.validator.CaveValidator
import kotlin.random.Random

/**
 * Generates a randomized cave structure based on specified dimensions and cell initialization probability.
 *
 * This class is responsible for creating a cave represented by a 2D boolean matrix,
 * where each cell indicates if it's "alive" (true) or "dead" (false).
 * The probability of a cell being alive is determined by the initialization chance.
 *
 * Primary functionality includes:
 * - Validation of input parameters, such as dimensions and initialization chance.
 * - Randomized initialization of cave cells based on the provided chance.
 *
 * Methods:
 * - `initialize(rows: Int, cols: Int, chance: Int): Cave`:
 *   Generates and returns a new `Cave` instance with the specified properties.
 */
class CaveGenerator(
    private val random: Random = Random.Default,
) {
    /**
     * Initializes and generates a new randomized 2D cave structure with the given dimensions and initialization probability.
     *
     * The resulting cave is represented as a 2D boolean matrix, where each cell is set to `true` (alive)
     * with a probability determined by the given chance, and otherwise `false` (dead).
     *
     * @param rows The number of rows in the cave. Must be between [Cave.MIN_SIZE] and [Cave.MAX_SIZE].
     * @param cols The number of columns in the cave. Must be between [Cave.MIN_SIZE] and [Cave.MAX_SIZE].
     * @param chance The probability (as a percentage) that any given cell in the cave will be alive (`true`).
     * Must be between 0 and 100.
     * @return A new [Cave] instance representing the initialized 2D cave structure.
     * @throws IllegalArgumentException If the dimensions or chance do not adhere to the expected constraints.
     */
    fun initialize(rows: Int, cols: Int, chance: Int): Cave {
        CaveValidator.validateDimensions(rows, cols)
        CaveValidator.validateChance(chance)

        val cells = Array(rows) {
            BooleanArray(cols) { random.nextInt(100) < chance }
        }
        return Cave(rows, cols, cells)
    }
}
