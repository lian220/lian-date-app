package com.dateclick.api.domain.rating.port

/**
 * 사용자 만족도 점수 전송 아웃바운드 포트
 * 애플리케이션 레이어가 인프라(Langfuse)에 직접 의존하지 않도록 추상화
 */
interface UserSatisfactionPort {
    /**
     * 사용자 만족도 점수 전송 (비동기, fire-and-forget)
     * @param score 1-5점 평가 점수
     * @param sessionId 사용자 세션 ID
     * @param comment 평가 코멘트 (선택)
     */
    fun sendUserSatisfactionScore(
        score: Int,
        sessionId: String,
        comment: String? = null,
    )
}
