package com.school21.service

import com.school21.model.Maze
import java.nio.file.Files
import java.nio.file.Path

/**
 * A utility class responsible for writing maze configurations to a file.
 *
 * This class provides functionality to serialize a given [Maze] object into
 * a textual representation and save it to the specified file path. The format
 * includes:
 * - The maze dimensions: number of rows and columns.
 * - The right wall matrix, represented as rows of "1" (present) and "0" (absent).
 * - The bottom wall matrix, represented similarly to the right wall matrix.
 *
 * Functions:
 * - `write`: Accepts a [Maze] object and a file [Path], serializing the maze
 *   content into a readable string format and writing it to the specified file.
 *
 * Usage scenarios include:
 * - Storing maze configurations for later use.
 * - Exporting generated or manually created mazes to be shared with different systems.
 *
 * The file format is line-separated, with the following structure:
 * ```
 * <rows> <cols>
 * <right wall matrix>
 *
 * <bottom wall matrix>
 * ```
 *
 * Example:
 * ```
 * 3 3
 * 0 1 0
 * 1 1 0
 * 0 0 1
 *
 * 1 0 0
 * 1 1 0
 * 0 0 0
 * ```
 */
class MazeFileWriter {
    /**
     * Writes the serialized representation of a maze to a specified file path.
     *
     * The serialized maze includes:
     * - The maze dimensions (number of rows and columns).
     * - The right wall matrix: a binary representation where "1" indicates a present wall and "0" indicates no wall.
     * - A blank line separator.
     * - The bottom wall matrix: a binary representation similar to the right wall matrix.
     *
     * @param maze The [Maze] object containing the maze's dimensions and wall configurations.
     * @param path The target file path where the maze will be written.
     */
    fun write(maze: Maze, path: Path) {
        val content = buildString {
            appendLine("${maze.rows} ${maze.cols}")
            appendMatrix(maze.rightWalls)
            appendLine()
            appendMatrix(maze.bottomWalls)
        }
        Files.writeString(path, content)
    }

    private fun StringBuilder.appendMatrix(matrix: Array<BooleanArray>) {
        for (row in matrix) {
            appendLine(row.joinToString(" ") { if (it) "1" else "0" })
        }
    }
}
