package org.dam2.appstreaming.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ReviewResponse(
    @SerializedName("results") val results: List<Review>
)

data class Review(
    val author: String,
    val content: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("author_details") val authorDetails: AuthorDetails
)

data class AuthorDetails(
    val name: String,
    val username: String,
    @SerializedName("avatar_path") val avatarPath: String?,
    val rating: Double?
)
