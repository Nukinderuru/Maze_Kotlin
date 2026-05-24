package com.school21.web

import com.school21.model.Cell
import com.school21.model.Maze
import kotlin.test.Test
import kotlin.test.assertEquals

class MazeWebMapperTest {
    @Test
    fun `maps maze domain model to dto`() {
        val maze = Maze(
            rows = 2,
            cols = 2,
            rightWalls = arrayOf(
                booleanArrayOf(false, true),
                booleanArrayOf(true, true),
            ),
            bottomWalls = arrayOf(
                booleanArrayOf(true, false),
                booleanArrayOf(true, true),
            ),
        )

        val dto = MazeWebMapper.toDto(maze)

        assertEquals(2, dto.rows)
        assertEquals(listOf(false, true), dto.rightWalls[0])
        assertEquals(listOf(true, true), dto.bottomWalls[1])
    }

    @Test
    fun `maps cell dto with one based coordinates back to domain`() {
        val cell = MazeWebMapper.fromDto(com.school21.web.dto.CellDto(row = 3, col = 4))

        assertEquals(Cell(2, 3), cell)
        assertEquals(com.school21.web.dto.CellDto(row = 3, col = 4), MazeWebMapper.toDto(cell))
    }
}
