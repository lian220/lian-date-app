package com.dateclick.api.domain.course.entity

data class Route(
    val from: Int,
    val to: Int,
    val distance: Int, // meters
    val duration: Int, // minutes
    val transportType: TransportType,
    val description: String
)

enum class TransportType(val code: String) {
    WALK("walk"),
    TRANSIT("transit"),
    CAR("car")
}
