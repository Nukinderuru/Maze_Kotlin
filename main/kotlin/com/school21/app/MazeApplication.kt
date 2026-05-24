package com.school21.app

import com.school21.controller.CaveController
import com.school21.controller.MazeController
import com.school21.service.parser.CaveFileParser
import com.school21.service.generator.CaveGenerator
import com.school21.service.CaveSimulator
import com.school21.service.generator.EllerMazeGenerator
import com.school21.service.MazeFileWriter
import com.school21.service.parser.MazeFileParser
import com.school21.service.MazeSolver
import com.school21.service.rl.MazeAgentRunner
import com.school21.service.rl.QLearningTrainer
import com.school21.view.CaveWindow
import com.school21.view.LauncherWindow
import com.school21.view.MainWindow
import javafx.application.Application
import javafx.stage.Stage

class MazeApplication : Application() {
    override fun start(primaryStage: Stage) {
        LauncherWindow(
            openMaze = { openMazeWindow() },
            openCave = { openCaveWindow() },
        ).show(primaryStage)
    }

    private fun openMazeWindow() {
        val stage = Stage()
        val view = MainWindow()
        val controller = MazeController(
            MazeFileParser(),
            MazeFileWriter(),
            EllerMazeGenerator(),
            MazeSolver(),
            QLearningTrainer(),
            MazeAgentRunner(),
            view,
        )
        view.setController(controller)
        view.show(stage)
    }

    private fun openCaveWindow() {
        val stage = Stage()
        val view = CaveWindow()
        val controller = CaveController(
            CaveFileParser(),
            CaveGenerator(),
            CaveSimulator(),
            view,
        )
        view.setController(controller)
        view.show(stage)
    }
}
