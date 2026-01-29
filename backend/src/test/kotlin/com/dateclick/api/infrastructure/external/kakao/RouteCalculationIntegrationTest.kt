package com.dateclick.api.infrastructure.external.kakao

import com.dateclick.api.domain.course.entity.TransportType
import com.dateclick.api.domain.place.vo.Location
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

/**
 * LAD-27: 경로 계산 서비스 통합 테스트
 * 실제 Spring Context에서 KakaoPlaceSearchAdapter의 calculateRoute() 메서드 검증
 */
@SpringBootTest
class RouteCalculationIntegrationTest {
    @Autowired
    private lateinit var kakaoPlaceSearchAdapter: KakaoPlaceSearchAdapter

    @Test
    fun `should calculate walk route for short distance in real context`() {
        // Given: 강남역 근처 두 지점 (약 500m)
        val from = Location(lat = 37.4979, lng = 127.0276)
        val to = Location(lat = 37.5010, lng = 127.0296)

        // When
        val route = kakaoPlaceSearchAdapter.calculateRoute(from, to)

        // Then
        assertEquals(TransportType.WALK, route.transportType)
        assertTrue(route.distance > 0, "Distance should be calculated")
        assertTrue(route.duration >= 5, "Walk duration should be at least 5 minutes")
        assertTrue(route.duration <= 30, "Route should be within 30 minutes (AC 2.4)")
        assertTrue(route.isWithinTimeLimit(), "Route should pass time limit validation")
        assertTrue(route.description.contains("도보"), "Description should indicate walking")
    }

    @Test
    fun `should calculate transit route for medium distance in real context`() {
        // Given: 강남역 → 선릉역 (약 2.5km)
        val from = Location(lat = 37.4979, lng = 127.0276)
        val to = Location(lat = 37.5044, lng = 127.0491)

        // When
        val route = kakaoPlaceSearchAdapter.calculateRoute(from, to)

        // Then
        assertEquals(TransportType.TRANSIT, route.transportType)
        assertTrue(route.distance > 1000, "Distance should be over 1km")
        assertTrue(route.distance < 5000, "Distance should be under 5km")
        assertTrue(route.duration >= 10, "Transit duration should be at least 10 minutes")
        assertTrue(route.isWithinTimeLimit(), "Route should pass time limit validation")
        assertTrue(route.description.contains("대중교통"), "Description should indicate transit")
    }

    @Test
    fun `should select car transport type for long distance in real context`() {
        // Given: 강남역 → 홍대입구역 (약 10km)
        val from = Location(lat = 37.4979, lng = 127.0276)
        val to = Location(lat = 37.5577, lng = 126.9250)

        // When
        val route = kakaoPlaceSearchAdapter.calculateRoute(from, to)

        // Then
        assertEquals(TransportType.CAR, route.transportType)
        assertTrue(route.distance > 5000, "Distance should be over 5km")
        // Note: Car route uses Kakao API or fallback, both should work
        assertTrue(route.duration > 0, "Duration should be calculated")
    }

    @Test
    fun `should validate 30 minute time limit (AC 2-4)`() {
        // Given: Various distances
        val testCases =
            listOf(
                // Short distance (should pass)
                Pair(
                    Location(37.4979, 127.0276),
                    Location(37.5010, 127.0296),
                ),
                // Medium distance (should pass)
                Pair(
                    Location(37.4979, 127.0276),
                    Location(37.5007, 127.0363),
                ),
            )

        testCases.forEach { (from, to) ->
            // When
            val route = kakaoPlaceSearchAdapter.calculateRoute(from, to)

            // Then
            assertTrue(
                route.isWithinTimeLimit(),
                "Route from (${from.lat},${from.lng}) to (${to.lat},${to.lng}) " +
                    "should be within 30 minutes, but was ${route.duration} minutes",
            )
            assertDoesNotThrow(
                { route.validateTimeLimit() },
                "Route validation should not throw exception",
            )
        }
    }

    @Test
    fun `should calculate realistic durations for different transport types`() {
        // Given: Same distance, different contexts
        val from = Location(lat = 37.4979, lng = 127.0276)
        val shortDest = Location(lat = 37.5000, lng = 127.0290) // ~300m
        val mediumDest = Location(lat = 37.5100, lng = 127.0400) // ~2km

        // When
        val walkRoute = kakaoPlaceSearchAdapter.calculateRoute(from, shortDest)
        val transitRoute = kakaoPlaceSearchAdapter.calculateRoute(from, mediumDest)

        // Then: Duration should be realistic
        // Walk: ~300m at 4km/h = ~5 minutes
        assertTrue(
            walkRoute.duration >= 5 && walkRoute.duration <= 15,
            "Walk duration should be realistic: ${walkRoute.duration} min",
        )

        // Transit: ~2km at 20km/h + wait time = ~10-20 minutes
        assertTrue(
            transitRoute.duration >= 10 && transitRoute.duration <= 25,
            "Transit duration should be realistic: ${transitRoute.duration} min",
        )
    }
}
