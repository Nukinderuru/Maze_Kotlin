package com.school21.service.parser

import com.school21.model.Cave
import com.school21.service.ValidationConstants
import com.school21.service.exception.MazeFormatException
import com.school21.service.validator.CaveValidator
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path

/**
 * A utility class for parsing cave definitions from files or input streams.
 *
 * This parser supports reading and validating cave definitions from various sources,
 * ensuring the cave adheres to the required format and constraints. The parsed result
 * is returned as an instance of the [Cave] class.
 *
 * The cave file is expected to follow this format:
 * - The first line must define the cave dimensions as two integers separated by whitespace:
 *   the number of rows and the number of columns.
 * - The next lines must describe the cave's cell matrix, where each row contains
 *   exactly the number of columns specified, and each cell is represented by `0` (false)
 *   or `1` (true).
 *
 * Any invalid formatting, such as missing or malformed data, will result in a [MazeFormatException].
 *
 * Methods:
 * @constructor Creates an instance of the parser.
 *
 * - [parse]: Reads a cave definition from a file path.
 * - [parse]: Reads a cave definition from an input stream.
 */
class CaveFileParser {
    /**
     * Parses a file at the specified path into a [Cave] instance.
     *
     * @param path The path to the file containing the cave definition.
     * The file structure must adhere to the expected format:
     * - The first line contains two integers specifying the rows and columns of the cave.
     * - Subsequent lines represent the cave's cell matrix using "0" and "1" values,
     *   where "0" indicates an empty cell and "1" indicates a filled cell.
     *
     * @return A [Cave] object constructed from the file content. Throws a [MazeFormatException]
     * if the file is malformed or its content is invalid for cave construction.
     */
    fun parse(path: Path): Cave {
        return parseLines(Files.readAllLines(path))
    }

    /**
     * Parses an input stream into a [Cave] instance.
     *
     * @param inputStream The input stream containing the cave definition.
     * This method expects the input stream to follow a specific format:
     * - The first line must contain two integers specifying the number of rows and columns of the cave.
     * - Subsequent lines must represent the cave's cell matrix using "0" and "1" values,
     *   where "0" indicates an empty cell and "1" indicates a filled cell.
     *
     * @return A [Cave] object constructed from the parsed input stream.
     *         Throws a [MazeFormatException] if the input content is malformed or invalid for cave construction.
     */
    fun parse(inputStream: InputStream): Cave {
        return inputStream.bufferedReader().useLines { lines ->
            parseLines(lines.toList())
        }
    }

    private fun parseLines(rawLines: List<String>): Cave {
        val lines = rawLines.map(String::trim).filter(String::isNotEmpty)
        if (lines.isEmpty()) {
            throw MazeFormatException(ValidationConstants.CAVE_FILE_EMPTY)
        }

        val dimensionTokens = lines.first().split(Regex("\\s+"))
        if (dimensionTokens.size != 2) {
            throw MazeFormatException(ValidationConstants.FIRST_LINE_DIMENSIONS)
        }
        val rows = dimensionTokens[0].toIntOrNull()
            ?: throw MazeFormatException(ValidationConstants.CAVE_ROW_INTEGER)
        val cols = dimensionTokens[1].toIntOrNull()
            ?: throw MazeFormatException(ValidationConstants.CAVE_COLUMN_INTEGER)

        try {
            CaveValidator.validateDimensions(rows, cols)
        } catch (exception: IllegalArgumentException) {
            throw MazeFormatException(exception.message ?: ValidationConstants.INVALID_CAVE_DIMENSIONS)
        }

        if (lines.size != 1 + rows) {
            throw MazeFormatException(ValidationConstants.caveFileRowCount(rows))
        }

        val cells = Array(rows) { rowIndex ->
            val tokens = lines[rowIndex + 1].split(Regex("\\s+"))
            if (tokens.size != cols) {
                throw MazeFormatException(ValidationConstants.matrixRowCount("cave", cols))
            }
            BooleanArray(cols) { colIndex ->
                when (tokens[colIndex]) {
                    "0" -> false
                    "1" -> true
                    else -> throw MazeFormatException(ValidationConstants.CAVE_BINARY_ONLY)
                }
            }
        }

        return Cave(rows, cols, cells)
    }
}
