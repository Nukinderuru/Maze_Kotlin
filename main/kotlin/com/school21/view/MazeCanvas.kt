package com.school21.view

import com.school21.model.Cell
import com.school21.model.Maze
import com.school21.service.RenderCalculator
import com.school21.service.RenderMetrics
import javafx.scene.canvas.Canvas
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color

class MazeCanvas : Canvas(RenderCalculator.FIELD_SIZE, RenderCalculator.FIELD_SIZE) {
    private var currentMaze: Maze? = null
    var onCellClicked: ((Cell) -> Unit)? = null

    init {
        addEventHandler(MouseEvent.MOUSE_CLICKED) { event ->
            val maze = currentMaze ?: return@addEventHandler
            val metrics = RenderCalculator.calculate(maze.rows, maze.cols)
            if (event.x !in 0.0..metrics.fieldSize || event.y !in 0.0..metrics.fieldSize) {
                return@addEventHandler
            }
            val col = (event.x / metrics.cellWidth).toInt().coerceIn(0, maze.cols - 1)
            val row = (event.y / metrics.cellHeight).toInt().coerceIn(0, maze.rows - 1)
            onCellClicked?.invoke(Cell(row, col))
        }
    }

    /**
     * Renders the given maze onto the canvas by drawing its walls and boundaries.
     *
     * @param maze The maze to be rendered. Contains information about the maze's dimensions
     *             and the presence of walls between cells.
     */
    fun drawMaze(maze: Maze, solutionPath: List<Cell> = emptyList(), start: Cell? = null, end: Cell? = null) {
        currentMaze = maze
        val metrics = RenderCalculator.calculate(maze.rows, maze.cols)
        val graphics = graphicsContext2D

        graphics.clearRect(0.0, 0.0, width, height)
        graphics.fill = Color.WHITE
        graphics.fillRect(0.0, 0.0, width, height)
        graphics.stroke = Color.BLACK
        graphics.lineWidth = metrics.wallThickness

        graphics.strokeRect(0.0, 0.0, metrics.fieldSize, metrics.fieldSize)

        for (row in 0 until maze.rows) {
            for (col in 0 until maze.cols) {
                val x = col * metrics.cellWidth
                val y = row * metrics.cellHeight

                if (maze.rightWalls[row][col]) {
                    val rightX = x + metrics.cellWidth
                    graphics.strokeLine(rightX, y, rightX, y + metrics.cellHeight)
                }

                if (maze.bottomWalls[row][col]) {
                    val bottomY = y + metrics.cellHeight
                    graphics.strokeLine(x, bottomY, x + metrics.cellWidth, bottomY)
                }
            }
        }

        if (solutionPath.isNotEmpty()) {
            graphics.stroke = Color.DODGERBLUE
            graphics.lineWidth = metrics.wallThickness
            for (index in 0 until solutionPath.lastIndex) {
                val from = solutionPath[index]
                val to = solutionPath[index + 1]
                graphics.strokeLine(
                    centerX(from, metrics),
                    centerY(from, metrics),
                    centerX(to, metrics),
                    centerY(to, metrics),
                )
            }
        }

        drawMarker(start, metrics, Color.FORESTGREEN)
        drawMarker(end, metrics, Color.CRIMSON)
    }

    private fun drawMarker(cell: Cell?, metrics: RenderMetrics, color: Color) {
        if (cell == null) {
            return
        }
        val graphics = graphicsContext2D
        val radius = minOf(metrics.cellWidth, metrics.cellHeight) * 0.18
        graphics.fill = color
        graphics.fillOval(
            centerX(cell, metrics) - radius,
            centerY(cell, metrics) - radius,
            radius * 2,
            radius * 2,
        )
    }

    private fun centerX(cell: Cell, metrics: RenderMetrics): Double {
        return (cell.col + 0.5) * metrics.cellWidth
    }

    private fun centerY(cell: Cell, metrics: RenderMetrics): Double {
        return (cell.row + 0.5) * metrics.cellHeight
    }
}
