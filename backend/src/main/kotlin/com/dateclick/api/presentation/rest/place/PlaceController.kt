package com.dateclick.api.presentation.rest.place

import com.dateclick.api.application.place.CuratePlaceUseCase
import com.dateclick.api.application.place.PlaceNotFoundException
import com.dateclick.api.domain.place.vo.PlaceId
import com.dateclick.api.infrastructure.external.openai.PlaceCurationException
import com.dateclick.api.presentation.rest.common.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * 장소 관련 REST API Controller
 */
@Tag(name = "Place", description = "장소 API")
@RestController
@RequestMapping("/api/v1/places")
class PlaceController(
    private val curatePlaceUseCase: CuratePlaceUseCase
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Operation(summary = "장소 큐레이션 조회", description = "AI 기반 장소 큐레이션 정보를 조회합니다")
    @GetMapping("/{placeId}/curation")
    fun getCuration(
        @PathVariable placeId: String
    ): ResponseEntity<ApiResponse<PlaceCurationResponse>> {
        logger.info("GET /api/v1/places/{}/curation", placeId)

        return try {
            val curationInfo = curatePlaceUseCase.execute(PlaceId(placeId))

            ResponseEntity.ok(
                ApiResponse.success(PlaceCurationResponse.from(curationInfo))
            )
        } catch (ex: PlaceNotFoundException) {
            logger.warn("Place not found: {}", placeId)
            ResponseEntity.status(404).body(
                ApiResponse.error("PLACE_NOT_FOUND", ex.message ?: "장소를 찾을 수 없습니다")
            )
        } catch (ex: PlaceCurationException) {
            logger.error("Failed to curate place", ex)
            ResponseEntity.status(500).body(
                ApiResponse.error("CURATION_FAILED", ex.message ?: "큐레이션에 실패했습니다")
            )
        } catch (ex: Exception) {
            logger.error("Unexpected error while curating place", ex)
            ResponseEntity.status(500).body(
                ApiResponse.error("INTERNAL_ERROR", "서버 오류가 발생했습니다")
            )
        }
    }
}
