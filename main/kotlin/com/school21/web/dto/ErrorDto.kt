package com.school21.web.dto

import kotlinx.serialization.Serializable

@Serializable
data class ErrorDto(
    val message: String
)
