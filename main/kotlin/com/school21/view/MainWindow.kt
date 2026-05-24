package com.school21.view

import com.school21.controller.MazeController
import com.school21.model.Cell
import com.school21.model.Maze
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.ButtonType
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.stage.Stage

class MainWindow : MazeView {
    private val mazeCanvas = MazeCanvas()
    private val statusLabel = Label("Load a maze file to begin").apply {
        style = "-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #1f2937;"
    }
    private val pickedCoordinatesLabel = Label("Click a maze cell to inspect its coordinates").apply {
        style = "-fx-font-size: 12px; -fx-text-fill: #4b5563;"
    }
    private val agentStatusLabel = Label("Agent status: not trained").apply {
        style = "-fx-font-size: 12px; -fx-text-fill: #4b5563;"
    }
    private val startRowField = TextField().apply {
        promptText = "Start row"
        prefColumnCount = 4
    }
    private val startColField = TextField().apply {
        promptText = "Start col"
        prefColumnCount = 4
    }
    private val endRowField = TextField().apply {
        promptText = "End row"
        prefColumnCount = 4
    }
    private val endColField = TextField().apply {
        promptText = "End col"
        prefColumnCount = 4
    }
    private var controller: MazeController? = null

    /**
     * Displays the main application window, initializing and arranging its components,
     * and setting up event handlers for user interactions.
     *
     * @param stage The primary stage for this application, provided by the JavaFX framework.
     */
    fun show(stage: Stage) {
        val loadButton = Button("Load Maze")
        loadButton.setOnAction {
            controller?.loadMaze(stage)
        }

        val rowsField = TextField("10").apply {
            promptText = "Rows"
            prefColumnCount = 4
        }
        val colsField = TextField("10").apply {
            promptText = "Cols"
            prefColumnCount = 4
        }

        val generateButton = Button("Generate Maze")
        generateButton.setOnAction {
            controller?.generateMaze(rowsField.text, colsField.text)
        }

        val saveButton = Button("Save Maze")
        saveButton.setOnAction {
            controller?.saveCurrentMaze(stage)
        }

        val sampleSelector = ComboBox<BundledMazeSample>().apply {
            items.addAll(BundledMazeSample.ALL)
            value = BundledMazeSample.DEFAULT
        }

        val loadSampleButton = Button("Load Sample")
        loadSampleButton.setOnAction {
            val sample = sampleSelector.value ?: return@setOnAction
            controller?.loadBundledMaze(sample.resourcePath, sample.displayName)
        }

        val solveButton = Button("Solve Maze")
        solveButton.setOnAction {
            controller?.solveMaze(
                startRowField.text,
                startColField.text,
                endRowField.text,
                endColField.text,
            )
        }
        val trainAgentButton = Button("Train Agent")
        trainAgentButton.setOnAction {
            controller?.trainAgent(endRowField.text, endColField.text)
        }
        val showAgentRouteButton = Button("Show Agent Route")
        showAgentRouteButton.setOnAction {
            controller?.showAgentRoute(startRowField.text, startColField.text)
        }

        mazeCanvas.onCellClicked = { cell ->
            controller?.handleMazeCellClick(cell)
        }

        val fileSection = createControlSection(
            title = "File",
            controls = listOf(loadButton, saveButton),
        )
        val sampleSection = createControlSection(
            title = "Bundled Samples",
            controls = listOf(sampleSelector, loadSampleButton),
        )
        val generationSection = createControlSection(
            title = "Generate Maze",
            controls = listOf(rowsField, colsField, generateButton),
        )
        val solvingSection = createControlSection(
            title = "Solve Maze",
            controls = listOf(
                createLabeledRow("Start", startRowField, startColField),
                createLabeledRow("End", endRowField, endColField),
                HBox(solveButton),
            ),
        )
        val qLearningSection = createControlSection(
            title = "Q-Learning",
            controls = listOf(
                Label("Uses current End as exit and Start as launch point").apply {
                    style = "-fx-font-size: 11px; -fx-text-fill: #6b7280;"
                },
                agentStatusLabel,
                HBox(8.0, trainAgentButton, showAgentRouteButton),
            ),
        )
        val statusRow = HBox(statusLabel).apply {
            alignment = Pos.CENTER
            padding = Insets(10.0, 14.0, 10.0, 14.0)
            style = "-fx-background-color: #f3f4f6; -fx-background-radius: 8; " +
                "-fx-border-color: #d1d5db; -fx-border-radius: 8;"
        }
        val pickedCoordinatesRow = HBox(pickedCoordinatesLabel).apply {
            alignment = Pos.CENTER
        }
        val controlsColumn = VBox(
            12.0,
            fileSection,
            sampleSection,
            generationSection,
            solvingSection,
            qLearningSection,
        ).apply {
            prefWidth = 300.0
            minWidth = 300.0
        }

        val topPanel = VBox(
            10.0,
            statusRow,
            pickedCoordinatesRow,
        ).apply {
            padding = Insets(12.0)
        }

        val content = HBox(16.0, controlsColumn, mazeCanvas).apply {
            padding = Insets(0.0, 12.0, 12.0, 12.0)
            alignment = Pos.TOP_CENTER
            HBox.setHgrow(mazeCanvas, Priority.NEVER)
        }

        val root = BorderPane().apply {
            top = topPanel
            center = content
        }

        stage.title = "Maze"
        stage.scene = Scene(root)
        stage.isResizable = false
        stage.show()
    }

    private fun createControlSection(title: String, controls: List<javafx.scene.Node>): VBox {
        val titleLabel = Label(title).apply {
            style = "-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;"
        }
        val controlsRow = VBox(8.0).apply {
            children.addAll(controls)
        }
        return VBox(6.0, titleLabel, controlsRow).apply {
            padding = Insets(10.0)
            style = "-fx-background-color: #ffffff; -fx-background-radius: 8; " +
                "-fx-border-color: #d1d5db; -fx-border-radius: 8;"
        }
    }

    private fun createLabeledRow(label: String, vararg controls: javafx.scene.Node): HBox {
        val rowLabel = Label(label).apply {
            minWidth = 36.0
            style = "-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: #4b5563;"
        }
        return HBox(8.0, rowLabel, *controls).apply {
            alignment = Pos.CENTER_LEFT
        }
    }

    fun setController(controller: MazeController) {
        this.controller = controller
    }

    override fun renderMaze(maze: Maze, solutionPath: List<Cell>, start: Cell?, end: Cell?) {
        mazeCanvas.drawMaze(maze, solutionPath, start, end)
    }

    override fun showPickedCoordinates(targetName: String, cell: Cell) {
        pickedCoordinatesLabel.text = "$targetName cell: row ${cell.row + 1}, col ${cell.col + 1}"
    }

    override fun promptClickedCellAction(cell: Cell): ClickedCellAction {
        val chooseStart = ButtonType("Choose Start")
        val chooseEnd = ButtonType("Choose End")
        val cancel = ButtonType("Cancel")
        val result = Alert(Alert.AlertType.CONFIRMATION).apply {
            title = "Use Clicked Cell"
            headerText = "Clicked cell: row ${cell.row + 1}, col ${cell.col + 1}"
            contentText = "Choose how to use this cell."
            buttonTypes.setAll(chooseStart, chooseEnd, cancel)
        }.showAndWait()

        return when (result.orElse(cancel)) {
            chooseStart -> ClickedCellAction.CHOOSE_START
            chooseEnd -> ClickedCellAction.CHOOSE_END
            else -> ClickedCellAction.CANCEL
        }
    }

    override fun updateSolveInputs(start: Cell?, end: Cell?) {
        startRowField.text = start?.let { (it.row + 1).toString() } ?: ""
        startColField.text = start?.let { (it.col + 1).toString() } ?: ""
        endRowField.text = end?.let { (it.row + 1).toString() } ?: ""
        endColField.text = end?.let { (it.col + 1).toString() } ?: ""
    }

    override fun updateAgentStatus(message: String) {
        agentStatusLabel.text = message
    }

    override fun showError(message: String) {
        Alert(Alert.AlertType.ERROR).apply {
            title = "Maze Error"
            headerText = "Maze operation failed"
            contentText = message
        }.showAndWait()
    }

    override fun showStatus(message: String) {
        statusLabel.text = message
    }
}
