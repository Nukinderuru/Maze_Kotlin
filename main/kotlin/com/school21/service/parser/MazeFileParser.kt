package com.school21.service.parser

import com.school21.model.Maze
import com.school21.service.ValidationConstants
import com.school21.service.exception.MazeFormatException
import com.school21.service.validator.MazeValidator
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path

class MazeFileParser {
    /**
     * Parses a maze representation from the file at the specified path.
     *
     * The file must be structured as follows:
     * - The first line contains the dimensions of the maze (rows and columns) as two integers separated by whitespace.
     * - The next `rows` lines contain binary values (0 or 1) representing the presence of right-side walls for each cell.
     * - The following `rows` lines contain binary values (0 or 1) representing the presence of bottom-side walls for each cell.
     *
     * Blank lines and extraneous whitespace around the content are ignored. The method validates the file's structure
     * and content before constructing the maze.
     *
     * @param path The path to the file containing the maze representation.
     * @return A [Maze] instance representing the parsed maze.
     * @throws MazeFormatException If the file is empty, malformed, or contains invalid dimensions or matrix data.
     */
    fun parse(path: Path): Maze {
        return parseLines(Files.readAllLines(path))
    }

    /**
     * Parses a maze representation from the provided input stream.
     *
     * The input stream must contain a maze file structured as follows:
     * - The first line specifies the dimensions of the maze (rows and columns) as two integers separated by whitespace.
     * - The next `rows` lines contain binary values (0 or 1) representing the presence of right-side walls for each cell.
     * - The following `rows` lines contain binary values (0 or 1) representing the presence of bottom-side walls for each cell.
     *
     * Leading and trailing whitespace, as well as blank lines, are ignored. The method ensures that the maze file is
     * well-formed and validates its content before constructing the maze.
     *
     * @param inputStream The input stream containing the maze representation.
     * @return A [Maze] instance representing the parsed maze.
     * @throws MazeFormatException If the input stream is empty, malformed, or contains invalid dimensions or wall data.
     */
    fun parse(inputStream: InputStream): Maze {
        return inputStream.bufferedReader().useLines { lines ->
            parseLines(lines.toList())
        }
    }

    private fun parseLines(rawLines: List<String>): Maze {
        val lines = rawLines
            .map(String::trim)
            .filter(String::isNotEmpty)

        if (lines.isEmpty()) {
            throw MazeFormatException(ValidationConstants.MAZE_FILE_EMPTY)
        }

        val dimensionTokens = lines.first().split(Regex("\\s+"))
        if (dimensionTokens.size != 2) {
            throw MazeFormatException(ValidationConstants.FIRST_LINE_DIMENSIONS)
        }

        val rows = dimensionTokens[0].toIntOrNull()
            ?: throw MazeFormatException(ValidationConstants.MAZE_ROW_INTEGER)
        val cols = dimensionTokens[1].toIntOrNull()
            ?: throw MazeFormatException(ValidationConstants.MAZE_COLUMN_INTEGER)

        try {
            MazeValidator.validateDimensions(rows, cols)
        } catch (exception: IllegalArgumentException) {
            throw MazeFormatException(exception.message ?: ValidationConstants.INVALID_MAZE_DIMENSIONS)
        }

        val expectedLineCount = 1 + rows + rows
        if (lines.size != expectedLineCount) {
            throw MazeFormatException(ValidationConstants.mazeFileRowCounts(rows))
        }

        val rightWalls = parseMatrix(lines, 1, rows, cols, "right wall")
        val bottomWalls = parseMatrix(lines, 1 + rows, rows, cols, "bottom wall")

        return Maze(rows, cols, rightWalls, bottomWalls)
    }

    private fun parseMatrix(
        lines: List<String>,
        startIndex: Int,
        rowCount: Int,
        colCount: Int,
        matrixName: String,
    ): Array<BooleanArray> {
        return Array(rowCount) { rowIndex ->
            val tokens = lines[startIndex + rowIndex].split(Regex("\\s+"))
            if (tokens.size != colCount) {
                throw MazeFormatException(ValidationConstants.matrixRowCount(matrixName, colCount))
            }
            BooleanArray(colCount) { colIndex ->
                when (tokens[colIndex]) {
                    "0" -> false
                    "1" -> true
                    else -> throw MazeFormatException(ValidationConstants.MAZE_BINARY_ONLY)
                }
            }
        }
    }
}
