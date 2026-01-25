package com.dateclick.api.domain.course.entity

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class RouteTest {

    @Test
    fun `should return true when route is within 30 minutes`() {
        // Given
        val route = Route(
            from = 0,
            to = 1,
            distance = 3000,
            duration = 20,
            transportType = TransportType.WALK,
            description = "도보 경로"
        )

        // When & Then
        assertTrue(route.isWithinTimeLimit())
        assertFalse(route.exceedsTimeLimit())
    }

    @Test
    fun `should return false when route exceeds 30 minutes`() {
        // Given
        val route = Route(
            from = 0,
            to = 1,
            distance = 15000,
            duration = 35,
            transportType = TransportType.CAR,
            description = "자동차 경로"
        )

        // When & Then
        assertFalse(route.isWithinTimeLimit())
        assertTrue(route.exceedsTimeLimit())
    }

    @Test
    fun `should return true when route is exactly 30 minutes`() {
        // Given
        val route = Route(
            from = 0,
            to = 1,
            distance = 10000,
            duration = 30,
            transportType = TransportType.TRANSIT,
            description = "대중교통 경로"
        )

        // When & Then
        assertTrue(route.isWithinTimeLimit())
        assertFalse(route.exceedsTimeLimit())
    }

    @Test
    fun `should pass validation when route is within time limit`() {
        // Given
        val route = Route(
            from = 0,
            to = 1,
            distance = 2000,
            duration = 15,
            transportType = TransportType.WALK,
            description = "도보 경로"
        )

        // When & Then
        assertDoesNotThrow { route.validateTimeLimit() }
    }

    @Test
    fun `should throw exception when route exceeds time limit`() {
        // Given
        val route = Route(
            from = 0,
            to = 1,
            distance = 20000,
            duration = 40,
            transportType = TransportType.CAR,
            description = "자동차 경로"
        )

        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            route.validateTimeLimit()
        }
        assertTrue(exception.message!!.contains("40분"))
        assertTrue(exception.message!!.contains("30분"))
    }

    @Test
    fun `should verify MAX_DURATION_MINUTES is 30`() {
        assertEquals(30, Route.MAX_DURATION_MINUTES)
    }
}
