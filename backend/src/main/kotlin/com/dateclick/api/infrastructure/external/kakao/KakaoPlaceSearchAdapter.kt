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
    private val kakaoNaviRestClient: RestClient,
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

        // Haversine formula로 직선 거리 계산
        val straightDistance = calculateDistance(from, to)

        // 거리 기반 교통수단 자동 선택
        val transportType = determineTransportType(straightDistance)

        logger.debug(
            "Selected transport type: {}, straight distance: {}m",
            transportType,
            straightDistance.toInt()
        )

        // 교통수단별 경로 계산
        val route = when (transportType) {
            TransportType.CAR -> calculateCarRoute(from, to, straightDistance)
            TransportType.WALK -> calculateWalkRoute(from, to, straightDistance)
            TransportType.TRANSIT -> calculateTransitRoute(from, to, straightDistance)
        }

        // AC 2.4: 30분 이내 검증 (경고 로그)
        if (route.exceedsTimeLimit()) {
            logger.warn(
                "Route duration exceeds time limit: {}min > {}min (distance: {}m, type: {})",
                route.duration,
                Route.MAX_DURATION_MINUTES,
                route.distance,
                route.transportType
            )
        }

        return route
    }

    /**
     * 거리 기반 교통수단 자동 결정
     */
    private fun determineTransportType(distance: Double): TransportType {
        return when {
            distance < 1000 -> TransportType.WALK      // 1km 미만: 도보
            distance < 5000 -> TransportType.TRANSIT   // 5km 미만: 대중교통
            else -> TransportType.CAR                  // 5km 이상: 자동차
        }
    }

    /**
     * 자동차 경로 계산 (Kakao Mobility Directions API 사용)
     */
    private fun calculateCarRoute(from: Location, to: Location, fallbackDistance: Double): Route {
        return try {
            val response = kakaoNaviRestClient.get()
                .uri { builder ->
                    builder.path("/v1/directions")
                        .queryParam("origin", "${from.lng},${from.lat}")
                        .queryParam("destination", "${to.lng},${to.lat}")
                        .build()
                }
                .header(HttpHeaders.AUTHORIZATION, "KakaoAK $restApiKey")
                .retrieve()
                .body(KakaoDirectionsResponse::class.java)

            response?.routes?.firstOrNull()?.summary?.let { summary ->
                Route(
                    from = 0, // 실제 order는 호출하는 쪽에서 설정
                    to = 0,
                    distance = summary.distance,
                    duration = (summary.duration / 60.0).toInt(), // 초 -> 분 변환
                    transportType = TransportType.CAR,
                    description = "자동차 경로"
                ).also {
                    logger.info("Car route calculated: {}m, {}min", it.distance, it.duration)
                }
            } ?: createFallbackRoute(from, to, TransportType.CAR, fallbackDistance)
        } catch (e: Exception) {
            logger.warn("Failed to get car route from Kakao API, using fallback", e)
            createFallbackRoute(from, to, TransportType.CAR, fallbackDistance)
        }
    }

    /**
     * 도보 경로 계산 (Haversine + 평균 보행 속도)
     */
    private fun calculateWalkRoute(from: Location, to: Location, distance: Double): Route {
        // 도보: 평균 시속 4km (분당 67m)
        // 실제 도로를 따라가므로 직선거리 * 1.3 보정
        val actualDistance = (distance * 1.3).toInt()
        val duration = (actualDistance / 67.0).toInt().coerceAtLeast(5) // 최소 5분

        return Route(
            from = 0,
            to = 0,
            distance = actualDistance,
            duration = duration,
            transportType = TransportType.WALK,
            description = "도보 경로"
        ).also {
            logger.info("Walk route calculated: {}m, {}min", it.distance, it.duration)
        }
    }

    /**
     * 대중교통 경로 계산 (Haversine + 평균 대중교통 속도 + 대기시간)
     */
    private fun calculateTransitRoute(from: Location, to: Location, distance: Double): Route {
        // 대중교통: 평균 시속 20km (분당 333m) + 환승 및 대기시간
        // 실제 경로 보정 * 1.4 (우회 고려)
        val actualDistance = (distance * 1.4).toInt()
        val travelTime = (actualDistance / 333.0).toInt()
        val waitingTime = 5 // 기본 대기시간 5분
        val duration = (travelTime + waitingTime).coerceAtLeast(10) // 최소 10분

        return Route(
            from = 0,
            to = 0,
            distance = actualDistance,
            duration = duration,
            transportType = TransportType.TRANSIT,
            description = "대중교통 경로"
        ).also {
            logger.info("Transit route calculated: {}m, {}min", it.distance, it.duration)
        }
    }

    /**
     * API 호출 실패 시 fallback 경로
     */
    private fun createFallbackRoute(
        from: Location,
        to: Location,
        transportType: TransportType,
        distance: Double
    ): Route {
        val duration = estimateDuration(distance)

        return Route(
            from = 0,
            to = 0,
            distance = distance.toInt(),
            duration = duration,
            transportType = transportType,
            description = "${transportType.code} 경로 (추정)"
        ).also {
            logger.info("Fallback route created: {}m, {}min", it.distance, it.duration)
        }
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

/**
 * Kakao Mobility Directions API 응답 DTO
 */
data class KakaoDirectionsResponse(
    val trans_id: String,
    val routes: List<KakaoRoute>
)

data class KakaoRoute(
    val result_code: Int,
    val result_msg: String,
    val summary: KakaoRouteSummary
)

data class KakaoRouteSummary(
    val origin: KakaoRoutePoint,
    val destination: KakaoRoutePoint,
    val distance: Int,        // 거리 (미터)
    val duration: Int,        // 소요 시간 (초)
    val fare: KakaoFare? = null
)

data class KakaoRoutePoint(
    val name: String? = null,
    val x: Double,
    val y: Double
)

data class KakaoFare(
    val taxi: Int? = null,
    val toll: Int? = null
)
