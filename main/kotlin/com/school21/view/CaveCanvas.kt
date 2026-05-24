package com.school21.view

import com.school21.model.Cave
import com.school21.service.RenderCalculator
import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color

class CaveCanvas : Canvas(RenderCalculator.FIELD_SIZE, RenderCalculator.FIELD_SIZE) {
    /**
     * Renders a visual representation of the given cave on the canvas.
     *
     * Each cell in the cave is drawn as a rectangle, with its color determined by the state of the cell.
     * Black represents an active cell (`true`), while white represents an inactive cell (`false`).
     * The method also ensures that the canvas is cleared before rendering and
     * draws an outer border around the cave.
     *
     * @param cave The cave to draw. It contains the number of rows, columns, and a grid of boolean values
     * indicating the state of each cell.
     */
    fun drawCave(cave: Cave) {
        val metrics = RenderCalculator.calculate(cave.rows, cave.cols)
        val graphics = graphicsContext2D
        graphics.clearRect(0.0, 0.0, width, height)
        graphics.fill = Color.WHITE
        graphics.fillRect(0.0, 0.0, width, height)

        for (row in 0 until cave.rows) {
            for (col in 0 until cave.cols) {
                graphics.fill = if (cave.cells[row][col]) Color.BLACK else Color.WHITE
                graphics.fillRect(
                    col * metrics.cellWidth,
                    row * metrics.cellHeight,
                    metrics.cellWidth,
                    metrics.cellHeight,
                )
            }
        }

        graphics.stroke = Color.BLACK
        graphics.lineWidth = RenderCalculator.WALL_THICKNESS
        graphics.strokeRect(0.0, 0.0, metrics.fieldSize, metrics.fieldSize)
    }
}
