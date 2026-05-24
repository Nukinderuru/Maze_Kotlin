package com.school21.view

import com.school21.controller.CaveController
import com.school21.model.Cave
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Stage

class CaveWindow : CaveView {
    private val caveCanvas = CaveCanvas()
    private val statusLabel = Label("Load or initialize a cave to begin").apply {
        style = "-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #1f2937;"
    }
    private var controller: CaveController? = null

    fun show(stage: Stage) {
        val rowsField = TextField("20").apply {
            promptText = "Rows"
            prefColumnCount = 4
        }
        val colsField = TextField("20").apply {
            promptText = "Cols"
            prefColumnCount = 4
        }
        val chanceField = TextField("45").apply {
            promptText = "Chance %"
            prefColumnCount = 4
        }
        val birthField = TextField("4").apply {
            promptText = "Birth"
            prefColumnCount = 4
        }
        val deathField = TextField("3").apply {
            promptText = "Death"
            prefColumnCount = 4
        }
        val delayField = TextField("200").apply {
            promptText = "Delay ms"
            prefColumnCount = 5
        }

        val loadButton = Button("Load Cave").apply {
            setOnAction { controller?.loadCave(stage) }
        }
        val sampleSelector = ComboBox<BundledCaveSample>().apply {
            items.addAll(BundledCaveSample.ALL)
            value = BundledCaveSample.DEFAULT
        }
        val loadSampleButton = Button("Load Sample").apply {
            setOnAction {
                val sample = sampleSelector.value ?: return@setOnAction
                controller?.loadBundledCave(sample.resourcePath, sample.displayName)
            }
        }
        val randomizeButton = Button("Initialize Random").apply {
            setOnAction { controller?.initializeRandom(rowsField.text, colsField.text, chanceField.text) }
        }
        val nextStepButton = Button("Next Step").apply {
            setOnAction { controller?.nextStep(birthField.text, deathField.text) }
        }
        val startAutoButton = Button("Start Auto").apply {
            setOnAction { controller?.startAuto(birthField.text, deathField.text, delayField.text) }
        }
        val stopAutoButton = Button("Stop Auto").apply {
            setOnAction { controller?.stopAuto() }
        }

        val fileSection = createControlSection(
            "File",
            listOf(
                HBox(8.0, loadButton),
                HBox(8.0, sampleSelector, loadSampleButton),
            ),
        )
        val initializeSection = createControlSection(
            "Initialize",
            listOf(
                createLabeledRow("Size", rowsField, colsField),
                createLabeledRow("Chance", chanceField, randomizeButton),
            ),
        )
        val simulationSection = createControlSection(
            "Simulation",
            listOf(
                createLabeledRow("Rules", birthField, deathField),
                createLabeledRow("Delay", delayField),
                HBox(8.0, nextStepButton, startAutoButton, stopAutoButton),
            ),
        )

        val statusRow = HBox(statusLabel).apply {
            alignment = Pos.CENTER
            padding = Insets(10.0, 14.0, 10.0, 14.0)
            style = "-fx-background-color: #f3f4f6; -fx-background-radius: 8; " +
                "-fx-border-color: #d1d5db; -fx-border-radius: 8;"
        }
        val controlsColumn = VBox(12.0, fileSection, initializeSection, simulationSection).apply {
            prefWidth = 300.0
            minWidth = 300.0
        }
        val content = HBox(16.0, controlsColumn, caveCanvas).apply {
            padding = Insets(0.0, 12.0, 12.0, 12.0)
            alignment = Pos.TOP_CENTER
        }

        val root = BorderPane().apply {
            top = VBox(10.0, statusRow).apply { padding = Insets(12.0) }
            center = content
        }

        stage.title = "Cave"
        stage.scene = Scene(root)
        stage.isResizable = false
        stage.setOnCloseRequest { controller?.stopAuto(silent = true) }
        stage.show()
    }

    private fun createControlSection(title: String, controls: List<javafx.scene.Node>): VBox {
        val titleLabel = Label(title).apply {
            style = "-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;"
        }
        val controlsColumn = VBox(8.0).apply {
            children.addAll(controls)
        }
        return VBox(6.0, titleLabel, controlsColumn).apply {
            padding = Insets(10.0)
            style = "-fx-background-color: #ffffff; -fx-background-radius: 8; " +
                "-fx-border-color: #d1d5db; -fx-border-radius: 8;"
        }
    }

    private fun createLabeledRow(label: String, vararg controls: javafx.scene.Node): HBox {
        val rowLabel = Label(label).apply {
            minWidth = 48.0
            style = "-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: #4b5563;"
        }
        return HBox(8.0, rowLabel, *controls).apply {
            alignment = Pos.CENTER_LEFT
        }
    }

    fun setController(controller: CaveController) {
        this.controller = controller
    }

    override fun renderCave(cave: Cave) {
        caveCanvas.drawCave(cave)
    }

    override fun showError(message: String) {
        Alert(Alert.AlertType.ERROR).apply {
            title = "Cave Error"
            headerText = "Cave operation failed"
            contentText = message
        }.showAndWait()
    }

    override fun showStatus(message: String) {
        statusLabel.text = message
    }
}
