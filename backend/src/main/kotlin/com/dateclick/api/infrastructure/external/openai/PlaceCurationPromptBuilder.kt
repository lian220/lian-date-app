package com.dateclick.api.infrastructure.external.openai

import com.dateclick.api.domain.place.entity.Place

/**
 * 장소 큐레이션용 GPT-4 프롬프트 빌더
 */
object PlaceCurationPromptBuilder {

    /**
     * 시스템 프롬프트 생성
     * AI의 역할과 응답 형식을 정의
     */
    fun buildSystemPrompt(): String {
        return """
            당신은 한국의 데이트 장소 평가 전문가입니다.
            장소의 이름, 카테고리, 위치 정보를 기반으로 해당 장소가 데이트하기 좋은지 분석하고,
            데이트 적합도와 메타데이터를 제공해주세요.

            응답은 반드시 다음 JSON 형식으로 작성해야 합니다:
            {
              "date_score": 8,
              "mood_tags": ["#로맨틱", "#아늑한", "#트렌디"],
              "price_range": 25000,
              "best_time": "저녁 7-9시",
              "recommendation": "분위기 좋은 루프탑에서 야경을 즐기며 데이트하기 좋은 곳"
            }

            필드 설명:
            - date_score: 1-10점 (데이트 적합도, 10점이 최고)
            - mood_tags: 최대 3개의 해시태그 (분위기, 특징)
            - price_range: 1인당 예상 가격대 (원 단위)
            - best_time: 추천 방문 시간대
            - recommendation: 한 줄 추천 이유 (50자 이내)

            주의사항:
            - 장소 이름과 카테고리만으로 추론해야 합니다
            - 실제 데이터를 모르더라도 일반적인 패턴을 기반으로 합리적으로 추정하세요
            - mood_tags는 반드시 #으로 시작해야 합니다
            - 한국어로 작성하세요
        """.trimIndent()
    }

    /**
     * 사용자 프롬프트 생성
     */
    fun buildUserPrompt(place: Place): String {
        return """
            다음 장소를 데이트 장소로 평가해주세요:

            [장소 정보]
            이름: ${place.name}
            카테고리: ${place.category}
            세부 카테고리: ${place.categoryDetail ?: "정보 없음"}
            주소: ${place.address}
            위치: 위도 ${place.location.lat}, 경도 ${place.location.lng}

            위 정보를 기반으로 이 장소의 데이트 적합도, 분위기 태그, 예상 가격대, 추천 시간대, 추천 이유를 JSON 형식으로 제공해주세요.
        """.trimIndent()
    }

    /**
     * ChatCompletion 요청 메시지 생성
     */
    fun buildMessages(place: Place): List<Message> {
        return listOf(
            Message.system(buildSystemPrompt()),
            Message.user(buildUserPrompt(place))
        )
    }
}
