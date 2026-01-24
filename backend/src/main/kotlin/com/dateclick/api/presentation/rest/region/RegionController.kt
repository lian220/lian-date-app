package com.dateclick.api.presentation.rest.region

import com.dateclick.api.application.region.GetRegionsUseCase
import com.dateclick.api.presentation.rest.common.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/regions")
class RegionController(
    private val getRegionsUseCase: GetRegionsUseCase
) {

    @GetMapping
    fun getRegions(
        @RequestParam(required = false) city: String?
    ): ApiResponse<RegionListResponse> {
        val regions = getRegionsUseCase.execute(city)
        val response = RegionListResponse(
            regions = regions.map { region ->
                RegionResponse(
                    id = region.id.value,
                    name = region.name,
                    city = region.city,
                    description = region.description,
                    keywords = region.keywords,
                    centerLat = region.centerLat,
                    centerLng = region.centerLng
                )
            }
        )
        return ApiResponse.success(response)
    }
}
