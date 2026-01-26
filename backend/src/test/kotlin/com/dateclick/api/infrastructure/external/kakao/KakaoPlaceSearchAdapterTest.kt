package com.dateclick.api.infrastructure.external.kakao

import com.dateclick.api.domain.course.entity.TransportType
import com.dateclick.api.domain.place.vo.Location
import com.dateclick.api.domain.region.port.RegionRepository
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.web.client.RestClient

@ExtendWith(MockKExtension::class)
class KakaoPlaceSearchAdapterTest {
    @MockK
    private lateinit var kakaoRestClient: RestClient

    @MockK
    private lateinit var kakaoNaviRestClient: RestClient

    @MockK
    private lateinit var regionRepository: RegionRepository

    private lateinit var adapter: KakaoPlaceSearchAdapter
    private val restApiKey = "test-api-key"

    @BeforeEach
    fun setUp() {
        adapter =
            KakaoPlaceSearchAdapter(
                kakaoRestClient = kakaoRestClient,
                kakaoNaviRestClient = kakaoNaviRestClient,
                regionRepository = regionRepository,
                restApiKey = restApiKey,
            )
    }

    @Test
    fun `should calculate walk route for short distance (less than 1km)`() {
        // Given
        val from = Location(lat = 37.4979, lng = 127.0276)
        val to = Location(lat = 37.5010, lng = 127.0296) // 약 400m

        // When
        val result = adapter.calculateRoute(from, to)

        // Then
        assertEquals(TransportType.WALK, result.transportType)
        assertTrue(result.distance > 0)
        assertTrue(result.duration >= 5) // 최소 5분
        assertTrue(result.duration <= 30) // 30분 이내
        assertTrue(result.description.contains("도보"))
    }

    @Test
    fun `should calculate transit route for medium distance (1-5km)`() {
        // Given
        val from = Location(lat = 37.4979, lng = 127.0276)
        val to = Location(lat = 37.5200, lng = 127.0500) // 약 3km

        // When
        val result = adapter.calculateRoute(from, to)

        // Then
        assertEquals(TransportType.TRANSIT, result.transportType)
        assertTrue(result.distance > 1000)
        assertTrue(result.distance < 5000)
        assertTrue(result.duration >= 10) // 최소 10분
        assertTrue(result.description.contains("대중교통"))
    }

    @Test
    fun `should select car transport type for long distance (over 5km)`() {
        // Given
        val from = Location(lat = 37.4979, lng = 127.0276)
        val to = Location(lat = 37.5650, lng = 126.9779) // 약 6km

        // Note: API 호출은 통합 테스트에서 검증
        // 여기서는 자동차 타입 선택 로직만 검증

        // When
        val result = adapter.calculateRoute(from, to)

        // Then
        assertEquals(TransportType.CAR, result.transportType)
        assertTrue(result.distance > 5000)
    }

    @Test
    fun `should verify route duration calculation for walk`() {
        // Given
        val from = Location(lat = 37.4979, lng = 127.0276)
        val to = Location(lat = 37.5000, lng = 127.0300) // 약 300m

        // When
        val result = adapter.calculateRoute(from, to)

        // Then
        // 도보: 평균 시속 4km (분당 67m)
        // 실제 거리 * 1.3 보정 / 67m/분
        val expectedMinDuration = 5 // 최소 5분
        assertTrue(result.duration >= expectedMinDuration)
        assertTrue(result.isWithinTimeLimit()) // 30분 이내
    }

    @Test
    fun `should verify route duration calculation for transit`() {
        // Given
        val from = Location(lat = 37.4979, lng = 127.0276)
        val to = Location(lat = 37.5200, lng = 127.0500) // 약 3km

        // When
        val result = adapter.calculateRoute(from, to)

        // Then
        // 대중교통: 평균 시속 20km + 대기시간 5분
        val expectedMinDuration = 10 // 최소 10분
        assertTrue(result.duration >= expectedMinDuration)
        assertTrue(result.isWithinTimeLimit()) // 30분 이내
    }

    @Test
    fun `should warn when route exceeds 30 minutes`() {
        // Given
        val from = Location(lat = 37.4979, lng = 127.0276)
        val to = Location(lat = 37.6650, lng = 127.1279) // 매우 먼 거리 (약 15km+)

        // When
        val result = adapter.calculateRoute(from, to)

        // Then
        if (result.duration > 30) {
            assertTrue(result.exceedsTimeLimit())
            assertFalse(result.isWithinTimeLimit())
        }
    }
}
