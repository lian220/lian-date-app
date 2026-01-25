package com.dateclick.api.infrastructure.external.openai

import com.dateclick.api.domain.course.vo.Budget
import com.dateclick.api.domain.course.vo.DateType
import com.dateclick.api.domain.region.entity.Region

/**
 * GPT-4 프롬프트 빌더
 * 데이트 코스 추천을 위한 프롬프트 생성
 * MVP: AI가 장소 정보를 포함한 전체 코스를 생성
 */
object PromptBuilder {

    /**
     * 시스템 프롬프트 생성
     * AI의 역할과 응답 형식을 정의
     */
    fun buildSystemPrompt(): String {
        return """
            당신은 한국의 데이트 코스 추천 전문가입니다.
            사용자의 요구사항(지역, 데이트 유형, 예산)을 기반으로 실제 존재하는 장소를 활용하여 최적의 데이트 코스를 추천해주세요.

            응답은 반드시 다음 JSON 형식으로 작성해야 합니다:
            {
              "places": [
                {
                  "order": 1,
                  "name": "장소명",
                  "category": "카페|레스토랑|문화시설|액티비티|기타",
                  "category_detail": "구체적인 카테고리 (예: 디저트카페, 이탈리안, 미술관)",
                  "address": "지번 주소",
                  "road_address": "도로명 주소",
                  "location": {
                    "lat": 37.5665,
                    "lng": 126.9780
                  },
                  "phone": "전화번호 (선택)",
                  "recommend_reason": "추천 이유 (한글, 100자 이내)",
                  "estimated_cost": 15000,
                  "estimated_duration": 60,
                  "recommended_time": "14:00-15:00"
                }
              ],
              "summary": "전체 코스 요약 (한글, 150자 이내)"
            }

            주의사항:
            - places는 정확히 3-4개를 추천해야 합니다.
            - order는 1, 2, 3, 4 순서로 방문 순서를 의미합니다.
            - 실제 존재하는 장소를 추천하고, 정확한 주소와 위치 정보를 제공해야 합니다.
            - estimated_cost는 1인 기준 예상 비용(원)입니다.
            - estimated_duration은 해당 장소에서의 예상 소요 시간(분)입니다.
            - recommended_time은 방문 추천 시간대입니다.
            - 데이트 흐름(카페/브런치 → 메인 활동 → 식사/디저트)을 고려하여 순서를 정해주세요.
        """.trimIndent()
    }

    /**
     * 사용자 프롬프트 생성
     * 구체적인 요구사항 포함
     */
    fun buildUserPrompt(
        region: Region,
        dateType: DateType,
        budget: Budget,
        specialRequest: String?
    ): String {
        val dateTypeDescription = when (dateType) {
            DateType.ROMANTIC -> "로맨틱한 데이트 (분위기 있고 특별한 경험)"
            DateType.ACTIVITY -> "액티브한 데이트 (활동적이고 에너지 넘치는)"
            DateType.FOOD -> "맛집 탐방 데이트 (미식 경험 중심)"
            DateType.CULTURE -> "문화적인 데이트 (전시, 공연, 문화 체험)"
            DateType.HEALING -> "힐링 데이트 (휴식과 재충전)"
        }

        val budgetDescription = "총 예산: ${budget.toDisplayName()} (2인 기준)"
        val regionKeywords = if (region.keywords.isNotEmpty()) {
            "\n지역 키워드: ${region.keywords.joinToString(", ")}"
        } else ""

        return """
            다음 조건으로 데이트 코스를 추천해주세요:

            [지역]
            ${region.name} (${region.description})
            중심 좌표: 위도 ${region.centerLat}, 경도 ${region.centerLng}$regionKeywords

            [데이트 유형]
            $dateTypeDescription

            [예산]
            $budgetDescription

            ${specialRequest?.let { "[특별 요청]\n$it\n" } ?: ""}

            위 조건을 고려하여 ${region.name} 지역에서 실제 존재하는 장소를 활용한 데이트 코스 3-4곳을 추천해주세요.
            일반적인 데이트 흐름(카페/브런치 → 메인 활동 → 식사/디저트)을 고려하여 순서를 정해주세요.
            각 장소의 정확한 주소, 위치 정보, 예상 비용을 포함해주세요.
        """.trimIndent()
    }

    /**
     * ChatCompletion 요청 메시지 생성
     */
    fun buildMessages(
        region: Region,
        dateType: DateType,
        budget: Budget,
        specialRequest: String?
    ): List<Message> {
        return listOf(
            Message.system(buildSystemPrompt()),
            Message.user(
                buildUserPrompt(
                    region = region,
                    dateType = dateType,
                    budget = budget,
                    specialRequest = specialRequest
                )
            )
        )
    }
}
