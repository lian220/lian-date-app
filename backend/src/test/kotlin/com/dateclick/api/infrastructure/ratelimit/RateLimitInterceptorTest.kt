package com.dateclick.api.infrastructure.ratelimit

import com.dateclick.api.presentation.rest.course.CreateCourseRequest
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
class RateLimitInterceptorTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `X-Session-Id가 없으면 IP 기반으로 Rate Limit을 적용한다`() {
        val request =
            CreateCourseRequest(
                regionId = "1",
                dateType = "DINING",
                budget = "50000",
            )

        // X-Session-Id 없이 10번 요청 (IP 기반 Rate Limit 적용)
        repeat(10) {
            mockMvc.perform(
                post("/v1/courses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)),
            )
        }

        // 11번째 요청은 IP 기반 Rate Limit으로 429 응답
        val result =
            mockMvc.perform(
                post("/v1/courses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)),
            )
                .andReturn()

        // IP 기반 Rate Limit이 적용되어 429여야 함
        assertEquals(429, result.response.status, "IP-based rate limit should apply without X-Session-Id")
    }

    @Test
    fun `POST courses는 10회 초과 시 429 응답을 반환한다`() {
        val sessionId = "test-session-${System.currentTimeMillis()}"
        val request =
            CreateCourseRequest(
                regionId = "1",
                dateType = "DINING",
                budget = "50000",
            )

        // 10회까지는 성공 (또는 다른 이유로 실패하지만 429는 아님)
        repeat(10) {
            mockMvc.perform(
                post("/v1/courses")
                    .header("X-Session-Id", sessionId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)),
            )
        }

        // 11회째는 429 응답
        mockMvc.perform(
            post("/v1/courses")
                .header("X-Session-Id", sessionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)),
        )
            .andExpect(status().isTooManyRequests)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error.code").value("RATE_LIMIT_EXCEEDED"))
            .andExpect(header().exists("Retry-After"))
    }

    @Test
    fun `서로 다른 세션 ID는 독립적으로 Rate Limit이 적용된다`() {
        val sessionId1 = "session-1-${System.currentTimeMillis()}"
        val sessionId2 = "session-2-${System.currentTimeMillis()}"
        val request =
            CreateCourseRequest(
                regionId = "1",
                dateType = "DINING",
                budget = "50000",
            )

        // session-1에서 10회 요청
        repeat(10) {
            mockMvc.perform(
                post("/v1/courses")
                    .header("X-Session-Id", sessionId1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)),
            )
        }

        // session-2는 여전히 가능 (429가 아닌 다른 상태)
        val result =
            mockMvc.perform(
                post("/v1/courses")
                    .header("X-Session-Id", sessionId2)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)),
            )
                .andReturn()

        // 429가 아니어야 함
        assertNotEquals(429, result.response.status, "Session-2 should not be rate limited")
    }
}
