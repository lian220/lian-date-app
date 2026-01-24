package com.dateclick.api.domain.place.vo

@JvmInline
value class PlaceId(val value: String) {
    init {
        require(value.isNotBlank()) { "Place ID cannot be blank" }
    }

    val isKakaoPlace: Boolean
        get() = value.startsWith("kakao_")
}
