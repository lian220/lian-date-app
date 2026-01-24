package com.dateclick.api.presentation.rest.region

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
class RegionControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `should return all regions when city parameter is not provided`() {
        mockMvc.perform(get("/v1/regions"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.regions").isArray)
            .andExpect(jsonPath("$.data.regions.length()").value(16))
    }

    @Test
    fun `should return Seoul regions when city is seoul`() {
        mockMvc.perform(get("/v1/regions").param("city", "seoul"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.regions").isArray)
            .andExpect(jsonPath("$.data.regions[0].city").value("seoul"))
    }

    @Test
    fun `should return Gyeonggi regions when city is gyeonggi`() {
        mockMvc.perform(get("/v1/regions").param("city", "gyeonggi"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.regions").isArray)
            .andExpect(jsonPath("$.data.regions[0].city").value("gyeonggi"))
    }

    @Test
    fun `should return region with all required fields`() {
        mockMvc.perform(get("/v1/regions").param("city", "seoul"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.regions[0].id").exists())
            .andExpect(jsonPath("$.data.regions[0].name").exists())
            .andExpect(jsonPath("$.data.regions[0].city").exists())
            .andExpect(jsonPath("$.data.regions[0].description").exists())
            .andExpect(jsonPath("$.data.regions[0].keywords").isArray)
            .andExpect(jsonPath("$.data.regions[0].centerLat").exists())
            .andExpect(jsonPath("$.data.regions[0].centerLng").exists())
    }
}
