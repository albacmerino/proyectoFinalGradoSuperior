package org.dam2.appstreaming.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ReleaseDatesResponse(
    val results: List<RegionReleaseDate>
)

data class RegionReleaseDate(
    @SerializedName("iso_3166_1") val iso31661: String,
    @SerializedName("release_dates") val releaseDates: List<CertificationItem>
)

data class CertificationItem(
    val certification: String
)
