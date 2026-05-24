package com.school21.web.dto

import kotlinx.serialization.Serializable

@Serializable
data class SolutionDto(
    val path: List<CellDto>
)
