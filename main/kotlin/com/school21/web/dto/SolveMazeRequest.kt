package com.school21.web.dto

import kotlinx.serialization.Serializable

@Serializable
data class SolveMazeRequest(
    val maze: MazeDto,
    val start: CellDto,
    val end: CellDto
)
