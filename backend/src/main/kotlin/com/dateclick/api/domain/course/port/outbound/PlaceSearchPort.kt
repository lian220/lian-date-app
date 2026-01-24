package com.dateclick.api.domain.course.port.outbound

import com.dateclick.api.domain.place.entity.Place
import com.dateclick.api.domain.place.vo.Location
import com.dateclick.api.domain.place.vo.PlaceId
import com.dateclick.api.domain.region.vo.RegionId
import com.dateclick.api.domain.course.entity.Route

interface PlaceSearchPort {
    fun searchPlaces(
        regionId: RegionId,
        category: PlaceCategory,
        limit: Int
    ): List<Place>

    fun getPlaceDetail(placeId: PlaceId): Place?

    fun calculateRoute(from: Location, to: Location): Route
}

enum class PlaceCategory(val code: String) {
    ALL("all"),
    CAFE("cafe"),
    RESTAURANT("restaurant"),
    ACTIVITY("activity"),
    CULTURE("culture");

    fun toKakaoQuery(): String = when (this) {
        ALL -> ""
        CAFE -> "카페"
        RESTAURANT -> "음식점"
        ACTIVITY -> "레저"
        CULTURE -> "문화시설"
    }
}
