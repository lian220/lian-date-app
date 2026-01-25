package com.dateclick.api.domain.place.vo

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 * PlaceCurationInfo Value Object 테스트
 */
class PlaceCurationInfoTest {

    @Test
    fun `should create PlaceCurationInfo successfully`() {
        val curationInfo = PlaceCurationInfo(
            dateScore = 8,
            moodTags = listOf("#로맨틱", "#아늑한", "#트렌디"),
            priceRange = 25000,
            bestTime = "저녁 7-9시",
            recommendation = "분위기 좋은 루프탑에서 야경을 즐기며 데이트하기 좋은 곳"
        )

        assertEquals(8, curationInfo.dateScore)
        assertEquals(3, curationInfo.moodTags.size)
        assertEquals(25000, curationInfo.priceRange)
        assertEquals("저녁 7-9시", curationInfo.bestTime)
        assertNotNull(curationInfo.recommendation)
    }

    @Test
    fun `should fail when dateScore is out of range`() {
        assertThrows<IllegalArgumentException> {
            PlaceCurationInfo(
                dateScore = 0, // Invalid: must be 1-10
                moodTags = listOf("#로맨틱"),
                priceRange = 25000,
                bestTime = "저녁 7-9시",
                recommendation = "추천 이유"
            )
        }

        assertThrows<IllegalArgumentException> {
            PlaceCurationInfo(
                dateScore = 11, // Invalid: must be 1-10
                moodTags = listOf("#로맨틱"),
                priceRange = 25000,
                bestTime = "저녁 7-9시",
                recommendation = "추천 이유"
            )
        }
    }

    @Test
    fun `should fail when moodTags exceed 3 items`() {
        assertThrows<IllegalArgumentException> {
            PlaceCurationInfo(
                dateScore = 8,
                moodTags = listOf("#로맨틱", "#아늑한", "#트렌디", "#모던"), // Invalid: max 3
                priceRange = 25000,
                bestTime = "저녁 7-9시",
                recommendation = "추천 이유"
            )
        }
    }

    @Test
    fun `should fail when priceRange is negative`() {
        assertThrows<IllegalArgumentException> {
            PlaceCurationInfo(
                dateScore = 8,
                moodTags = listOf("#로맨틱"),
                priceRange = -1000, // Invalid: must be non-negative
                bestTime = "저녁 7-9시",
                recommendation = "추천 이유"
            )
        }
    }

    @Test
    fun `should allow empty moodTags`() {
        val curationInfo = PlaceCurationInfo(
            dateScore = 5,
            moodTags = emptyList(),
            priceRange = 10000,
            bestTime = "오후 2-5시",
            recommendation = "간단한 카페 데이트"
        )

        assertTrue(curationInfo.moodTags.isEmpty())
    }

    @Test
    fun `should allow priceRange of zero`() {
        val curationInfo = PlaceCurationInfo(
            dateScore = 7,
            moodTags = listOf("#자연", "#힐링"),
            priceRange = 0, // Free place
            bestTime = "오전 10-12시",
            recommendation = "무료로 즐기는 공원 데이트"
        )

        assertEquals(0, curationInfo.priceRange)
    }
}
