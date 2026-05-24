package com.school21.web.dto

import kotlinx.serialization.Serializable

@Serializable
data class CellDto(
    val row: Int,
    val col: Int
)
