package com.dateclick.api.domain.place.vo

data class Location(
    val lat: Double,
    val lng: Double
) {
    init {
        require(lat in -90.0..90.0) { "Latitude must be between -90 and 90" }
        require(lng in -180.0..180.0) { "Longitude must be between -180 and 180" }
    }
}
