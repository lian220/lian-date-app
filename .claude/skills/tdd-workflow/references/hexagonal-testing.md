# Hexagonal Architecture 테스트 전략

## 레이어별 테스트 방식

```
Domain Service  → 순수 단위 테스트 (Port를 mock)
UseCase         → Port를 mock한 통합 테스트
Controller      → MockMvc로 API 테스트
Repository      → @DataJpaTest로 DB 연동 테스트
```

## 테스트 우선순위

Domain Service > UseCase > Controller > Repository

## 의존성 방향

```
Presentation → Application → Domain ← Infrastructure
(Controller)   (UseCase)   (Service, Port)  (Adapter, Repository)
```

- Domain 레이어는 외부에 의존하지 않음 → mock 없이 순수 테스트 가능
- UseCase는 Port(인터페이스)만 의존 → Port를 mock하여 테스트
- Controller는 UseCase만 의존 → UseCase를 mock하여 테스트
- Repository는 JPA/DB 의존 → @DataJpaTest로 실제 DB 테스트

## 새 도메인 추가 시 테스트 순서

1. Domain Entity/VO 테스트 (불변성, 유효성 검증)
2. Domain Service 테스트 (비즈니스 로직)
3. UseCase 테스트 (오케스트레이션)
4. Controller 테스트 (API 입출력)
5. Repository 테스트 (영속성)

## Mapper 테스트

Domain ↔ JPA Entity 매핑도 테스트 필요:
```kotlin
@Test
fun `should map JPA entity to domain entity`() {
    val jpaEntity = JpaCourseEntity(...)
    val domain = CourseMapper.toDomain(jpaEntity)
    assertThat(domain.id).isEqualTo(...)
}
```
