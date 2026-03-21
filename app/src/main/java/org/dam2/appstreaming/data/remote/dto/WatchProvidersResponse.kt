package org.dam2.appstreaming.data.remote.dto

import com.google.gson.annotations.SerializedName

data class WatchProvidersResponse(
    val results: Map<String, WatchCountryInfo>
)

data class WatchCountryInfo(
    val link: String,
    val flatrate: List<Provider>? = null
)

data class Provider(
    @SerializedName("provider_name") val providerName: String,
    @SerializedName("logo_path") val logoPath: String
)
