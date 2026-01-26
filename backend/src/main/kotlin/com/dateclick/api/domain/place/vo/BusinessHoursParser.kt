package com.dateclick.api.domain.place.vo

import org.slf4j.LoggerFactory

/**
 * 영업시간 문자열 파싱 유틸리티
 *
 * 다양한 형식의 영업시간 문자열을 BusinessHours 리스트로 변환합니다.
 *
 * 지원 형식:
 * - "월~금 09:00~18:00"
 * - "매일 10:00~23:00"
 * - "월,수,금 09:00~18:00"
 * - "휴무"
 * - "영업시간 정보 없음"
 */
object BusinessHoursParser {

    private val logger = LoggerFactory.getLogger(javaClass)

    // 한글 요일 목록
    private val ALL_DAYS = listOf("월", "화", "수", "목", "금", "토", "일")

    /**
     * 영업시간 문자열을 파싱하여 BusinessHours 리스트로 변환
     *
     * @param input 영업시간 문자열 (예: "월~금 09:00~18:00")
     * @return BusinessHours 리스트
     */
    fun parse(input: String?): List<BusinessHours> {
        if (input.isNullOrBlank()) {
            logger.debug("Empty business hours input")
            return emptyList()
        }

        val trimmed = input.trim()

        // 휴무 또는 정보 없음
        if (isClosedPattern(trimmed)) {
            logger.debug("Closed pattern detected: {}", trimmed)
            return emptyList()
        }

        return try {
            parsePattern(trimmed)
        } catch (e: Exception) {
            logger.warn("Failed to parse business hours: {}", trimmed, e)
            emptyList()
        }
    }

    /**
     * 휴무 패턴 확인
     */
    private fun isClosedPattern(input: String): Boolean {
        val closedKeywords = listOf("휴무", "영업시간 정보 없음", "정보 없음", "미운영")
        return closedKeywords.any { input.contains(it) }
    }

    /**
     * 영업시간 패턴 파싱
     */
    private fun parsePattern(input: String): List<BusinessHours> {
        // 패턴: "요일 시간~시간" 형식
        // 예: "월~금 09:00~18:00", "매일 10:00~23:00", "월,수,금 09:00~18:00"

        // 1. "매일" 패턴 처리
        if (input.startsWith("매일")) {
            return parseEveryDay(input)
        }

        // 2. "월~금" 또는 "월,수,금" 패턴 처리
        return parseSpecificDays(input)
    }

    /**
     * "매일" 패턴 파싱
     * 예: "매일 10:00~23:00"
     */
    private fun parseEveryDay(input: String): List<BusinessHours> {
        val timeRange = extractTimeRange(input)
        if (timeRange == null) {
            logger.warn("Failed to extract time range from: {}", input)
            return emptyList()
        }

        return ALL_DAYS.map { day ->
            BusinessHours(
                day = day,
                open = timeRange.first,
                close = timeRange.second
            )
        }
    }

    /**
     * 특정 요일 패턴 파싱
     * 예: "월~금 09:00~18:00", "월,수,금 09:00~18:00"
     */
    private fun parseSpecificDays(input: String): List<BusinessHours> {
        // 요일 부분과 시간 부분 분리
        val parts = input.split(" ", limit = 2)
        if (parts.size < 2) {
            logger.warn("Invalid format, missing time part: {}", input)
            return emptyList()
        }

        val dayPart = parts[0]
        val timePart = parts[1]

        // 요일 파싱
        val days = parseDays(dayPart)
        if (days.isEmpty()) {
            logger.warn("Failed to parse days from: {}", dayPart)
            return emptyList()
        }

        // 시간 파싱
        val timeRange = extractTimeRange(timePart)
        if (timeRange == null) {
            logger.warn("Failed to extract time range from: {}", timePart)
            return emptyList()
        }

        // 해당 요일들에 대해 BusinessHours 생성
        return days.map { day ->
            BusinessHours(
                day = day,
                open = timeRange.first,
                close = timeRange.second
            )
        }
    }

    /**
     * 요일 문자열 파싱
     *
     * 지원 형식:
     * - "월~금" → [월, 화, 수, 목, 금]
     * - "월,수,금" → [월, 수, 금]
     * - "월" → [월]
     */
    private fun parseDays(dayPart: String): List<String> {
        return when {
            // 범위 패턴: "월~금"
            dayPart.contains("~") -> parseDayRange(dayPart)
            // 콤마 구분 패턴: "월,수,금"
            dayPart.contains(",") -> dayPart.split(",").map { it.trim() }
            // 단일 요일: "월"
            dayPart.length == 1 && ALL_DAYS.contains(dayPart) -> listOf(dayPart)
            else -> {
                logger.warn("Unknown day pattern: {}", dayPart)
                emptyList()
            }
        }
    }

    /**
     * 요일 범위 파싱
     * 예: "월~금" → [월, 화, 수, 목, 금]
     */
    private fun parseDayRange(dayRange: String): List<String> {
        val parts = dayRange.split("~")
        if (parts.size != 2) {
            return emptyList()
        }

        val startDay = parts[0].trim()
        val endDay = parts[1].trim()

        val startIndex = ALL_DAYS.indexOf(startDay)
        val endIndex = ALL_DAYS.indexOf(endDay)

        if (startIndex == -1 || endIndex == -1) {
            logger.warn("Invalid day range: {} ~ {}", startDay, endDay)
            return emptyList()
        }

        // 시작 요일부터 종료 요일까지
        return if (startIndex <= endIndex) {
            ALL_DAYS.subList(startIndex, endIndex + 1)
        } else {
            // 예외: "토~월" 같은 경우는 지원하지 않음
            logger.warn("Invalid day range: start > end ({} > {})", startDay, endDay)
            emptyList()
        }
    }

    /**
     * 시간 범위 추출
     * 예: "09:00~18:00" → Pair("09:00", "18:00")
     */
    private fun extractTimeRange(input: String): Pair<String, String>? {
        // 패턴: "HH:mm~HH:mm" 또는 "HH:mm ~ HH:mm"
        val timePattern = Regex("""(\d{2}:\d{2})\s*[~-]\s*(\d{2}:\d{2})""")
        val match = timePattern.find(input)

        if (match != null) {
            val open = match.groupValues[1]
            val close = match.groupValues[2]
            return Pair(open, close)
        }

        return null
    }

    /**
     * 여러 줄의 영업시간 문자열을 파싱
     *
     * 예:
     * ```
     * parseMultiLine("""
     *   월~금 09:00~18:00
     *   토 10:00~15:00
     *   일 휴무
     * """)
     * ```
     */
    fun parseMultiLine(input: String?): List<BusinessHours> {
        if (input.isNullOrBlank()) {
            return emptyList()
        }

        return input.lines()
            .flatMap { line -> parse(line.trim()) }
            .groupBy { it.day }
            .mapValues { (_, hours) -> hours.last() } // 중복 요일 제거 (마지막 것 우선)
            .values
            .toList()
    }
}
