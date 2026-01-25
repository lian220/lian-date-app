package com.dateclick.api.domain.place.vo

import com.dateclick.api.domain.place.entity.Place
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class PlaceWithCurationTest {

    @Test
    fun `toDocument는 자연어 문서로 변환한다`() {
        // given
        val place = Place(
            id = PlaceId("test-place-id"),
            name = "로맨틱 이탈리안 레스토랑",
            category = "음식점",
            categoryDetail = "이탈리안",
            address = "서울시 강남구 테헤란로 123",
            roadAddress = "서울시 강남구 역삼동 123-45",
            phone = "02-1234-5678",
            location = Location(lat = 37.5665, lng = 126.9780),
            kakaoRating = 4.5,
            kakaoReviewCount = 234
        )

        val curation = PlaceCurationInfo(
            dateScore = 9,
            moodTags = listOf("#로맨틱", "#아늑한", "#고급스러운"),
            priceRange = 50000,
            bestTime = "저녁 7-9시",
            recommendation = "분위기가 로맨틱하고 음식이 맛있어 데이트하기 좋은 곳입니다."
        )

        val placeWithCuration = PlaceWithCuration(place, curation)

        // when
        val document = placeWithCuration.toDocument()

        // then
        assertTrue(document.contains("로맨틱 이탈리안 레스토랑"))
        assertTrue(document.contains("음식점"))
        assertTrue(document.contains("이탈리안"))
        assertTrue(document.contains("서울시 강남구 테헤란로 123"))
        assertTrue(document.contains("37.5665"))
        assertTrue(document.contains("126.978"))  // 소수점 끝자리 0은 생략될 수 있음
        assertTrue(document.contains("02-1234-5678"))
        assertTrue(document.contains("4.5점"))
        assertTrue(document.contains("234개"))
        assertTrue(document.contains("9/10점"))
        assertTrue(document.contains("#로맨틱, #아늑한, #고급스러운"))
        assertTrue(document.contains("50,000원"))
        assertTrue(document.contains("저녁 7-9시"))
        assertTrue(document.contains("분위기가 로맨틱하고"))
    }

    @Test
    fun `getSummary는 간단한 요약 정보를 반환한다`() {
        // given
        val place = Place(
            id = PlaceId("test-place-id"),
            name = "테스트 카페",
            category = "카페",
            categoryDetail = null,
            address = "서울시 강남구",
            roadAddress = null,
            phone = null,
            location = Location(lat = 37.5, lng = 127.0)
        )

        val curation = PlaceCurationInfo(
            dateScore = 8,
            moodTags = listOf("#편안한", "#조용한"),
            priceRange = 15000,
            bestTime = "오후 2-5시",
            recommendation = "여유롭게 대화하기 좋은 곳"
        )

        val placeWithCuration = PlaceWithCuration(place, curation)

        // when
        val summary = placeWithCuration.getSummary()

        // then
        assertTrue(summary.contains("테스트 카페"))
        assertTrue(summary.contains("#편안한, #조용한"))
        assertTrue(summary.contains("8/10"))
        assertTrue(summary.contains("15,000원"))
    }

    @Test
    fun `필수 필드만 있어도 문서 변환이 가능하다`() {
        // given
        val place = Place(
            id = PlaceId("minimal-place"),
            name = "최소 정보 장소",
            category = "기타",
            categoryDetail = null,
            address = "서울시",
            roadAddress = null,
            phone = null,
            location = Location(lat = 37.0, lng = 127.0)
        )

        val curation = PlaceCurationInfo(
            dateScore = 5,
            moodTags = listOf("#평범한"),
            priceRange = 10000,
            bestTime = "언제든지",
            recommendation = "무난한 장소"
        )

        val placeWithCuration = PlaceWithCuration(place, curation)

        // when & then
        assertDoesNotThrow {
            val document = placeWithCuration.toDocument()
            assertTrue(document.isNotEmpty())
            assertTrue(document.contains("최소 정보 장소"))
        }
    }
}
