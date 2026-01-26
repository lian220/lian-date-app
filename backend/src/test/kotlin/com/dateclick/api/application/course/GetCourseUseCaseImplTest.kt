package com.dateclick.api.application.course

import com.dateclick.api.domain.course.entity.Course
import com.dateclick.api.domain.course.entity.CoursePlace
import com.dateclick.api.domain.course.port.outbound.CourseRepository
import com.dateclick.api.domain.course.vo.*
import com.dateclick.api.domain.place.vo.Location
import com.dateclick.api.domain.place.vo.PlaceId
import com.dateclick.api.domain.region.vo.RegionId
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant

class GetCourseUseCaseImplTest {

    private lateinit var courseRepository: CourseRepository
    private lateinit var getCourseUseCase: GetCourseUseCaseImpl

    @BeforeEach
    fun setup() {
        courseRepository = mockk()
        getCourseUseCase = GetCourseUseCaseImpl(courseRepository)
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `execute는 존재하는 코스를 반환한다`() {
        // given
        val courseId = CourseId.generate()
        val testCourse = createTestCourse(courseId)

        every { courseRepository.findById(courseId) } returns testCourse

        // when
        val result = getCourseUseCase.execute(courseId)

        // then
        assertNotNull(result)
        assertEquals(courseId, result?.id)
        assertEquals("강남구", result?.regionName)
        assertEquals(3, result?.places?.size)
        verify(exactly = 1) { courseRepository.findById(courseId) }
    }

    @Test
    fun `execute는 존재하지 않는 코스일 때 null을 반환한다`() {
        // given
        val courseId = CourseId.generate()

        every { courseRepository.findById(courseId) } returns null

        // when
        val result = getCourseUseCase.execute(courseId)

        // then
        assertNull(result)
        verify(exactly = 1) { courseRepository.findById(courseId) }
    }

    @Test
    fun `execute는 코스의 모든 장소 정보를 포함한다`() {
        // given
        val courseId = CourseId.generate()
        val testCourse = createTestCourse(courseId)

        every { courseRepository.findById(courseId) } returns testCourse

        // when
        val result = getCourseUseCase.execute(courseId)

        // then
        assertNotNull(result)
        assertEquals(3, result?.places?.size)

        val firstPlace = result?.places?.get(0)
        assertEquals(1, firstPlace?.order)
        assertEquals("테스트 레스토랑", firstPlace?.name)
        assertEquals("음식점", firstPlace?.category)
    }

    private fun createTestCourse(courseId: CourseId): Course {
        val places = listOf(
            createTestPlace(1, "테스트 레스토랑", "음식점"),
            createTestPlace(2, "테스트 카페", "카페"),
            createTestPlace(3, "테스트 공원", "명소")
        )

        return Course(
            id = courseId,
            regionId = RegionId("region_test"),
            regionName = "강남구",
            dateType = DateType.ROMANTIC,
            budget = Budget(50000..100000),
            places = places,
            routes = emptyList(),
            createdAt = Instant.now(),
            sessionId = "test-session-123"
        )
    }

    private fun createTestPlace(order: Int, name: String, category: String): CoursePlace {
        return CoursePlace(
            order = order,
            placeId = PlaceId("place-test-$order"),
            name = name,
            category = category,
            categoryDetail = "${category} 상세",
            address = "서울시 강남구 테스트로 ${order}번길",
            roadAddress = "서울시 강남구 테스트대로 ${order * 10}",
            location = Location(lat = 37.5 + order * 0.01, lng = 127.0 + order * 0.01),
            phone = "02-1234-567$order",
            estimatedCost = EstimatedCost(50000),
            estimatedDuration = 90,
            recommendedTime = "오후 ${order + 5}시",
            recommendReason = "${name}은 데이트하기 좋은 장소입니다.",
            imageUrl = "https://example.com/image-$order.jpg",
            kakaoPlaceUrl = "https://place.map.kakao.com/$order"
        )
    }
}
