package com.school21.service.app

import com.school21.model.Cell
import com.school21.model.Maze
import com.school21.service.MazeSolver
import com.school21.service.generator.MazeGenerator
import com.school21.service.parser.MazeFileParser
import java.io.InputStream

class MazeApplicationService(
    private val parser: MazeFileParser,
    private val generator: MazeGenerator,
    private val solver: MazeSolver,
) {
    fun loadMaze(inputStream: InputStream): Maze {
        return parser.parse(inputStream)
    }

    fun generateMaze(rows: Int, cols: Int): Maze {
        return generator.generate(rows, cols)
    }

    fun solveMaze(maze: Maze, start: Cell, end: Cell): List<Cell> {
        return solver.solve(maze, start, end)
    }
}
