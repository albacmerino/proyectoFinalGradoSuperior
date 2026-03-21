package org.dam2.appstreaming.data.remote.dto

import com.google.gson.annotations.SerializedName

data class KeywordResponse(
    val id: Int,
    @SerializedName("keywords") val keywords: List<Keyword>? = null,
    @SerializedName("results") val results: List<Keyword>? = null
)

data class Keyword(
    val id: Int,
    val name: String
)
