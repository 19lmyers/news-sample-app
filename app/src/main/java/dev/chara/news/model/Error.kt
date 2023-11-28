package dev.chara.news.model

import kotlinx.serialization.Serializable

@Serializable
data class Error(
    val status: String,
    val code: String,
    val message: String
)