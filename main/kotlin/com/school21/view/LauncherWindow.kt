package com.school21.view

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import javafx.stage.Stage

class LauncherWindow(
    private val openMaze: () -> Unit,
    private val openCave: () -> Unit,
) {
    fun show(stage: Stage) {
        val title = Label("Choose Mode").apply {
            style = "-fx-font-size: 18px; -fx-font-weight: 700;"
        }
        val mazeButton = Button("Open Maze Window").apply {
            prefWidth = 220.0
            setOnAction { openMaze() }
        }
        val caveButton = Button("Open Cave Window").apply {
            prefWidth = 220.0
            setOnAction { openCave() }
        }

        val root = VBox(16.0, title, mazeButton, caveButton).apply {
            alignment = Pos.CENTER
            padding = Insets(24.0)
        }
        stage.title = "Maze Launcher"
        stage.scene = Scene(root)
        stage.isResizable = false
        stage.show()
    }
}
