package com.dateclick.api.infrastructure.external.openai

import com.dateclick.api.domain.course.vo.Budget
import com.dateclick.api.domain.course.vo.DateType
import com.dateclick.api.domain.place.entity.Place
import com.dateclick.api.domain.region.entity.Region

/**
 * GPT-4 프롬프트 빌더
 * 데이트 코스 추천을 위한 프롬프트 생성
 */
object PromptBuilder {

    /**
     * 시스템 프롬프트 생성
     * AI의 역할과 응답 형식을 정의
     */
    fun buildSystemPrompt(): String {
        return """
            당신은 한국의 데이트 코스 추천 전문가입니다.
            사용자의 요구사항(지역, 데이트 유형, 예산)과 후보 장소 목록을 기반으로 최적의 데이트 코스를 추천해주세요.

            응답은 반드시 다음 JSON 형식으로 작성해야 합니다:
            {
              "places": [
                {
                  "place_id": "장소 ID (후보 목록의 ID 사용)",
                  "order": 1,
                  "recommend_reason": "추천 이유 (한글, 50자 이내)",
                  "estimated_cost": 15000,
                  "estimated_duration": 60,
                  "recommended_time": "14:00-15:00"
                }
              ],
              "summary": "전체 코스 요약 (한글, 100자 이내)"
            }

            주의사항:
            - places는 정확히 3개를 선택해야 합니다.
            - order는 1, 2, 3 순서로 방문 순서를 의미합니다.
            - place_id는 제공된 후보 목록의 ID만 사용해야 합니다.
            - estimated_cost는 1인 기준 예상 비용(원)입니다.
            - estimated_duration은 예상 소요 시간(분)입니다.
            - recommended_time은 방문 추천 시간대입니다.
        """.trimIndent()
    }

    /**
     * 사용자 프롬프트 생성
     * 구체적인 요구사항과 후보 장소 목록 포함
     */
    fun buildUserPrompt(
        region: Region,
        dateType: DateType,
        budget: Budget,
        specialRequest: String?,
        candidatePlaces: List<Place>
    ): String {
        val dateTypeDescription = when (dateType) {
            DateType.ROMANTIC -> "로맨틱한 데이트 (분위기 있고 특별한 경험)"
            DateType.ACTIVITY -> "액티브한 데이트 (활동적이고 에너지 넘치는)"
            DateType.FOOD -> "맛집 탐방 데이트 (미식 경험 중심)"
            DateType.CULTURE -> "문화적인 데이트 (전시, 공연, 문화 체험)"
            DateType.HEALING -> "힐링 데이트 (휴식과 재충전)"
        }

        val budgetDescription = "예산: ${budget.toDisplayName()}"

        val placesDescription = candidatePlaces.joinToString("\n") { place ->
            """
            - ID: ${place.id.value}
              이름: ${place.name}
              카테고리: ${place.category}
              주소: ${place.roadAddress ?: place.address}
              ${place.kakaoRating?.let { "평점: $it (${place.kakaoReviewCount}개 리뷰)" } ?: ""}
            """.trimIndent()
        }

        return """
            다음 조건으로 데이트 코스를 추천해주세요:

            [지역]
            ${region.name} (${region.description})

            [데이트 유형]
            $dateTypeDescription

            [예산]
            $budgetDescription

            ${specialRequest?.let { "[특별 요청]\n$it\n" } ?: ""}
            [후보 장소 목록]
            $placesDescription

            위 후보 장소들 중에서 데이트 유형과 예산에 가장 적합한 3곳을 선택하여 추천해주세요.
            일반적인 데이트 흐름(카페/브런치 → 메인 활동 → 식사)을 고려하여 순서를 정해주세요.
        """.trimIndent()
    }

    /**
     * ChatCompletion 요청 메시지 생성
     */
    fun buildMessages(
        region: Region,
        dateType: DateType,
        budget: Budget,
        specialRequest: String?,
        candidatePlaces: List<Place>
    ): List<Message> {
        return listOf(
            Message.system(buildSystemPrompt()),
            Message.user(
                buildUserPrompt(
                    region = region,
                    dateType = dateType,
                    budget = budget,
                    specialRequest = specialRequest,
                    candidatePlaces = candidatePlaces
                )
            )
        )
    }
}
