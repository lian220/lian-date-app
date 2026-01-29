package com.dateclick.api.domain.place.vo

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class BusinessHoursParserTest {
    @Test
    fun `null 또는 빈 문자열은 빈 리스트 반환`() {
        assertTrue(BusinessHoursParser.parse(null).isEmpty())
        assertTrue(BusinessHoursParser.parse("").isEmpty())
        assertTrue(BusinessHoursParser.parse("   ").isEmpty())
    }

    @Test
    fun `휴무 패턴은 빈 리스트 반환`() {
        assertTrue(BusinessHoursParser.parse("휴무").isEmpty())
        assertTrue(BusinessHoursParser.parse("영업시간 정보 없음").isEmpty())
        assertTrue(BusinessHoursParser.parse("정보 없음").isEmpty())
    }

    @Test
    fun `매일 패턴 파싱`() {
        val result = BusinessHoursParser.parse("매일 10:00~23:00")

        assertEquals(7, result.size)
        result.forEach { businessHours ->
            assertEquals("10:00", businessHours.open)
            assertEquals("23:00", businessHours.close)
            assertFalse(businessHours.isClosed)
        }
    }

    @Test
    fun `요일 범위 패턴 파싱 - 월~금`() {
        val result = BusinessHoursParser.parse("월~금 09:00~18:00")

        assertEquals(5, result.size)
        assertEquals(listOf("월", "화", "수", "목", "금"), result.map { it.day })
        result.forEach { businessHours ->
            assertEquals("09:00", businessHours.open)
            assertEquals("18:00", businessHours.close)
        }
    }

    @Test
    fun `요일 범위 패턴 파싱 - 토~일`() {
        val result = BusinessHoursParser.parse("토~일 10:00~20:00")

        assertEquals(2, result.size)
        assertEquals(listOf("토", "일"), result.map { it.day })
        result.forEach { businessHours ->
            assertEquals("10:00", businessHours.open)
            assertEquals("20:00", businessHours.close)
        }
    }

    @Test
    fun `콤마 구분 요일 파싱`() {
        val result = BusinessHoursParser.parse("월,수,금 09:00~18:00")

        assertEquals(3, result.size)
        assertEquals(listOf("월", "수", "금"), result.map { it.day })
        result.forEach { businessHours ->
            assertEquals("09:00", businessHours.open)
            assertEquals("18:00", businessHours.close)
        }
    }

    @Test
    fun `단일 요일 파싱`() {
        val result = BusinessHoursParser.parse("월 09:00~18:00")

        assertEquals(1, result.size)
        assertEquals("월", result[0].day)
        assertEquals("09:00", result[0].open)
        assertEquals("18:00", result[0].close)
    }

    @Test
    fun `시간 구분자 다양한 형식 지원 - 틴더 ~`() {
        val result = BusinessHoursParser.parse("월~금 09:00~18:00")
        assertEquals(5, result.size)
    }

    @Test
    fun `시간 구분자 다양한 형식 지원 - 대시 -`() {
        val result = BusinessHoursParser.parse("월~금 09:00-18:00")
        assertEquals(5, result.size)
    }

    @Test
    fun `공백이 있는 시간 형식`() {
        val result = BusinessHoursParser.parse("월~금 09:00 ~ 18:00")
        assertEquals(5, result.size)
        assertEquals("09:00", result[0].open)
        assertEquals("18:00", result[0].close)
    }

    @Test
    fun `자정 넘어가는 영업시간 파싱`() {
        val result = BusinessHoursParser.parse("금 18:00~02:00")

        assertEquals(1, result.size)
        assertEquals("금", result[0].day)
        assertEquals("18:00", result[0].open)
        assertEquals("02:00", result[0].close)
    }

    @Test
    fun `24시간 영업 파싱`() {
        val result = BusinessHoursParser.parse("매일 00:00~24:00")

        assertEquals(7, result.size)
        result.forEach { businessHours ->
            assertEquals("00:00", businessHours.open)
            assertEquals("24:00", businessHours.close)
        }
    }

    @Test
    fun `잘못된 형식은 빈 리스트 반환`() {
        assertTrue(BusinessHoursParser.parse("Invalid format").isEmpty())
        assertTrue(BusinessHoursParser.parse("월~금").isEmpty()) // 시간 없음
        assertTrue(BusinessHoursParser.parse("09:00~18:00").isEmpty()) // 요일 없음
    }

    @Test
    fun `여러 줄 파싱 - 요일별 다른 영업시간`() {
        val input =
            """
            월~금 09:00~18:00
            토 10:00~15:00
            일 휴무
            """.trimIndent()

        val result = BusinessHoursParser.parseMultiLine(input)

        // 월~금 (5개) + 토 (1개) = 6개 (일은 휴무이므로 제외)
        assertEquals(6, result.size)

        // 평일 확인
        val weekdays = result.filter { it.day in listOf("월", "화", "수", "목", "금") }
        assertEquals(5, weekdays.size)
        weekdays.forEach {
            assertEquals("09:00", it.open)
            assertEquals("18:00", it.close)
        }

        // 토요일 확인
        val saturday = result.find { it.day == "토" }
        assertNotNull(saturday)
        assertEquals("10:00", saturday!!.open)
        assertEquals("15:00", saturday.close)

        // 일요일은 없어야 함 (휴무)
        val sunday = result.find { it.day == "일" }
        assertNull(sunday)
    }

    @Test
    fun `중복 요일은 마지막 것만 유지`() {
        val input =
            """
            월 09:00~18:00
            월 10:00~20:00
            """.trimIndent()

        val result = BusinessHoursParser.parseMultiLine(input)

        assertEquals(1, result.size)
        assertEquals("10:00", result[0].open)
        assertEquals("20:00", result[0].close)
    }

    @Test
    fun `실제 카카오 맵 형식 예시 - 1`() {
        val result = BusinessHoursParser.parse("매일 11:00 ~ 22:00")

        assertEquals(7, result.size)
        result.forEach {
            assertEquals("11:00", it.open)
            assertEquals("22:00", it.close)
        }
    }

    @Test
    fun `실제 카카오 맵 형식 예시 - 2`() {
        val input =
            """
            월~일 11:00 ~ 22:00
            """.trimIndent()

        val result = BusinessHoursParser.parse(input)

        assertEquals(7, result.size)
    }

    @Test
    fun `실제 카카오 맵 형식 예시 - 3 심야 영업`() {
        val result = BusinessHoursParser.parse("금~토 18:00~02:00")

        assertEquals(2, result.size)
        assertEquals(listOf("금", "토"), result.map { it.day })
    }
}
