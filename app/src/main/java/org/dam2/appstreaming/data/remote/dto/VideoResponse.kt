package org.dam2.appstreaming.data.remote.dto

import com.google.gson.annotations.SerializedName

data class VideoResponse(
    @SerializedName("results") val listaVideos: List<Video>
)

data class Video(
    val key: String,
    val site: String,
    val type: String
)
