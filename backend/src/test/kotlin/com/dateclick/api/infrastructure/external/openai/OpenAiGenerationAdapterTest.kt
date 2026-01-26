package com.dateclick.api.infrastructure.external.openai

import com.dateclick.api.domain.course.vo.Budget
import com.dateclick.api.domain.course.vo.DateType
import com.dateclick.api.domain.place.entity.Place
import com.dateclick.api.domain.place.vo.Location
import com.dateclick.api.domain.place.vo.PlaceId
import com.dateclick.api.domain.region.entity.Region
import com.dateclick.api.domain.region.vo.RegionId
import com.dateclick.api.infrastructure.config.OpenAiProperties
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class OpenAiGenerationAdapterTest {
    @MockK
    private lateinit var openAiClient: OpenAiClient

    private lateinit var openAiProperties: OpenAiProperties
    private lateinit var objectMapper: ObjectMapper
    private lateinit var adapter: OpenAiGenerationAdapter

    @BeforeEach
    fun setUp() {
        openAiProperties =
            OpenAiProperties(
                apiKey = "test-api-key",
                model = "gpt-4",
                maxTokens = 2000,
                temperature = 0.7,
            )
        objectMapper = jacksonObjectMapper()
        adapter = OpenAiGenerationAdapter(openAiClient, openAiProperties, objectMapper)
    }

    @Test
    fun `should successfully generate course recommendation`() {
        // Given
        val region = createTestRegion()
        val dateType = DateType.ROMANTIC
        val budget = Budget(50000..100000)

        val mockResponse = createMockChatCompletionResponse()
        every { openAiClient.createChatCompletion(any()) } returns mockResponse

        // When
        val result =
            adapter.generateCourseRecommendation(
                region = region,
                dateType = dateType,
                budget = budget,
                specialRequest = null,
            )

        // Then
        assertNotNull(result)
        assertEquals(3, result.places.size)
        assertEquals("테스트 카페", result.places[0].name)
        assertEquals(1, result.places[0].order)
        assertEquals("멋진 카페입니다", result.places[0].recommendReason)
        assertEquals(15000, result.places[0].estimatedCost)
        assertEquals(60, result.places[0].estimatedDuration)
        assertEquals("14:00-15:00", result.places[0].recommendedTime)
        assertEquals("로맨틱한 데이트 코스입니다", result.summary)

        verify(exactly = 1) { openAiClient.createChatCompletion(any()) }
    }

    @Test
    fun `should throw AiGenerationException when OpenAI API fails`() {
        // Given
        val region = createTestRegion()
        val dateType = DateType.FOOD
        val budget = Budget(0..30000)

        every { openAiClient.createChatCompletion(any()) } throws OpenAiException("API call failed")

        // When & Then
        assertThrows<AiGenerationException> {
            adapter.generateCourseRecommendation(
                region = region,
                dateType = dateType,
                budget = budget,
                specialRequest = null,
            )
        }
    }

    @Test
    fun `should throw AiGenerationException when response is empty`() {
        // Given
        val region = createTestRegion()
        val dateType = DateType.ACTIVITY
        val budget = Budget(50000..100000)

        val emptyResponse = createMockChatCompletionResponse(choices = emptyList())
        every { openAiClient.createChatCompletion(any()) } returns emptyResponse

        // When & Then
        assertThrows<AiGenerationException> {
            adapter.generateCourseRecommendation(
                region = region,
                dateType = dateType,
                budget = budget,
                specialRequest = null,
            )
        }
    }

    @Test
    fun `should throw AiGenerationException when response JSON is invalid`() {
        // Given
        val region = createTestRegion()
        val dateType = DateType.CULTURE
        val budget = Budget(30000..50000)

        val invalidResponse =
            createMockChatCompletionResponse(
                messageContent = "Invalid JSON content",
            )
        every { openAiClient.createChatCompletion(any()) } returns invalidResponse

        // When & Then
        assertThrows<AiGenerationException> {
            adapter.generateCourseRecommendation(
                region = region,
                dateType = dateType,
                budget = budget,
                specialRequest = null,
            )
        }
    }

    private fun createTestRegion(): Region {
        return Region(
            id = RegionId("seoul-gangnam"),
            name = "강남구",
            city = "seoul",
            description = "서울 강남구",
            keywords = listOf("쇼핑", "맛집"),
            centerLat = 37.4979,
            centerLng = 127.0276,
        )
    }

    private fun createTestPlaces(): List<Place> {
        return listOf(
            Place(
                id = PlaceId("place-1"),
                name = "테스트 카페",
                category = "카페",
                categoryDetail = "카페,디저트",
                location = Location(37.4979, 127.0276),
                address = "서울 강남구 테스트동 1",
                roadAddress = "서울 강남구 테스트로 1",
                phone = "02-1234-5678",
                kakaoRating = 4.5,
                kakaoReviewCount = 100,
                kakaoPlaceUrl = "https://place.map.kakao.com/1",
            ),
            Place(
                id = PlaceId("place-2"),
                name = "테스트 레스토랑",
                category = "음식점",
                categoryDetail = "한식",
                location = Location(37.4980, 127.0277),
                address = "서울 강남구 테스트동 2",
                roadAddress = "서울 강남구 테스트로 2",
                phone = "02-1234-5679",
                kakaoRating = 4.8,
                kakaoReviewCount = 200,
                kakaoPlaceUrl = "https://place.map.kakao.com/2",
            ),
            Place(
                id = PlaceId("place-3"),
                name = "테스트 영화관",
                category = "문화시설",
                categoryDetail = "영화관",
                location = Location(37.4981, 127.0278),
                address = "서울 강남구 테스트동 3",
                roadAddress = "서울 강남구 테스트로 3",
                phone = "02-1234-5680",
                kakaoRating = 4.3,
                kakaoReviewCount = 150,
                kakaoPlaceUrl = "https://place.map.kakao.com/3",
            ),
        )
    }

    private fun createMockChatCompletionResponse(
        choices: List<Choice> =
            listOf(
                Choice(
                    index = 0,
                    message = Message.assistant(createMockAiCourseResponseJson()),
                    finishReason = "stop",
                ),
            ),
        messageContent: String? = null,
    ): ChatCompletionResponse {
        val actualChoices =
            if (messageContent != null) {
                listOf(
                    Choice(
                        index = 0,
                        message = Message.assistant(messageContent),
                        finishReason = "stop",
                    ),
                )
            } else {
                choices
            }

        return ChatCompletionResponse(
            id = "chatcmpl-123",
            `object` = "chat.completion",
            created = System.currentTimeMillis() / 1000,
            model = "gpt-4",
            choices = actualChoices,
            usage =
                Usage(
                    promptTokens = 100,
                    completionTokens = 200,
                    totalTokens = 300,
                ),
        )
    }

    private fun createMockAiCourseResponseJson(): String {
        return """
            {
                "places": [
                    {
                        "order": 1,
                        "name": "테스트 카페",
                        "category": "카페",
                        "category_detail": "카페,디저트",
                        "address": "서울 강남구 테스트동 1",
                        "road_address": "서울 강남구 테스트로 1",
                        "location": {
                            "lat": 37.4979,
                            "lng": 127.0276
                        },
                        "phone": "02-1234-5678",
                        "recommend_reason": "멋진 카페입니다",
                        "estimated_cost": 15000,
                        "estimated_duration": 60,
                        "recommended_time": "14:00-15:00"
                    },
                    {
                        "order": 2,
                        "name": "테스트 레스토랑",
                        "category": "음식점",
                        "category_detail": "한식",
                        "address": "서울 강남구 테스트동 2",
                        "road_address": "서울 강남구 테스트로 2",
                        "location": {
                            "lat": 37.4980,
                            "lng": 127.0277
                        },
                        "phone": "02-1234-5679",
                        "recommend_reason": "맛있는 레스토랑입니다",
                        "estimated_cost": 35000,
                        "estimated_duration": 90,
                        "recommended_time": "18:00-19:30"
                    },
                    {
                        "order": 3,
                        "name": "테스트 영화관",
                        "category": "문화시설",
                        "category_detail": "영화관",
                        "address": "서울 강남구 테스트동 3",
                        "road_address": "서울 강남구 테스트로 3",
                        "location": {
                            "lat": 37.4981,
                            "lng": 127.0278
                        },
                        "phone": "02-1234-5680",
                        "recommend_reason": "재미있는 영화관입니다",
                        "estimated_cost": 20000,
                        "estimated_duration": 120,
                        "recommended_time": "20:00-22:00"
                    }
                ],
                "summary": "로맨틱한 데이트 코스입니다"
            }
            """.trimIndent()
    }
}
