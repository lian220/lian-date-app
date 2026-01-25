package com.dateclick.api.infrastructure.external.kakao

import com.dateclick.api.domain.course.entity.Route
import com.dateclick.api.domain.course.entity.TransportType
import com.dateclick.api.domain.course.port.outbound.PlaceCategory
import com.dateclick.api.domain.course.port.outbound.PlaceSearchPort
import com.dateclick.api.domain.place.entity.Place
import com.dateclick.api.domain.place.vo.Location
import com.dateclick.api.domain.place.vo.PlaceId
import com.dateclick.api.domain.region.port.RegionRepository
import com.dateclick.api.domain.region.vo.RegionId
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

/**
 * Kakao Maps API 기반 장소 검색 어댑터
 */
@Component
class KakaoPlaceSearchAdapter(
    private val kakaoRestClient: RestClient,
    private val regionRepository: RegionRepository,
    @Value("\${kakao.api.rest-key}") private val restApiKey: String
) : PlaceSearchPort {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun searchPlaces(
        regionId: RegionId,
        category: PlaceCategory,
        limit: Int
    ): List<Place> {
        val region = regionRepository.findById(regionId)
            ?: throw IllegalArgumentException("Region not found: ${regionId.value}")

        logger.debug(
            "Searching places: region={}, category={}, limit={}",
            region.name,
            category,
            limit
        )

        return try {
            val response = kakaoRestClient.get()
                .uri { builder ->
                    builder.path("/v2/local/search/keyword.json")
                        .queryParam("query", "${region.name} ${category.toKakaoQuery()}")
                        .queryParam("x", region.centerLng)
                        .queryParam("y", region.centerLat)
                        .queryParam("radius", 5000)
                        .queryParam("size", limit.coerceAtMost(15))
                        .queryParam("sort", "accuracy")
                        .build()
                }
                .header(HttpHeaders.AUTHORIZATION, "KakaoAK $restApiKey")
                .retrieve()
                .body(KakaoPlaceSearchResponse::class.java)
                ?: throw RuntimeException("Empty response from Kakao API")

            response.documents.map { doc ->
                Place(
                    id = PlaceId(doc.id),
                    name = doc.place_name,
                    category = doc.category_group_name ?: category.toKakaoQuery(),
                    categoryDetail = doc.category_name,
                    location = Location(
                        lat = doc.y.toDouble(),
                        lng = doc.x.toDouble()
                    ),
                    address = doc.address_name,
                    roadAddress = doc.road_address_name,
                    phone = doc.phone,
                    kakaoPlaceUrl = doc.place_url,
                    kakaoRating = null,
                    kakaoReviewCount = null
                )
            }.also {
                logger.info("Found {} places for {}", it.size, category)
            }
        } catch (e: Exception) {
            logger.error("Failed to search places from Kakao API", e)
            emptyList()
        }
    }

    override fun getPlaceDetail(placeId: PlaceId): Place? {
        logger.debug("Getting place detail: {}", placeId.value)

        return try {
            val response = kakaoRestClient.get()
                .uri { builder ->
                    builder.path("/v2/local/search/keyword.json")
                        .queryParam("query", placeId.value)
                        .build()
                }
                .header(HttpHeaders.AUTHORIZATION, "KakaoAK $restApiKey")
                .retrieve()
                .body(KakaoPlaceSearchResponse::class.java)

            response?.documents?.firstOrNull()?.let { doc ->
                Place(
                    id = PlaceId(doc.id),
                    name = doc.place_name,
                    category = doc.category_group_name ?: "기타",
                    categoryDetail = doc.category_name,
                    location = Location(
                        lat = doc.y.toDouble(),
                        lng = doc.x.toDouble()
                    ),
                    address = doc.address_name,
                    roadAddress = doc.road_address_name,
                    phone = doc.phone,
                    kakaoPlaceUrl = doc.place_url,
                    kakaoRating = null,
                    kakaoReviewCount = null
                )
            }
        } catch (e: Exception) {
            logger.error("Failed to get place detail from Kakao API", e)
            null
        }
    }

    override fun calculateRoute(from: Location, to: Location): Route {
        logger.debug(
            "Calculating route: from=({}, {}), to=({}, {})",
            from.lat,
            from.lng,
            to.lat,
            to.lng
        )

        // 간단한 거리 계산 (Haversine formula)
        val distance = calculateDistance(from, to)
        val duration = estimateDuration(distance)

        return Route(
            from = 0, // 실제 order는 호출하는 쪽에서 설정
            to = 0,
            distance = distance.toInt(),
            duration = duration,
            transportType = if (distance < 1000) TransportType.WALK else TransportType.TRANSIT,
            description = "이동 경로"
        )
    }

    /**
     * Haversine formula를 사용한 두 지점 간 거리 계산 (미터 단위)
     */
    private fun calculateDistance(from: Location, to: Location): Double {
        val earthRadius = 6371000.0 // 지구 반지름 (미터)
        val dLat = Math.toRadians(to.lat - from.lat)
        val dLon = Math.toRadians(to.lng - from.lng)

        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(from.lat)) * Math.cos(Math.toRadians(to.lat)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)

        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        return earthRadius * c
    }

    /**
     * 거리 기반 예상 소요 시간 계산 (분 단위)
     */
    private fun estimateDuration(distanceInMeters: Double): Int {
        return when {
            distanceInMeters < 500 -> 5 // 500m 미만: 도보 5분
            distanceInMeters < 1000 -> 10 // 1km 미만: 도보 10분
            distanceInMeters < 2000 -> 15 // 2km 미만: 도보 15분
            else -> (distanceInMeters / 400).toInt().coerceAtLeast(20) // 대중교통 기준 (시속 24km)
        }
    }
}

/**
 * Kakao Place Search API 응답 DTO
 */
data class KakaoPlaceSearchResponse(
    val documents: List<KakaoPlaceDocument>,
    val meta: KakaoMeta
)

data class KakaoPlaceDocument(
    val id: String,
    val place_name: String,
    val category_name: String?,
    val category_group_name: String?,
    val phone: String?,
    val address_name: String,
    val road_address_name: String?,
    val x: String, // longitude
    val y: String, // latitude
    val place_url: String
)

data class KakaoMeta(
    val total_count: Int,
    val pageable_count: Int,
    val is_end: Boolean
)
