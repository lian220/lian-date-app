package com.dateclick.api.infrastructure.external.openai

import com.dateclick.api.domain.place.port.PlaceCurationPort
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.TestPropertySource

/**
 * 장소 큐레이션 통합 테스트
 * Spring Context 로딩 및 Bean 등록 확인
 */
@SpringBootTest
@TestPropertySource(
    properties = [
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "openai.api-key=test-key-for-curation-test",
        "openai.model=gpt-4o-mini",
        "openai.max-tokens=2000",
        "openai.temperature=0.7",
    ],
)
class PlaceCurationIntegrationTest {
    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @Test
    fun `should register OpenAiPlaceCurationAdapter as Bean`() {
        val adapter = applicationContext.getBean(OpenAiPlaceCurationAdapter::class.java)
        assertNotNull(adapter)
    }

    @Test
    fun `should register PlaceCurationPort implementation`() {
        val port = applicationContext.getBean(PlaceCurationPort::class.java)
        assertNotNull(port)
        assertTrue(port is OpenAiPlaceCurationAdapter)
    }
}
