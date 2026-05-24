package com.school21.web.dto

import kotlinx.serialization.Serializable

@Serializable
data class MazeDto(
    val rows: Int,
    val cols: Int,
    val rightWalls: List<List<Boolean>>,
    val bottomWalls: List<List<Boolean>>
)
