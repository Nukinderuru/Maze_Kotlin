package com.school21.controller

import com.school21.model.Cave
import com.school21.service.ValidationConstants
import com.school21.service.parser.CaveFileParser
import com.school21.service.generator.CaveGenerator
import com.school21.service.CaveSimulator
import com.school21.service.validator.CaveValidator
import com.school21.view.CaveView
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.stage.FileChooser
import javafx.stage.Window
import javafx.util.Duration

/**
 * A controller for handling cave-related operations including loading, initialization, simulation,
 * and auto-stepping through cave evolution.
 *
 * @property parser A utility for parsing cave data from files or resources.
 * @property generator A utility for generating randomized caves.
 * @property simulator A utility for simulating cave evolution processes.
 * @property view A UI component responsible for rendering caves and displaying statuses.
 */
class CaveController(
    private val parser: CaveFileParser,
    private val generator: CaveGenerator,
    private val simulator: CaveSimulator,
    private val view: CaveView
) {
    private var currentCave: Cave? = null
    private var autoTimeline: Timeline? = null

    /**
     * Loads a cave from a file chosen by the user and displays it in the application.
     * If the file selection is canceled or an error occurs during loading, appropriate feedback is shown to the user.
     *
     * @param ownerWindow The parent window used for displaying the file chooser dialog.
     */
    fun loadCave(ownerWindow: Window) {
        val file = createFileChooser().showOpenDialog(ownerWindow) ?: return
        runCatching { parser.parse(file.toPath()) }
            .onSuccess { cave ->
                stopAuto(silent = true)
                showCave(cave, "Loaded cave")
            }
            .onFailure { exception ->
                view.showError(exception.message ?: ValidationConstants.FAILED_TO_LOAD_CAVE)
                view.showStatus("Cave loading failed")
            }
    }

    /**
     * Loads a bundled cave from the specified resource path and displays it in the application.
     * If the resource is not found or an error occurs during loading, appropriate feedback
     * is shown to the user.
     *
     * @param resourcePath The path to the resource file containing the bundled cave.
     * @param sampleName The name of the bundled sample to load, used in error messages and status updates.
     */
    fun loadBundledCave(resourcePath: String, sampleName: String) {
        runCatching {
            val inputStream = javaClass.classLoader.getResourceAsStream(resourcePath)
                ?: throw IllegalArgumentException(ValidationConstants.bundledSampleNotFound(sampleName))
            parser.parse(inputStream)
        }.onSuccess { cave ->
            stopAuto(silent = true)
            showCave(cave, "Loaded sample $sampleName")
        }.onFailure { exception ->
            view.showError(exception.message ?: ValidationConstants.FAILED_TO_LOAD_BUNDLED_SAMPLE)
            view.showStatus("Sample loading failed")
        }
    }

    /**
     * Initializes a random cave grid using the provided parameters.
     * Converts the input strings for dimensions and chance into integers,
     * performs validation, and initializes the cave. Displays appropriate
     * feedback on success or failure.
     *
     * @param rowsText The number of rows in the cave grid, provided as a string.
     *                 Must be a valid integer.
     * @param colsText The number of columns in the cave grid, provided as a string.
     *                 Must be a valid integer.
     * @param chanceText The percentage chance (0-100) of each cell being alive at
     *                   initialization, provided as a string. Must be a valid integer.
     */
    fun initializeRandom(rowsText: String, colsText: String, chanceText: String) {
        runCatching {
            val rows = rowsText.toIntOrNull() ?: throw IllegalArgumentException(ValidationConstants.CAVE_ROW_INTEGER)
            val cols = colsText.toIntOrNull() ?: throw IllegalArgumentException(ValidationConstants.CAVE_COLUMN_INTEGER)
            val chance = chanceText.toIntOrNull() ?: throw IllegalArgumentException(ValidationConstants.INITIAL_CHANCE_INTEGER)
            generator.initialize(rows, cols, chance)
        }.onSuccess { cave ->
            stopAuto(silent = true)
            showCave(cave, "Initialized cave")
        }.onFailure { exception ->
            view.showError(exception.message ?: ValidationConstants.FAILED_TO_INITIALIZE_CAVE)
            view.showStatus("Cave initialization failed")
        }
    }

    /**
     * Advances the simulation of the cave by one step using the provided birth and death limits.
     * Parses the input strings, validates the limits, and updates the cave state.
     * Displays the updated cave or an error message based on the success of the operation.
     *
     * @param birthText The birth limit provided as a string. Must be a valid integer.
     * @param deathText The death limit provided as a string. Must be a valid integer.
     */
    fun nextStep(birthText: String, deathText: String) {
        val cave = currentCave
        if (cave == null) {
            view.showError(ValidationConstants.LOAD_OR_INITIALIZE_CAVE_BEFORE_STEPPING)
            view.showStatus("Cave step failed")
            return
        }

        runCatching {
            val birthLimit = parseLimit(birthText, "Birth limit")
            val deathLimit = parseLimit(deathText, "Death limit")
            simulator.nextStep(cave, birthLimit, deathLimit)
        }.onSuccess { next ->
            currentCave = next
            view.renderCave(next)
            view.showStatus("Advanced cave by one step")
        }.onFailure { exception ->
            view.showError(exception.message ?: ValidationConstants.FAILED_TO_ADVANCE_CAVE)
            view.showStatus("Cave step failed")
        }
    }

    /**
     * Starts the automatic simulation process for the cave grid with the specified parameters.
     * The cave must be loaded or initialized before invoking this method. If the cave is not
     * ready or the parameters are invalid, errors will be displayed, and auto mode will not start.
     *
     * @param birthText The birth limit provided as a string. Specifies the minimum number of
     *                  live neighbors required to transition a dead cell to a live state.
     *                  Must be a valid integer.
     * @param deathText The death limit provided as a string. Specifies the maximum number of
     *                  live neighbors a live cell can have before transitioning to a dead
     *                  state. Must be a valid integer.
     * @param delayText The delay for automatic steps, provided as a string representation of
     *                  milliseconds. Must be a valid positive integer.
     */
    fun startAuto(birthText: String, deathText: String, delayText: String) {
        val cave = currentCave
        if (cave == null) {
            view.showError(ValidationConstants.LOAD_OR_INITIALIZE_CAVE_BEFORE_AUTO)
            view.showStatus("Auto mode unavailable")
            return
        }

        runCatching {
            val birthLimit = parseLimit(birthText, "Birth limit")
            val deathLimit = parseLimit(deathText, "Death limit")
            val delay = delayText.toLongOrNull() ?: throw IllegalArgumentException(ValidationConstants.AUTO_STEP_DELAY_INTEGER)
            CaveValidator.validateDelay(delay)
            Triple(birthLimit, deathLimit, delay)
        }.onSuccess { (birthLimit, deathLimit, delay) ->
            stopAuto(silent = true)
            autoTimeline = Timeline(
                KeyFrame(Duration.millis(delay.toDouble()), {
                    currentCave?.let {
                        val next = simulator.nextStep(it, birthLimit, deathLimit)
                        currentCave = next
                        view.renderCave(next)
                    }
                })
            ).apply {
                cycleCount = Timeline.INDEFINITE
                play()
            }
            view.showStatus("Auto mode running every $delay ms")
        }.onFailure { exception ->
            view.showError(exception.message ?: ValidationConstants.FAILED_TO_START_AUTO)
            view.showStatus("Auto mode failed")
        }
    }

    /**
     * Stops the automatic simulation of the cave. If `silent` is set to `false` and there is a
     * currently loaded cave, a status message will be displayed to inform the user that
     * the automatic mode has been stopped.
     *
     * @param silent If `true`, suppresses the display of the status message. Defaults to `false`.
     */
    fun stopAuto(silent: Boolean = false) {
        autoTimeline?.stop()
        autoTimeline = null
        if (!silent && currentCave != null) {
            view.showStatus("Auto mode stopped")
        }
    }

    private fun showCave(cave: Cave, sourceLabel: String) {
        currentCave = cave
        view.renderCave(cave)
        view.showStatus("$sourceLabel: ${cave.rows}x${cave.cols}")
    }

    private fun parseLimit(text: String, name: String): Int {
        val limit = text.toIntOrNull() ?: throw IllegalArgumentException("$name must be an integer")
        CaveValidator.validateLimit(limit, name)
        return limit
    }

    private fun createFileChooser(): FileChooser {
        return FileChooser().apply {
            title = "Open Cave File"
            extensionFilters.addAll(
                FileChooser.ExtensionFilter("Text Files", "*.txt"),
                FileChooser.ExtensionFilter("All Files", "*.*"),
            )
        }
    }
}
