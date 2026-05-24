package com.school21.service.generator

import com.school21.model.Maze
import com.school21.service.validator.MazeValidator
import kotlin.random.Random

/**
 * Generates a rectangular maze using the Eller’s algorithm. This algorithm builds the maze row by
 * row while dynamically managing cell connectivity through set assignments and random wall removals.
 *
 * @constructor Creates an instance of EllerMazeGenerator.
 * @param random The pseudorandom number generator used for determining wall removal and set transitions.
 * Defaults to [Random.Default].
 */
class EllerMazeGenerator(
    private val random: Random = Random.Default
) : MazeGenerator {
    /**
     * Generates a maze with the specified number of rows and columns using Eller's algorithm.
     * The resulting maze is represented as a grid with walls separating cells.
     *
     * @param rows The number of rows in the maze. Must be between [Maze.MIN_SIZE] and [Maze.MAX_SIZE].
     * @param cols The number of columns in the maze. Must be between [Maze.MIN_SIZE] and [Maze.MAX_SIZE].
     * @return A [Maze] instance containing the generated maze with wall configurations.
     *
     * @throws IllegalArgumentException If the dimensions are outside the valid range.
     */
    override fun generate(rows: Int, cols: Int): Maze {
        MazeValidator.validateDimensions(rows, cols)

        val rightWalls = Array(rows) { BooleanArray(cols) { true } }
        val bottomWalls = Array(rows) { BooleanArray(cols) { true } }
        var currentSets = IntArray(cols)
        var nextSetId = 1

        for (row in 0 until rows) {
            assignMissingSets(currentSets) { nextSetId++ }

            if (row == rows - 1) {
                finishLastRow(currentSets, rightWalls[row])
                continue
            }

            mergeAdjacentSets(currentSets, rightWalls[row])
            currentSets = createNextRowSets(currentSets, bottomWalls[row])
        }

        return Maze(rows, cols, rightWalls, bottomWalls)
    }

    private fun assignMissingSets(currentSets: IntArray, nextSetId: () -> Int) {
        for (index in currentSets.indices) {
            if (currentSets[index] == 0) {
                currentSets[index] = nextSetId()
            }
        }
    }

    private fun finishLastRow(currentSets: IntArray, rightWalls: BooleanArray) {
        for (col in 0 until currentSets.lastIndex) {
            if (currentSets[col] != currentSets[col + 1]) {
                rightWalls[col] = false
                mergeSetIds(currentSets, currentSets[col + 1], currentSets[col])
            }
        }
    }

    private fun mergeAdjacentSets(currentSets: IntArray, rightWalls: BooleanArray) {
        for (col in 0 until currentSets.lastIndex) {
            val sameSet = currentSets[col] == currentSets[col + 1]
            val shouldMerge = !sameSet && random.nextBoolean()
            if (shouldMerge) {
                rightWalls[col] = false
                mergeSetIds(currentSets, currentSets[col + 1], currentSets[col])
            }
        }
    }

    private fun createNextRowSets(currentSets: IntArray, bottomWalls: BooleanArray): IntArray {
        val nextRowSets = IntArray(currentSets.size)
        val groups = linkedMapOf<Int, MutableList<Int>>()

        for (col in currentSets.indices) {
            groups.getOrPut(currentSets[col]) { mutableListOf() }.add(col)
        }

        for (indices in groups.values) {
            val passages = indices.filter { random.nextBoolean() }.ifEmpty {
                listOf(indices.random(random))
            }
            for (col in indices) {
                val opensDown = col in passages
                bottomWalls[col] = !opensDown
                if (opensDown) {
                    nextRowSets[col] = currentSets[col]
                }
            }
        }

        return nextRowSets
    }

    private fun mergeSetIds(currentSets: IntArray, fromSetId: Int, toSetId: Int) {
        for (index in currentSets.indices) {
            if (currentSets[index] == fromSetId) {
                currentSets[index] = toSetId
            }
        }
    }
}
