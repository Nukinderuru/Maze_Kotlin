package com.school21.service

import com.school21.model.Cave
import com.school21.service.validator.CaveValidator

/**
 * Handles simulation of cellular automata within a cave-like structure.
 *
 * This class provides functionality to simulate the next state of a cave based on
 * configurable birth and death limits, along with computing the number of alive
 * neighboring cells for each cell in the cave.
 */
class CaveSimulator {
    /**
     * Computes the next state of the cave based on the rules of cellular automata.
     *
     * The method processes each cell in the given cave and determines its state (alive or dead)
     * in the next step based on the number of alive neighboring cells and the specified
     * birth and death limits. A cell becomes alive if it is dead and has more live neighbors
     * than the birth limit. A cell stays alive if it has at least as many living neighbors
     * as the death limit.
     *
     * @param cave The current state of the cave represented as a 2D grid of boolean values.
     *             True indicates a live cell, and false indicates a dead cell.
     * @param birthLimit The threshold of live neighbors required for a dead cell to become alive.
     *                   Must be validated to fall within an acceptable range.
     * @param deathLimit The minimum number of live neighbors required for a live cell to remain alive.
     *                   Must be validated to fall within an acceptable range.
     * @return A new instance of [Cave] representing the next state of the cave after applying the rules.
     */
    fun nextStep(cave: Cave, birthLimit: Int, deathLimit: Int): Cave {
        CaveValidator.validateLimit(birthLimit, "Birth limit")
        CaveValidator.validateLimit(deathLimit, "Death limit")

        val nextCells = Array(cave.rows) { row ->
            BooleanArray(cave.cols) { col ->
                val aliveNeighbors = countAliveNeighbors(cave, row, col)
                if (cave.cells[row][col]) {
                    aliveNeighbors >= deathLimit
                } else {
                    aliveNeighbors > birthLimit
                }
            }
        }
        return Cave(cave.rows, cave.cols, nextCells)
    }

    private fun countAliveNeighbors(cave: Cave, row: Int, col: Int): Int {
        var alive = 0
        for (rowOffset in -1..1) {
            for (colOffset in -1..1) {
                if (rowOffset == 0 && colOffset == 0) {
                    continue
                }
                val neighborRow = row + rowOffset
                val neighborCol = col + colOffset
                if (neighborRow !in 0 until cave.rows || neighborCol !in 0 until cave.cols) {
                    alive++
                } else if (cave.cells[neighborRow][neighborCol]) {
                    alive++
                }
            }
        }
        return alive
    }
}
