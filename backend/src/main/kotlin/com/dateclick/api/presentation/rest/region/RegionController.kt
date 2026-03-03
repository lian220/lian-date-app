package com.dateclick.api.presentation.rest.region

import com.dateclick.api.application.region.GetRegionsUseCase
import com.dateclick.api.presentation.rest.common.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Region", description = "지역 목록 조회 API")
@RestController
@RequestMapping("/v1/regions")
class RegionController(
    private val getRegionsUseCase: GetRegionsUseCase,
) {
    @Operation(summary = "지역 목록 조회", description = "데이트 코스 생성에 사용 가능한 지역 목록을 조회합니다. city 파라미터로 특정 도시의 지역만 필터링할 수 있습니다.")
    @GetMapping
    fun getRegions(
        @Parameter(description = "도시명 필터 (예: 서울, 부산). 생략 시 전체 지역 반환", example = "서울")
        @RequestParam(required = false) city: String?,
    ): ApiResponse<RegionListResponse> {
        val regions = getRegionsUseCase.execute(city)
        val response =
            RegionListResponse(
                regions =
                    regions.map { region ->
                        RegionResponse(
                            id = region.id.value,
                            name = region.name,
                            city = region.city,
                            description = region.description,
                            keywords = region.keywords,
                            centerLat = region.centerLat,
                            centerLng = region.centerLng,
                        )
                    },
            )
        return ApiResponse.success(response)
    }
}
