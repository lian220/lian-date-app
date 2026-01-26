package com.dateclick.api.presentation.rest.region

data class RegionListResponse(
    val regions: List<RegionResponse>,
)

data class RegionResponse(
    val id: String,
    val name: String,
    val city: String,
    val description: String,
    val keywords: List<String>,
    val centerLat: Double,
    val centerLng: Double,
)
