package com.school21.view

import com.school21.model.Cell
import com.school21.model.Maze

interface MazeView {
    fun renderMaze(maze: Maze, solutionPath: List<Cell> = emptyList(), start: Cell? = null, end: Cell? = null)

    fun showPickedCoordinates(targetName: String, cell: Cell)

    fun promptClickedCellAction(cell: Cell): ClickedCellAction

    fun updateSolveInputs(start: Cell?, end: Cell?)

    fun updateAgentStatus(message: String)

    fun showError(message: String)

    fun showStatus(message: String)
}

enum class ClickedCellAction {
    CHOOSE_START,
    CHOOSE_END,
    CANCEL
}
