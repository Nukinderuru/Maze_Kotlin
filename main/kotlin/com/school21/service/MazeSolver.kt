package com.school21.service

import com.school21.model.Cell
import com.school21.model.Maze

/**
 * Provides functionality to solve a rectangular maze using the Breadth-First Search (BFS) algorithm.
 *
 * This class performs pathfinding from a given starting cell to an ending cell within a maze
 * represented by the [Maze] data structure. It identifies the shortest path (if one exists)
 * and returns the sequence of cells that form it.
 *
 * Throws:
 * - [IllegalArgumentException] if the start or end cell coordinates are invalid (out of the maze's bounds).
 * - [IllegalStateException] if no valid path exists between the start and end cells.
 *
 * Primary functionality includes:
 * - Validation of cell coordinates to ensure they lie within the maze's bounds.
 * - Determination of neighboring cells accessible from the current cell based on wall constraints in the maze.
 * - Reconstruction of the path from the ending cell to the starting cell after solving.
 *
 * Methods:
 * - `solve(maze: Maze, start: Cell, end: Cell): List<Cell>`:
 *   Solves the maze to find the shortest path from the specified starting cell to the ending cell.
 */
class MazeSolver {
    /**
     * Solves the given maze to find a path from the start cell to the end cell.
     *
     * This method uses a breadth-first search algorithm to determine the shortest
     * path from the start cell to the end cell in the given maze. If no path exists,
     * an exception is thrown.
     *
     * @param maze The maze to be solved, represented as a grid with walls.
     * @param start The starting cell from where the pathfinding begins.
     * @param end The target cell where the pathfinding terminates.
     * @return A list of cells representing the path from the start cell to the end cell.
     * If the start cell and end cell are the same, the returned list contains only the start cell.
     * @throws IllegalStateException If no path exists between the start and end cells.
     */
    fun solve(maze: Maze, start: Cell, end: Cell): List<Cell> {
        validateCell(maze, start, "Start")
        validateCell(maze, end, "End")

        if (start == end) {
            return listOf(start)
        }

        val parents = Array(maze.rows) { arrayOfNulls<Cell>(maze.cols) }
        val visited = Array(maze.rows) { BooleanArray(maze.cols) }
        val queue = ArrayDeque<Cell>()
        queue.addLast(start)
        visited[start.row][start.col] = true

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            if (current == end) {
                return restorePath(parents, end)
            }

            for (neighbor in neighbors(maze, current)) {
                if (!visited[neighbor.row][neighbor.col]) {
                    visited[neighbor.row][neighbor.col] = true
                    parents[neighbor.row][neighbor.col] = current
                    queue.addLast(neighbor)
                }
            }
        }

        throw IllegalStateException(ValidationConstants.NO_PATH_EXISTS)
    }

    private fun validateCell(maze: Maze, cell: Cell, prefix: String) {
        require(cell.row in 0 until maze.rows) {
            ValidationConstants.rowRange(prefix, maze.rows)
        }
        require(cell.col in 0 until maze.cols) {
            ValidationConstants.columnRange(prefix, maze.cols)
        }
    }

    private fun neighbors(maze: Maze, cell: Cell): List<Cell> {
        val neighbors = mutableListOf<Cell>()
        val row = cell.row
        val col = cell.col

        if (col < maze.cols - 1 && !maze.rightWalls[row][col]) {
            neighbors.add(Cell(row, col + 1))
        }
        if (col > 0 && !maze.rightWalls[row][col - 1]) {
            neighbors.add(Cell(row, col - 1))
        }
        if (row < maze.rows - 1 && !maze.bottomWalls[row][col]) {
            neighbors.add(Cell(row + 1, col))
        }
        if (row > 0 && !maze.bottomWalls[row - 1][col]) {
            neighbors.add(Cell(row - 1, col))
        }

        return neighbors
    }

    private fun restorePath(parents: Array<Array<Cell?>>, end: Cell): List<Cell> {
        val path = mutableListOf<Cell>()
        var current: Cell? = end

        while (current != null) {
            path.add(current)
            current = parents[current.row][current.col]
        }

        path.reverse()
        return path
    }
}
