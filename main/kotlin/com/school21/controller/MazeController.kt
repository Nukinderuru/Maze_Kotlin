package com.school21.controller

import com.school21.model.Cell
import com.school21.model.Maze
import com.school21.model.TrainedMazeAgent
import com.school21.service.MazeFileWriter
import com.school21.service.parser.MazeFileParser
import com.school21.service.generator.MazeGenerator
import com.school21.service.MazeSolver
import com.school21.service.ValidationConstants
import com.school21.service.rl.MazeAgentRunner
import com.school21.service.rl.QLearningConfig
import com.school21.service.rl.QLearningTrainer
import com.school21.view.ClickedCellAction
import com.school21.view.MazeView
import javafx.stage.FileChooser
import javafx.stage.Window
import java.io.File

class MazeController(
    private val parser: MazeFileParser,
    private val writer: MazeFileWriter,
    private val generator: MazeGenerator,
    private val solver: MazeSolver,
    private val qLearningTrainer: QLearningTrainer,
    private val mazeAgentRunner: MazeAgentRunner,
    private val view: MazeView,
) {
    private var currentMaze: Maze? = null
    private var currentSolution: List<Cell> = emptyList()
    private var startCell: Cell? = null
    private var endCell: Cell? = null
    private var trainedAgent: TrainedMazeAgent? = null

    /**
     * Loads a maze file by presenting a file chooser dialog to the user.
     * If a file is selected, it delegates the parsing and loading to another method.
     *
     * @param ownerWindow The window that owns the file chooser dialog.
     */
    fun loadMaze(ownerWindow: Window) {
        val file = createFileChooser().showOpenDialog(ownerWindow) ?: return
        loadMaze(file)
    }

    /**
     * Loads and parses a maze from the specified file, then visualizes it.
     * If parsing fails, an error message is displayed.
     *
     * @param file The file containing the maze representation to be loaded.
     */
    fun loadMaze(file: File) {
        runCatching { parser.parse(file.toPath()) }
            .onSuccess { maze ->
                showMaze(maze, "Loaded maze")
            }
            .onFailure { exception ->
                val message = exception.message ?: ValidationConstants.FAILED_TO_LOAD_MAZE
                view.showError(message)
                view.showStatus("Maze loading failed")
            }
    }

    /**
     * Loads a maze from a bundled resource and displays it in the view.
     * The method searches for the maze file in the application resources using the provided path.
     * If the file is found and successfully parsed, the maze is rendered.
     * If the file is not found or parsing fails, an error message is displayed in the view.
     *
     * @param resourcePath The relative path to the bundled maze file in the application resources.
     * @param sampleName A human-readable name for the sample maze, used in status or error messages.
     */
    fun loadBundledMaze(resourcePath: String, sampleName: String) {
        runCatching {
            val inputStream = javaClass.classLoader.getResourceAsStream(resourcePath)
                ?: throw IllegalArgumentException(ValidationConstants.bundledSampleNotFound(sampleName))
            parser.parse(inputStream)
        }.onSuccess { maze ->
            showMaze(maze, "Loaded sample $sampleName")
        }.onFailure { exception ->
            val message = exception.message ?: ValidationConstants.FAILED_TO_LOAD_BUNDLED_SAMPLE
            view.showError(message)
            view.showStatus("Sample loading failed")
        }
    }

    /**
     * Generates a new maze with the specified number of rows and columns.
     * If the input values are not valid integers, an error is displayed.
     * The generated maze is rendered in the view if successful.
     *
     * @param rowsText The text input representing the number of rows in the maze.
     *                 This must be a valid integer value.
     * @param colsText The text input representing the number of columns in the maze.
     *                 This must be a valid integer value.
     */
    fun generateMaze(rowsText: String, colsText: String) {
        runCatching {
            val rows = rowsText.toIntOrNull() ?: throw IllegalArgumentException(ValidationConstants.MAZE_ROW_INTEGER)
            val cols = colsText.toIntOrNull() ?: throw IllegalArgumentException(ValidationConstants.MAZE_COLUMN_INTEGER)
            generator.generate(rows, cols)
        }.onSuccess { maze ->
            showMaze(maze, "Generated maze")
        }.onFailure { exception ->
            val message = exception.message ?: ValidationConstants.FAILED_TO_GENERATE_MAZE
            view.showError(message)
            view.showStatus("Maze generation failed")
        }
    }

    /**
     * Attempts to solve the current maze by finding a path between the specified start and end cells.
     * Updates the view with the solution path if successful or displays an error message otherwise.
     *
     * @param startRowText The text input representing the starting cell's row. It must be a valid integer within maze bounds.
     * @param startColText The text input representing the starting cell's column. It must be a valid integer within maze bounds.
     * @param endRowText The text input representing the target cell's row. It must be a valid integer within maze bounds.
     * @param endColText The text input representing the target cell's column. It must be a valid integer within maze bounds.
     */
    fun solveMaze(startRowText: String, startColText: String, endRowText: String, endColText: String) {
        val maze = currentMaze
        if (maze == null) {
            view.showError(ValidationConstants.LOAD_OR_GENERATE_MAZE_BEFORE_SOLVING)
            view.showStatus("Maze solving failed")
            return
        }

        runCatching {
            val start = parseCell(startRowText, startColText, maze, "Start")
            val end = parseCell(endRowText, endColText, maze, "End")
            val path = solver.solve(maze, start, end)
            Triple(start, end, path)
        }.onSuccess { (start, end, path) ->
            startCell = start
            endCell = end
            currentSolution = path
            redrawCurrentMaze()
            view.updateSolveInputs(startCell, endCell)
            view.showStatus("Solved maze in ${path.size} cells")
        }.onFailure { exception ->
            view.showError(exception.message ?: ValidationConstants.FAILED_TO_SOLVE_MAZE)
            view.showStatus("Maze solving failed")
        }
    }

    /**
     * Trains an agent to navigate the current maze towards a specified exit cell.
     * If the maze is not loaded, displays an error and updates the agent's status to indicate failure.
     * Training configuration is set according to the maze dimensions, and the agent is trained using Q-learning.
     * Updates the view and agent status upon success or failure.
     *
     * @param exitRowText The text input representing the row index of the exit cell.
     *                    This must be a valid integer within the maze's row bounds.
     * @param exitColText The text input representing the column index of the exit cell.
     *                    This must be a valid integer within the maze's column bounds.
     */
    fun trainAgent(exitRowText: String, exitColText: String) {
        val maze = currentMaze
        if (maze == null) {
            view.showError(ValidationConstants.LOAD_OR_GENERATE_MAZE_BEFORE_TRAINING)
            view.showStatus("Agent training failed")
            view.updateAgentStatus("Agent status: unavailable until a maze is loaded")
            return
        }

        runCatching {
            val exit = parseCell(exitRowText, exitColText, maze, "End")
            val config = QLearningConfig.defaultFor(maze.rows, maze.cols)
            qLearningTrainer.train(maze, exit, config) to exit
        }.onSuccess { (agent, exit) ->
            trainedAgent = agent
            currentSolution = emptyList()
            endCell = exit
            view.updateSolveInputs(startCell, endCell)
            view.updateAgentStatus("Agent status: trained for exit ${exit.row + 1}, ${exit.col + 1}")
            redrawCurrentMaze()
            view.showStatus("Agent trained for exit ${exit.row + 1}, ${exit.col + 1}")
        }.onFailure { exception ->
            view.showError(exception.message ?: ValidationConstants.FAILED_TO_TRAIN_AGENT)
            view.showStatus("Agent training failed")
            view.updateAgentStatus("Agent status: training failed")
        }
    }

    /**
     * Builds and displays a route for a trained agent in the current maze starting from the given start cell.
     * If the maze or the agent is not available, an appropriate error message is displayed,
     * and the operation is aborted. On success, the route is updated in the view along with
     * the agent's status and other relevant details.
     *
     * @param startRowText The text input representing the starting cell's row. It must be a valid integer within maze bounds.
     * @param startColText The text input representing the starting cell's column. It must be a valid integer within maze bounds.
     */
    fun showAgentRoute(startRowText: String, startColText: String) {
        val maze = currentMaze
        if (maze == null) {
            view.showError(ValidationConstants.LOAD_OR_GENERATE_MAZE_BEFORE_SOLVING)
            view.showStatus("Agent route failed")
            return
        }
        val agent = trainedAgent
        if (agent == null) {
            view.showError(ValidationConstants.TRAIN_AGENT_BEFORE_ROUTE)
            view.showStatus("Agent route failed")
            view.updateAgentStatus("Agent status: not trained")
            return
        }

        runCatching {
            val start = parseCell(startRowText, startColText, maze, "Start")
            val path = mazeAgentRunner.buildRoute(agent, start)
            start to path
        }.onSuccess { (start, path) ->
            startCell = start
            endCell = agent.exit
            currentSolution = path
            view.updateSolveInputs(startCell, endCell)
            view.updateAgentStatus("Agent status: route built for start ${start.row + 1}, ${start.col + 1}")
            redrawCurrentMaze()
            view.showStatus("Agent built route in ${path.size} cells")
        }.onFailure { exception ->
            view.showError(exception.message ?: ValidationConstants.FAILED_TO_BUILD_AGENT_ROUTE)
            view.showStatus("Agent route failed")
            view.updateAgentStatus("Agent status: route build failed")
        }
    }

    /**
     * Handles the user's action when clicking on a maze cell. Based on the selected action,
     * the method can set the start or end cell or cancel the interaction. It updates the view
     * and the maze state accordingly.
     *
     * @param cell The maze cell that was clicked. Contains the row and column indices of the cell.
     */
    fun handleMazeCellClick(cell: Cell) {
        view.showPickedCoordinates("Clicked", cell)
        when (view.promptClickedCellAction(cell)) {
            ClickedCellAction.CHOOSE_START -> {
                startCell = cell
                currentSolution = emptyList()
                redrawCurrentMaze()
                view.updateSolveInputs(startCell, endCell)
                view.showStatus("Selected start point: ${cell.row + 1}, ${cell.col + 1}")
            }
            ClickedCellAction.CHOOSE_END -> {
                endCell = cell
                currentSolution = emptyList()
                redrawCurrentMaze()
                view.updateSolveInputs(startCell, endCell)
                view.showStatus("Selected end point: ${cell.row + 1}, ${cell.col + 1}")
            }
            ClickedCellAction.CANCEL -> view.showStatus("Clicked cell: ${cell.row + 1}, ${cell.col + 1}")
        }
    }

    /**
     * Saves the current maze to a file chosen by the user through a save dialog.
     * If no maze is available or if an error occurs during the save process, an error message
     * is displayed in the view, and the operation is canceled.
     *
     * @param ownerWindow The window that owns the save file chooser dialog.
     */
    fun saveCurrentMaze(ownerWindow: Window) {
        val maze = currentMaze
        if (maze == null) {
            view.showError(ValidationConstants.NO_MAZE_TO_SAVE)
            view.showStatus("Maze saving failed")
            return
        }

        val file = createSaveFileChooser().showSaveDialog(ownerWindow) ?: return
        runCatching {
            writer.write(maze, file.toPath())
        }.onSuccess {
            view.showStatus("Saved maze: ${maze.rows}x${maze.cols}")
        }.onFailure { exception ->
            val message = exception.message ?: ValidationConstants.FAILED_TO_SAVE_MAZE
            view.showError(message)
            view.showStatus("Maze saving failed")
        }
    }

    private fun showMaze(maze: Maze, sourceLabel: String) {
        currentMaze = maze
        currentSolution = emptyList()
        startCell = null
        endCell = null
        trainedAgent = null
        view.updateSolveInputs(null, null)
        view.updateAgentStatus("Agent status: not trained")
        view.renderMaze(maze)
        view.showStatus("$sourceLabel: ${maze.rows}x${maze.cols}")
    }

    private fun redrawCurrentMaze() {
        val maze = currentMaze ?: return
        view.renderMaze(maze, currentSolution, startCell, endCell)
    }

    private fun parseCell(rowText: String, colText: String, maze: Maze, prefix: String): Cell {
        val row = rowText.toIntOrNull() ?: throw IllegalArgumentException(ValidationConstants.rowMustBeInteger(prefix))
        val col = colText.toIntOrNull() ?: throw IllegalArgumentException(ValidationConstants.columnMustBeInteger(prefix))
        require(row in 1..maze.rows) { ValidationConstants.rowRange(prefix, maze.rows) }
        require(col in 1..maze.cols) { ValidationConstants.columnRange(prefix, maze.cols) }
        return Cell(row - 1, col - 1)
    }

    private fun createFileChooser(): FileChooser {
        return FileChooser().apply {
            title = "Open Maze File"
            extensionFilters.addAll(
                FileChooser.ExtensionFilter("Text Files", "*.txt"),
                FileChooser.ExtensionFilter("All Files", "*.*"),
            )
        }
    }

    private fun createSaveFileChooser(): FileChooser {
        return FileChooser().apply {
            title = "Save Maze File"
            initialFileName = "maze.txt"
            extensionFilters.add(
                FileChooser.ExtensionFilter("Text Files", "*.txt"),
            )
        }
    }
}
