# 멀티-스테이지 빌드: Spring Boot 애플리케이션 최적화

# Stage 1: Build
FROM gradle:8.12-jdk21-alpine AS builder

WORKDIR /build

# Gradle 의존성 캐싱을 위한 파일 복사
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle ./gradle

# 의존성 다운로드 (캐싱)
RUN gradle dependencies --no-daemon || true

# 소스 코드 복사
COPY src ./src

# 애플리케이션 빌드 (테스트 제외)
RUN gradle bootJar --no-daemon -x test

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# 비루트 사용자 생성
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# 빌드 스테이지에서 JAR 파일 복사
COPY --from=builder /build/build/libs/*.jar app.jar

# 포트 노출
EXPOSE 8080

# 헬스체크
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/health || exit 1

# Spring 프로파일은 docker-compose.yml 또는 환경 변수로 설정
# Default: no profile (uses application.yml with Flyway enabled)
# Local dev in IDE: set SPRING_PROFILES_ACTIVE=local (Flyway disabled, uses RegionDataLoader)
# Production: set SPRING_PROFILES_ACTIVE=prod (Flyway enabled with strict validation)

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
