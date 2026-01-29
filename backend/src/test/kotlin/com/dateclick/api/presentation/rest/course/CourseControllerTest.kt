package com.dateclick.api.presentation.rest.course

import com.dateclick.api.application.course.CreateCourseUseCase
import com.dateclick.api.application.course.GetCourseUseCase
import com.dateclick.api.application.course.RegenerateCourseUseCase
import com.dateclick.api.domain.course.entity.Course
import com.dateclick.api.infrastructure.ratelimit.RateLimitService
import com.dateclick.api.domain.course.entity.CoursePlace
import com.dateclick.api.domain.course.entity.Route
import com.dateclick.api.domain.course.entity.TransportType
import com.dateclick.api.domain.course.vo.Budget
import com.dateclick.api.domain.course.vo.CourseId
import com.dateclick.api.domain.course.vo.DateType
import com.dateclick.api.domain.course.vo.EstimatedCost
import com.dateclick.api.domain.place.vo.Location
import com.dateclick.api.domain.place.vo.PlaceId
import com.dateclick.api.domain.region.vo.RegionId
import com.dateclick.api.presentation.mapper.CourseMapper
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.time.Instant

@SpringBootTest
@AutoConfigureMockMvc
class CourseControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var createCourseUseCase: CreateCourseUseCase

    @MockkBean
    private lateinit var getCourseUseCase: GetCourseUseCase

    @MockkBean
    private lateinit var regenerateCourseUseCase: RegenerateCourseUseCase

    @MockkBean
    private lateinit var courseMapper: CourseMapper

    @MockkBean(relaxed = true)
    private lateinit var rateLimitService: RateLimitService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `POST courses - 코스 생성 성공`() {
        // Given
        val request =
            CreateCourseRequest(
                regionId = "gangnam",
                dateType = "romantic",
                budget = "30000-50000",
                specialRequest = "조용한 곳으로",
            )

        val mockCourse = createMockCourse()
        val mockResponse = createMockResponse()

        every { courseMapper.toCommand(any(), any()) } returns createMockCommand()
        every { createCourseUseCase.execute(any()) } returns mockCourse
        every { courseMapper.toResponse(any()) } returns mockResponse

        // When & Then
        mockMvc.post("/v1/courses") {
            header("X-Session-Id", "test-session-123")
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
            jsonPath("$.success") { value(true) }
            jsonPath("$.data.courseId") { exists() }
            jsonPath("$.data.places") { isArray() }
            jsonPath("$.data.routes") { isArray() }
            jsonPath("$.data.totalEstimatedCost") { isNumber() }
        }

        verify(exactly = 1) { createCourseUseCase.execute(any()) }
    }

    @Test
    fun `POST courses - 필수 필드 누락 시 400 에러`() {
        // Given
        val invalidRequest =
            """
            {
                "regionId": "",
                "dateType": "romantic",
                "budget": "30000-50000"
            }
            """.trimIndent()

        // When & Then
        mockMvc.post("/v1/courses") {
            header("X-Session-Id", "test-session-123")
            contentType = MediaType.APPLICATION_JSON
            content = invalidRequest
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `POST courses regenerate - 코스 재생성 성공`() {
        // Given
        val courseId = "course-123"
        val request =
            RegenerateCourseRequest(
                excludePlaceIds = listOf("place-1"),
            )

        val mockCourse = createMockCourse()
        val mockResponse = createMockResponse()

        every { courseMapper.toRegenerateCommand(any(), any(), any()) } returns createMockRegenerateCommand()
        every { regenerateCourseUseCase.execute(any()) } returns mockCourse
        every { courseMapper.toResponse(any()) } returns mockResponse

        // When & Then
        mockMvc.post("/v1/courses/$courseId/regenerate") {
            header("X-Session-Id", "test-session-123")
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
            jsonPath("$.success") { value(true) }
            jsonPath("$.data.courseId") { exists() }
        }

        verify(exactly = 1) { regenerateCourseUseCase.execute(any()) }
    }

    @Test
    fun `GET courses - 코스 조회 성공`() {
        // Given
        val courseId = "course_123"
        val mockCourse = createMockCourse()
        val mockResponse = createMockResponse()

        every { getCourseUseCase.execute(CourseId(courseId)) } returns mockCourse
        every { courseMapper.toResponse(mockCourse) } returns mockResponse

        // When & Then
        mockMvc.get("/v1/courses/$courseId") {
        }.andExpect {
            status { isOk() }
            jsonPath("$.success") { value(true) }
            jsonPath("$.data.courseId") { value("course-123") }
            jsonPath("$.data.regionName") { value("강남") }
            jsonPath("$.data.places") { isArray() }
            jsonPath("$.data.routes") { isArray() }
            jsonPath("$.data.totalEstimatedCost") { value(50000) }
        }

        verify(exactly = 1) { getCourseUseCase.execute(CourseId(courseId)) }
    }

    @Test
    fun `GET courses - 존재하지 않는 코스 조회 시 404 에러`() {
        // Given
        val courseId = "course_non_existent"

        every { getCourseUseCase.execute(CourseId(courseId)) } returns null

        // When & Then
        mockMvc.get("/v1/courses/$courseId") {
        }.andExpect {
            status { isNotFound() }
            jsonPath("$.success") { value(false) }
            jsonPath("$.error.code") { value("NOT_FOUND") }
        }

        verify(exactly = 1) { getCourseUseCase.execute(CourseId(courseId)) }
    }

    private fun createMockCommand() =
        com.dateclick.api.application.course.CreateCourseCommand(
            regionId = RegionId("gangnam"),
            dateType = DateType.ROMANTIC,
            budget = Budget.from("30000-50000"),
            specialRequest = "조용한 곳으로",
            sessionId = "test-session-123",
        )

    private fun createMockRegenerateCommand() =
        com.dateclick.api.application.course.RegenerateCourseCommand(
            originalCourseId = CourseId("course_123"),
            excludePlaceIds = listOf("place-1"),
            sessionId = "test-session-123",
        )

    private fun createMockResponse() =
        CourseResponse(
            courseId = "course-123",
            regionId = "gangnam",
            regionName = "강남",
            dateType = "romantic",
            budget = "3~5만원",
            totalEstimatedCost = 50000,
            places =
                listOf(
                    CoursePlaceResponse(
                        order = 1,
                        placeId = "place-1",
                        name = "테스트 카페",
                        category = "카페",
                        categoryDetail = "디저트카페",
                        address = "서울시 강남구",
                        roadAddress = "강남대로 123",
                        lat = 37.5,
                        lng = 127.0,
                        phone = "02-1234-5678",
                        estimatedCost = 15000,
                        estimatedDuration = 60,
                        recommendedTime = "14:00",
                        recommendReason = "분위기 좋은 카페",
                        imageUrl = "https://example.com/image.jpg",
                        kakaoPlaceUrl = "https://place.map.kakao.com/123",
                    ),
                ),
            routes =
                listOf(
                    RouteResponse(
                        from = 1,
                        to = 2,
                        distance = 500,
                        duration = 7,
                        transportType = "walk",
                        description = "도보 7분",
                    ),
                ),
            createdAt = Instant.now(),
        )

    private fun createMockCourse(): Course {
        val places =
            listOf(
                CoursePlace(
                    order = 1,
                    placeId = PlaceId("place-1"),
                    name = "테스트 카페",
                    category = "카페",
                    categoryDetail = "디저트카페",
                    address = "서울시 강남구",
                    roadAddress = "강남대로 123",
                    location = Location(37.5, 127.0),
                    phone = "02-1234-5678",
                    estimatedCost = EstimatedCost(15000),
                    estimatedDuration = 60,
                    recommendedTime = "14:00",
                    recommendReason = "분위기 좋은 카페",
                    imageUrl = "https://example.com/image.jpg",
                    kakaoPlaceUrl = "https://place.map.kakao.com/123",
                ),
                CoursePlace(
                    order = 2,
                    placeId = PlaceId("place-2"),
                    name = "테스트 레스토랑",
                    category = "음식점",
                    categoryDetail = "이탈리안",
                    address = "서울시 강남구",
                    roadAddress = "강남대로 456",
                    location = Location(37.51, 127.01),
                    phone = "02-2345-6789",
                    estimatedCost = EstimatedCost(35000),
                    estimatedDuration = 90,
                    recommendedTime = "18:00",
                    recommendReason = "맛있는 이탈리안",
                    imageUrl = "https://example.com/image2.jpg",
                    kakaoPlaceUrl = "https://place.map.kakao.com/456",
                ),
            )

        val routes =
            listOf(
                Route(
                    from = 1,
                    to = 2,
                    distance = 500,
                    duration = 7,
                    transportType = TransportType.WALK,
                    description = "도보 7분",
                ),
            )

        return Course(
            id = CourseId.generate(),
            regionId = RegionId("gangnam"),
            regionName = "강남",
            dateType = DateType.ROMANTIC,
            budget = Budget.from("30000-50000"),
            places = places,
            routes = routes,
            createdAt = Instant.now(),
            sessionId = "test-session-123",
        )
    }
}
