package com.dateclick.api.infrastructure.external.openai

import com.dateclick.api.domain.course.port.outbound.AiGenerationPort
import com.dateclick.api.infrastructure.config.OpenAiProperties
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.TestPropertySource

/**
 * OpenAI 통합 테스트
 * Spring Context 로딩 및 Bean 등록 확인
 */
@SpringBootTest
@TestPropertySource(
    properties = [
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "openai.api-key=test-key-for-integration-test",
        "openai.model=gpt-4",
        "openai.max-tokens=2000",
        "openai.temperature=0.7"
    ]
)
class OpenAiIntegrationTest {

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @Autowired
    private lateinit var openAiProperties: OpenAiProperties

    @Test
    fun `should load Spring context successfully`() {
        assertNotNull(applicationContext)
    }

    @Test
    fun `should register OpenAiClient as Bean`() {
        val openAiClient = applicationContext.getBean(OpenAiClient::class.java)
        assertNotNull(openAiClient)
    }

    @Test
    fun `should register OpenAiGenerationAdapter as Bean`() {
        val adapter = applicationContext.getBean(OpenAiGenerationAdapter::class.java)
        assertNotNull(adapter)
    }

    @Test
    fun `should register AiGenerationPort implementation`() {
        val port = applicationContext.getBean(AiGenerationPort::class.java)
        assertNotNull(port)
        assertTrue(port is OpenAiGenerationAdapter)
    }

    @Test
    fun `should load OpenAI properties correctly`() {
        assertNotNull(openAiProperties)
        assertEquals("test-key-for-integration-test", openAiProperties.apiKey)
        assertEquals("gpt-4", openAiProperties.model)
        assertEquals(2000, openAiProperties.maxTokens)
        assertEquals(0.7, openAiProperties.temperature)
    }

    @Test
    fun `should create RestClient for OpenAI`() {
        val restClient = applicationContext.getBean("openAiRestClient")
        assertNotNull(restClient)
    }
}
