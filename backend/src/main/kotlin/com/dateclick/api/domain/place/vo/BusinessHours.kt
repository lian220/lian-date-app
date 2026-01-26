package com.dateclick.api.domain.place.vo

import java.time.DayOfWeek
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * 영업시간 Value Object
 *
 * @property day 요일 (한글: 월, 화, 수, 목, 금, 토, 일)
 * @property open 오픈 시간 (HH:mm 형식, 예: "09:00")
 * @property close 마감 시간 (HH:mm 형식, 예: "18:00")
 */
data class BusinessHours(
    val day: String,
    val open: String?,
    val close: String?,
) {
    init {
        require(isClosed || (open != null && close != null)) {
            "BusinessHours는 open/close가 둘 다 null(isClosed) 이거나 둘 다 non-null이어야 합니다. 현재 open=$open, close=$close"
        }
    }

    val isClosed: Boolean
        get() = open == null && close == null

    /**
     * 특정 시간에 영업 중인지 판단
     *
     * @param time 확인할 시간
     * @return 영업 중이면 true, 아니면 false
     */
    fun isOpenAt(time: LocalTime): Boolean {
        if (isClosed) return false

        val openTime = parseTime(open!!)
        val closeTime = parseTime(close!!)

        // 24시간 영업 (00:00 ~ 24:00 또는 00:00 ~ 23:59:59.999...)
        if (openTime == LocalTime.MIN && (closeTime == LocalTime.MAX || closeTime == LocalTime.MIDNIGHT)) {
            return true
        }

        // 자정을 넘어가는 영업시간 (예: 18:00 ~ 02:00)
        return if (closeTime <= openTime) {
            // 자정 이전 시간대 또는 자정 이후 시간대
            time >= openTime || time < closeTime
        } else {
            // 일반적인 영업시간 (예: 09:00 ~ 18:00)
            time >= openTime && time < closeTime
        }
    }

    /**
     * 현재 시간 기준으로 영업 중인지 판단 (한국 표준시 기준)
     *
     * @return 영업 중이면 true, 아니면 false
     */
    fun isOpenNow(): Boolean {
        val now = ZonedDateTime.now(KOREA_ZONE_ID)
        val currentDay = toDayString(now.dayOfWeek)

        // 현재 요일이 아니면 영업 안 함
        if (day != currentDay) return false

        return isOpenAt(now.toLocalTime())
    }

    companion object {
        private val KOREA_ZONE_ID = ZoneId.of("Asia/Seoul")

        private val DAY_MAP =
            mapOf(
                "월" to DayOfWeek.MONDAY,
                "화" to DayOfWeek.TUESDAY,
                "수" to DayOfWeek.WEDNESDAY,
                "목" to DayOfWeek.THURSDAY,
                "금" to DayOfWeek.FRIDAY,
                "토" to DayOfWeek.SATURDAY,
                "일" to DayOfWeek.SUNDAY,
            )

        private val DAY_REVERSE_MAP = DAY_MAP.entries.associate { (k, v) -> v to k }

        /**
         * 한글 요일 문자열을 DayOfWeek로 변환
         *
         * @param day 요일 문자열 (월, 화, 수, 목, 금, 토, 일)
         * @return DayOfWeek
         * @throws IllegalArgumentException 잘못된 요일 문자열인 경우
         */
        fun parseDayOfWeek(day: String): DayOfWeek {
            return DAY_MAP[day] ?: throw IllegalArgumentException("Invalid day string: $day")
        }

        /**
         * DayOfWeek를 한글 요일 문자열로 변환
         *
         * @param dayOfWeek DayOfWeek
         * @return 한글 요일 문자열 (월, 화, 수, 목, 금, 토, 일)
         */
        fun toDayString(dayOfWeek: DayOfWeek): String {
            return DAY_REVERSE_MAP[dayOfWeek] ?: throw IllegalArgumentException("Invalid DayOfWeek: $dayOfWeek")
        }

        /**
         * 시간 문자열을 LocalTime으로 변환
         *
         * @param time 시간 문자열 (HH:mm 형식, 예: "09:00", "24:00")
         * @return LocalTime
         */
        fun parseTime(time: String): LocalTime {
            // "24:00"은 자정까지 영업을 의미 (LocalTime.MAX로 처리)
            if (time == "24:00") {
                return LocalTime.MAX // 23:59:59.999999999
            }

            return LocalTime.parse(time)
        }
    }
}
