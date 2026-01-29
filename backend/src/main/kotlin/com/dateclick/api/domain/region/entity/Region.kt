package com.dateclick.api.domain.region.entity

import com.dateclick.api.domain.region.vo.RegionId

data class Region(
    val id: RegionId,
    val name: String,
    val city: String,
    val description: String,
    val keywords: List<String>,
    val centerLat: Double,
    val centerLng: Double,
)
