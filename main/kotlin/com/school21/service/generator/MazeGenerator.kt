package com.school21.service.generator

import com.school21.model.Maze

interface MazeGenerator {
    fun generate(rows: Int, cols: Int): Maze
}
