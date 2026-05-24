package com.school21.web

import com.school21.model.Cell
import com.school21.model.Maze
import com.school21.web.dto.CellDto
import com.school21.web.dto.MazeDto

object MazeWebMapper {
    fun toDto(maze: Maze): MazeDto {
        return MazeDto(
            rows = maze.rows,
            cols = maze.cols,
            rightWalls = maze.rightWalls.map { row -> row.toList() },
            bottomWalls = maze.bottomWalls.map { row -> row.toList() }
        )
    }

    fun fromDto(mazeDto: MazeDto): Maze {
        return Maze(
            rows = mazeDto.rows,
            cols = mazeDto.cols,
            rightWalls = mazeDto.rightWalls.map { row -> row.toBooleanArray() }.toTypedArray(),
            bottomWalls = mazeDto.bottomWalls.map { row -> row.toBooleanArray() }.toTypedArray()
        )
    }

    fun toDto(cell: Cell): CellDto {
        return CellDto(row = cell.row + 1, col = cell.col + 1)
    }

    fun fromDto(cellDto: CellDto): Cell {
        return Cell(row = cellDto.row - 1, col = cellDto.col - 1)
    }
}
