package com.dateclick.api.presentation.rest.place

import com.dateclick.api.presentation.rest.common.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/places")
class PlaceController {

    @GetMapping("/{placeId}")
    fun getPlaceDetail(
        @PathVariable placeId: String
    ): ApiResponse<PlaceDetailResponse> {
        // TODO: Implement with GetPlaceDetailUseCase
        throw NotImplementedError("To be implemented")
    }
}
