package com.dateclick.api.domain.place.vo

data class BusinessHours(
    val day: String,
    val open: String?,
    val close: String?
) {
    val isClosed: Boolean
        get() = open == null && close == null
}
