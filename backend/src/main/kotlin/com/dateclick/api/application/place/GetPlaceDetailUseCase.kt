package com.dateclick.api.application.place

import com.dateclick.api.domain.place.entity.Place
import com.dateclick.api.domain.place.vo.PlaceId

interface GetPlaceDetailUseCase {
    fun execute(placeId: PlaceId): Place?
}
