package com.dateclick.api.domain.place.vo

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.DayOfWeek
import java.time.LocalTime

class BusinessHoursTest {
    @Test
    fun `정상적인 영업시간 생성`() {
        val businessHours =
            BusinessHours(
                day = "월",
                open = "09:00",
                close = "18:00",
            )

        assertFalse(businessHours.isClosed)
        assertEquals("월", businessHours.day)
        assertEquals("09:00", businessHours.open)
        assertEquals("18:00", businessHours.close)
    }

    @Test
    fun `휴무일 생성 - open과 close 모두 null`() {
        val businessHours =
            BusinessHours(
                day = "일",
                open = null,
                close = null,
            )

        assertTrue(businessHours.isClosed)
    }

    @Test
    fun `open만 null인 경우 예외 발생`() {
        assertThrows<IllegalArgumentException> {
            BusinessHours(
                day = "월",
                open = null,
                close = "18:00",
            )
        }
    }

    @Test
    fun `close만 null인 경우 예외 발생`() {
        assertThrows<IllegalArgumentException> {
            BusinessHours(
                day = "월",
                open = "09:00",
                close = null,
            )
        }
    }

    @Test
    fun `현재 영업 중 판단 - 영업 시간 내`() {
        val businessHours =
            BusinessHours(
                day = "월",
                open = "09:00",
                close = "18:00",
            )

        // 오전 10시 - 영업 중
        assertTrue(businessHours.isOpenAt(LocalTime.of(10, 0)))

        // 정오 - 영업 중
        assertTrue(businessHours.isOpenAt(LocalTime.of(12, 0)))

        // 오후 5시 - 영업 중
        assertTrue(businessHours.isOpenAt(LocalTime.of(17, 0)))
    }

    @Test
    fun `현재 영업 중 판단 - 영업 시간 외`() {
        val businessHours =
            BusinessHours(
                day = "월",
                open = "09:00",
                close = "18:00",
            )

        // 오전 8시 - 영업 전
        assertFalse(businessHours.isOpenAt(LocalTime.of(8, 0)))

        // 오후 6시 - 영업 종료
        assertFalse(businessHours.isOpenAt(LocalTime.of(18, 0)))

        // 오후 7시 - 영업 종료
        assertFalse(businessHours.isOpenAt(LocalTime.of(19, 0)))
    }

    @Test
    fun `휴무일은 항상 영업 안 함`() {
        val businessHours =
            BusinessHours(
                day = "일",
                open = null,
                close = null,
            )

        assertFalse(businessHours.isOpenAt(LocalTime.of(12, 0)))
        assertFalse(businessHours.isOpenAt(LocalTime.of(15, 0)))
    }

    @Test
    fun `자정 넘어가는 영업시간 - 심야 영업`() {
        val businessHours =
            BusinessHours(
                day = "금",
                open = "18:00",
                close = "02:00", // 다음날 새벽 2시
            )

        // 오후 10시 - 영업 중
        assertTrue(businessHours.isOpenAt(LocalTime.of(22, 0)))

        // 자정 - 영업 중
        assertTrue(businessHours.isOpenAt(LocalTime.of(0, 0)))

        // 새벽 1시 - 영업 중
        assertTrue(businessHours.isOpenAt(LocalTime.of(1, 0)))

        // 새벽 3시 - 영업 종료
        assertFalse(businessHours.isOpenAt(LocalTime.of(3, 0)))

        // 오후 5시 - 영업 전
        assertFalse(businessHours.isOpenAt(LocalTime.of(17, 0)))
    }

    @Test
    fun `24시간 영업`() {
        val businessHours =
            BusinessHours(
                day = "월",
                open = "00:00",
                close = "24:00",
            )

        // 모든 시간에 영업 중
        assertTrue(businessHours.isOpenAt(LocalTime.of(0, 0)))
        assertTrue(businessHours.isOpenAt(LocalTime.of(6, 0)))
        assertTrue(businessHours.isOpenAt(LocalTime.of(12, 0)))
        assertTrue(businessHours.isOpenAt(LocalTime.of(18, 0)))
        assertTrue(businessHours.isOpenAt(LocalTime.of(23, 59)))
    }

    @Test
    fun `경계값 테스트 - 오픈 시간 정확히`() {
        val businessHours =
            BusinessHours(
                day = "월",
                open = "09:00",
                close = "18:00",
            )

        // 오픈 시간 정확히 - 영업 중
        assertTrue(businessHours.isOpenAt(LocalTime.of(9, 0)))
    }

    @Test
    fun `경계값 테스트 - 클로즈 시간 정확히`() {
        val businessHours =
            BusinessHours(
                day = "월",
                open = "09:00",
                close = "18:00",
            )

        // 클로즈 시간 정확히 - 영업 종료 (close 시간은 제외)
        assertFalse(businessHours.isOpenAt(LocalTime.of(18, 0)))
    }

    @Test
    fun `요일을 DayOfWeek로 변환`() {
        assertEquals(DayOfWeek.MONDAY, BusinessHours.parseDayOfWeek("월"))
        assertEquals(DayOfWeek.TUESDAY, BusinessHours.parseDayOfWeek("화"))
        assertEquals(DayOfWeek.WEDNESDAY, BusinessHours.parseDayOfWeek("수"))
        assertEquals(DayOfWeek.THURSDAY, BusinessHours.parseDayOfWeek("목"))
        assertEquals(DayOfWeek.FRIDAY, BusinessHours.parseDayOfWeek("금"))
        assertEquals(DayOfWeek.SATURDAY, BusinessHours.parseDayOfWeek("토"))
        assertEquals(DayOfWeek.SUNDAY, BusinessHours.parseDayOfWeek("일"))
    }

    @Test
    fun `잘못된 요일 문자열은 예외 발생`() {
        assertThrows<IllegalArgumentException> {
            BusinessHours.parseDayOfWeek("월요일")
        }

        assertThrows<IllegalArgumentException> {
            BusinessHours.parseDayOfWeek("MON")
        }
    }

    @Test
    fun `시간 문자열을 LocalTime으로 변환`() {
        assertEquals(LocalTime.of(9, 0), BusinessHours.parseTime("09:00"))
        assertEquals(LocalTime.of(18, 30), BusinessHours.parseTime("18:30"))
        assertEquals(LocalTime.of(0, 0), BusinessHours.parseTime("00:00"))
    }

    @Test
    fun `24시를 특별 처리`() {
        // "24:00"은 다음날 00:00을 의미하지만, 영업시간 맥락에서는 자정까지로 해석
        val time = BusinessHours.parseTime("24:00")
        assertEquals(LocalTime.MAX, time) // 23:59:59.999999999
    }
}
