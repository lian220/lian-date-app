package com.dateclick.api.infrastructure.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.servers.Server
import io.swagger.v3.oas.models.tags.Tag
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {
    @Bean
    fun openApi(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("DateClick API")
                    .description(
                        """
                        ## 데이트 딸깍 API
                        AI 기반 데이트 코스 추천 서비스의 백엔드 API입니다.

                        ### 주요 기능
                        - **Course**: AI 데이트 코스 생성 및 조회
                        - **Place**: 장소 상세 정보 및 AI 큐레이션
                        - **Region**: 지역 목록 조회
                        - **Place Memory**: Vector DB 기반 장소 검색
                        """.trimIndent(),
                    )
                    .version("v1")
                    .contact(
                        Contact()
                            .name("DateClick Team")
                            .email("lian.dy220@gmail.com"),
                    ),
            )
            .addServersItem(Server().url("http://localhost:8080").description("Local"))
            .addServersItem(Server().url("https://api.dateclick.app").description("Production"))
            .addTagsItem(Tag().name("Course").description("데이트 코스 생성·조회·평가"))
            .addTagsItem(Tag().name("Place").description("장소 상세 정보 및 AI 큐레이션"))
            .addTagsItem(Tag().name("Place Memory").description("Vector DB 기반 장소 메모리 관리"))
            .addTagsItem(Tag().name("Region").description("지역 목록 조회"))
            .addTagsItem(Tag().name("Health").description("서버 상태 확인"))
    }
}
