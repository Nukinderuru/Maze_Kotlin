package com.school21.web.dto

import kotlinx.serialization.Serializable

@Serializable
data class GenerateMazeRequest(
    val rows: Int,
    val cols: Int
)
