package com.dateclick.api.infrastructure.ratelimit

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class RateLimitServiceTest {
    private lateinit var rateLimitService: RateLimitService

    @BeforeEach
    fun setUp() {
        rateLimitService = RateLimitService()
    }

    @Test
    fun `POST courses는 분당 10회 제한이 적용된다`() {
        val sessionId = "test-session-123"

        // 10회까지는 성공
        repeat(10) {
            rateLimitService.checkRateLimit(sessionId, "POST", "/v1/courses")
        }

        // 11회째는 실패
        val exception =
            assertThrows<RateLimitException> {
                rateLimitService.checkRateLimit(sessionId, "POST", "/v1/courses")
            }

        assertEquals("POST:/v1/courses", exception.endpoint)
        assertEquals(10, exception.limit)
        assertEquals(60, exception.windowSeconds)
    }

    @Test
    fun `POST courses regenerate는 분당 5회 제한이 적용된다`() {
        val sessionId = "test-session-456"

        // 5회까지는 성공
        repeat(5) {
            rateLimitService.checkRateLimit(sessionId, "POST", "/v1/courses/abc-123/regenerate")
        }

        // 6회째는 실패
        val exception =
            assertThrows<RateLimitException> {
                rateLimitService.checkRateLimit(sessionId, "POST", "/v1/courses/abc-123/regenerate")
            }

        assertEquals("POST:/v1/courses/*/regenerate", exception.endpoint)
        assertEquals(5, exception.limit)
    }

    @Test
    fun `기타 엔드포인트는 분당 100회 제한이 적용된다`() {
        val sessionId = "test-session-789"

        // 100회까지는 성공
        repeat(100) {
            rateLimitService.checkRateLimit(sessionId, "GET", "/v1/places/search")
        }

        // 101회째는 실패
        val exception =
            assertThrows<RateLimitException> {
                rateLimitService.checkRateLimit(sessionId, "GET", "/v1/places/search")
            }

        assertEquals(100, exception.limit)
    }

    @Test
    fun `서로 다른 세션 ID는 독립적으로 제한된다`() {
        val sessionId1 = "session-1"
        val sessionId2 = "session-2"

        // session-1에서 10회 요청
        repeat(10) {
            rateLimitService.checkRateLimit(sessionId1, "POST", "/v1/courses")
        }

        // session-2는 여전히 가능
        rateLimitService.checkRateLimit(sessionId2, "POST", "/v1/courses")

        // session-1은 제한
        assertThrows<RateLimitException> {
            rateLimitService.checkRateLimit(sessionId1, "POST", "/v1/courses")
        }
    }

    @Test
    fun `서로 다른 엔드포인트는 독립적으로 제한된다`() {
        val sessionId = "test-session"

        // POST /v1/courses 10회
        repeat(10) {
            rateLimitService.checkRateLimit(sessionId, "POST", "/v1/courses")
        }

        // POST /v1/courses/{id}/regenerate는 여전히 가능 (다른 규칙)
        rateLimitService.checkRateLimit(sessionId, "POST", "/v1/courses/123/regenerate")

        // GET /v1/places도 여전히 가능 (다른 규칙)
        rateLimitService.checkRateLimit(sessionId, "GET", "/v1/places/search")
    }

    @Test
    fun `Path Variable이 있는 경로는 정규화된다`() {
        val sessionId = "test-session"

        // 다른 courseId로 5회 요청
        rateLimitService.checkRateLimit(sessionId, "POST", "/v1/courses/123/regenerate")
        rateLimitService.checkRateLimit(sessionId, "POST", "/v1/courses/456/regenerate")
        rateLimitService.checkRateLimit(sessionId, "POST", "/v1/courses/789/regenerate")
        rateLimitService.checkRateLimit(sessionId, "POST", "/v1/courses/abc/regenerate")
        rateLimitService.checkRateLimit(sessionId, "POST", "/v1/courses/def/regenerate")

        // 6회째는 실패 (모두 같은 엔드포인트로 카운트됨)
        assertThrows<RateLimitException> {
            rateLimitService.checkRateLimit(sessionId, "POST", "/v1/courses/ghi/regenerate")
        }
    }
}
