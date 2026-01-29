package com.dateclick.api.domain.course.entity

data class Route(
    val from: Int,
    val to: Int,
    val distance: Int, // meters
    val duration: Int, // minutes
    val transportType: TransportType,
    val description: String,
) {
    companion object {
        const val MAX_DURATION_MINUTES = 30
    }

    /**
     * AC 2.4: 장소 간 이동 거리 30분 이내 검증
     */
    fun isWithinTimeLimit(): Boolean = duration <= MAX_DURATION_MINUTES

    /**
     * 30분 초과 여부 확인
     */
    fun exceedsTimeLimit(): Boolean = !isWithinTimeLimit()

    /**
     * 30분 초과 시 예외 발생
     */
    fun validateTimeLimit() {
        require(isWithinTimeLimit()) {
            "이동 시간이 허용 범위를 초과합니다: ${duration}분 (최대 ${MAX_DURATION_MINUTES}분)"
        }
    }
}

enum class TransportType(val code: String) {
    WALK("walk"),
    TRANSIT("transit"),
    CAR("car"),
    ;

    companion object {
        fun fromCode(code: String): TransportType {
            return values().find { it.code.equals(code, ignoreCase = true) }
                ?: throw IllegalArgumentException("Unknown transport type code: $code")
        }
    }
}
