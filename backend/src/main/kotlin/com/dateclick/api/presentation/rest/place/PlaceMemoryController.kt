package com.dateclick.api.presentation.rest.place

import com.dateclick.api.application.place.PlaceMemoryService
import com.dateclick.api.domain.place.vo.PlaceId
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * 장소 메모리 관리 컨트롤러
 * Vector DB를 활용한 장소 큐레이션 정보 저장 및 검색
 */
@Tag(name = "Place Memory", description = "장소 메모리 관리 API (Vector DB)")
@RestController
@RequestMapping("/api/v1/place-memory")
class PlaceMemoryController(
    private val placeMemoryService: PlaceMemoryService
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * 장소 메모리 구축
     * 제공된 장소 ID들을 큐레이션하여 Vector DB에 저장
     */
    @Operation(summary = "장소 메모리 구축", description = "장소 리스트를 AI 큐레이션하여 Vector DB에 저장")
    @PostMapping("/build")
    fun buildMemory(
        @RequestBody request: BuildMemoryRequest
    ): ResponseEntity<BuildMemoryResponse> {
        logger.info("Building place memory for {} places", request.placeIds.size)

        val placeIds = request.placeIds.map { PlaceId(it) }
        val savedCount = placeMemoryService.buildPlaceMemory(placeIds)

        return ResponseEntity.ok(
            BuildMemoryResponse(
                savedCount = savedCount,
                totalPlaces = placeMemoryService.getMemoryCount()
            )
        )
    }

    /**
     * 의미 기반 장소 검색
     */
    @Operation(summary = "장소 검색", description = "자연어 쿼리로 유사한 장소 검색")
    @GetMapping("/search")
    fun searchPlaces(
        @RequestParam query: String,
        @RequestParam(defaultValue = "10") limit: Int
    ): ResponseEntity<SearchPlacesResponse> {
        logger.info("Searching places with query: '{}'", query)

        val results = placeMemoryService.searchPlaces(query, limit)

        return ResponseEntity.ok(
            SearchPlacesResponse(
                query = query,
                count = results.size,
                places = results.map { PlaceMemoryDto.from(it) }
            )
        )
    }

    /**
     * 저장된 장소 개수 조회
     */
    @Operation(summary = "메모리 통계", description = "Vector DB에 저장된 장소 개수 조회")
    @GetMapping("/stats")
    fun getStats(): ResponseEntity<MemoryStatsResponse> {
        val count = placeMemoryService.getMemoryCount()

        return ResponseEntity.ok(
            MemoryStatsResponse(totalPlaces = count)
        )
    }

    /**
     * 메모리 초기화 (개발용)
     */
    @Operation(summary = "메모리 초기화", description = "Vector DB의 모든 장소 삭제 (개발용)")
    @DeleteMapping("/clear")
    fun clearMemory(): ResponseEntity<ClearMemoryResponse> {
        logger.warn("Clearing all place memory")

        placeMemoryService.clearMemory()

        return ResponseEntity.ok(
            ClearMemoryResponse(message = "Place memory cleared successfully")
        )
    }
}

// Request DTOs
data class BuildMemoryRequest(
    val placeIds: List<String>
)

// Response DTOs
data class BuildMemoryResponse(
    val savedCount: Int,
    val totalPlaces: Long
)

data class SearchPlacesResponse(
    val query: String,
    val count: Int,
    val places: List<PlaceMemoryDto>
)

data class MemoryStatsResponse(
    val totalPlaces: Long
)

data class ClearMemoryResponse(
    val message: String
)

data class PlaceMemoryDto(
    val id: String,
    val name: String,
    val category: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val dateScore: Int,
    val moodTags: List<String>,
    val priceRange: Int,
    val bestTime: String,
    val recommendation: String,
    val kakaoRating: Double?,
    val kakaoReviewCount: Int?
) {
    companion object {
        fun from(placeWithCuration: com.dateclick.api.domain.place.vo.PlaceWithCuration): PlaceMemoryDto {
            val place = placeWithCuration.place
            val curation = placeWithCuration.curation

            return PlaceMemoryDto(
                id = place.id.value,
                name = place.name,
                category = place.category,
                address = place.address,
                latitude = place.location.lat,
                longitude = place.location.lng,
                dateScore = curation.dateScore,
                moodTags = curation.moodTags,
                priceRange = curation.priceRange,
                bestTime = curation.bestTime,
                recommendation = curation.recommendation,
                kakaoRating = place.kakaoRating,
                kakaoReviewCount = place.kakaoReviewCount
            )
        }
    }
}
