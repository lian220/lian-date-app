package com.dateclick.api.application.place

import com.dateclick.api.domain.course.port.outbound.PlaceSearchPort
import com.dateclick.api.domain.place.entity.Place
import com.dateclick.api.domain.place.port.PlaceCurationPort
import com.dateclick.api.domain.place.port.outbound.PlaceMemoryPort
import com.dateclick.api.domain.place.vo.*
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class PlaceMemoryServiceTest {

    private lateinit var placeSearchPort: PlaceSearchPort
    private lateinit var placeCurationPort: PlaceCurationPort
    private lateinit var placeMemoryPort: PlaceMemoryPort
    private lateinit var placeMemoryService: PlaceMemoryService

    @BeforeEach
    fun setup() {
        placeSearchPort = mockk()
        placeCurationPort = mockk()
        placeMemoryPort = mockk()
        placeMemoryService = PlaceMemoryService(
            placeSearchPort,
            placeCurationPort,
            placeMemoryPort
        )
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `buildPlaceMemory는 장소를 큐레이션하여 메모리에 저장한다`() {
        // given
        val placeId = PlaceId("test-place-1")
        val place = createTestPlace(placeId)
        val curation = createTestCuration()

        every { placeSearchPort.getPlaceDetail(placeId) } returns place
        every { placeCurationPort.curatePlace(place) } returns curation
        every { placeMemoryPort.addBatchToMemory(any()) } just Runs
        every { placeMemoryPort.count() } returns 1L

        // when
        val result = placeMemoryService.buildPlaceMemory(listOf(placeId))

        // then
        assertEquals(1, result)
        verify(exactly = 1) { placeSearchPort.getPlaceDetail(placeId) }
        verify(exactly = 1) { placeCurationPort.curatePlace(place) }
        verify(exactly = 1) { placeMemoryPort.addBatchToMemory(any()) }
    }

    @Test
    fun `빈 장소 리스트로 호출하면 0을 반환한다`() {
        // when
        val result = placeMemoryService.buildPlaceMemory(emptyList())

        // then
        assertEquals(0, result)
        verify(exactly = 0) { placeSearchPort.getPlaceDetail(any()) }
    }

    @Test
    fun `장소 정보 수집 실패 시 해당 장소는 건너뛴다`() {
        // given
        val placeId1 = PlaceId("place-1")
        val placeId2 = PlaceId("place-2")
        val place2 = createTestPlace(placeId2)
        val curation = createTestCuration()

        every { placeSearchPort.getPlaceDetail(placeId1) } returns null
        every { placeSearchPort.getPlaceDetail(placeId2) } returns place2
        every { placeCurationPort.curatePlace(place2) } returns curation
        every { placeMemoryPort.addBatchToMemory(any()) } just Runs
        every { placeMemoryPort.count() } returns 1L

        // when
        val result = placeMemoryService.buildPlaceMemory(listOf(placeId1, placeId2))

        // then
        assertEquals(1, result)
        verify(exactly = 1) { placeCurationPort.curatePlace(place2) }
    }

    @Test
    fun `큐레이션 실패 시 해당 장소는 건너뛴다`() {
        // given
        val placeId = PlaceId("test-place")
        val place = createTestPlace(placeId)

        every { placeSearchPort.getPlaceDetail(placeId) } returns place
        every { placeCurationPort.curatePlace(place) } throws RuntimeException("Curation failed")

        // when
        val result = placeMemoryService.buildPlaceMemory(listOf(placeId))

        // then
        assertEquals(0, result)
        verify(exactly = 0) { placeMemoryPort.addBatchToMemory(any()) }
    }

    @Test
    fun `searchPlaces는 유사한 장소를 검색한다`() {
        // given
        val query = "로맨틱한 레스토랑"
        val placeWithCuration = createTestPlaceWithCuration()

        every { placeMemoryPort.searchSimilar(query, 10) } returns listOf(placeWithCuration)

        // when
        val results = placeMemoryService.searchPlaces(query, 10)

        // then
        assertEquals(1, results.size)
        assertEquals(placeWithCuration, results[0])
        verify(exactly = 1) { placeMemoryPort.searchSimilar(query, 10) }
    }

    @Test
    fun `getMemoryCount는 저장된 장소 개수를 반환한다`() {
        // given
        every { placeMemoryPort.count() } returns 42L

        // when
        val count = placeMemoryService.getMemoryCount()

        // then
        assertEquals(42L, count)
    }

    @Test
    fun `clearMemory는 메모리를 초기화한다`() {
        // given
        every { placeMemoryPort.clearMemory() } just Runs

        // when
        placeMemoryService.clearMemory()

        // then
        verify(exactly = 1) { placeMemoryPort.clearMemory() }
    }

    private fun createTestPlace(placeId: PlaceId) = Place(
        id = placeId,
        name = "테스트 장소",
        category = "음식점",
        categoryDetail = "이탈리안",
        address = "서울시 강남구",
        roadAddress = null,
        phone = null,
        location = Location(lat = 37.5, lng = 127.0)
    )

    private fun createTestCuration() = PlaceCurationInfo(
        dateScore = 9,
        moodTags = listOf("#로맨틱", "#아늑한"),
        priceRange = 50000,
        bestTime = "저녁 7-9시",
        recommendation = "데이트하기 좋은 곳"
    )

    private fun createTestPlaceWithCuration() = PlaceWithCuration(
        place = createTestPlace(PlaceId("test-place")),
        curation = createTestCuration()
    )
}
