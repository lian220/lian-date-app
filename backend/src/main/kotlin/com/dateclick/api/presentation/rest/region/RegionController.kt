package com.dateclick.api.presentation.rest.region

import com.dateclick.api.presentation.rest.common.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/regions")
class RegionController {

    @GetMapping
    fun getRegions(
        @RequestParam(required = false) city: String?
    ): ApiResponse<RegionListResponse> {
        // TODO: Implement with GetRegionsUseCase
        throw NotImplementedError("To be implemented")
    }
}
