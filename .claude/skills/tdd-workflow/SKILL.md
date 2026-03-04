---
name: tdd-workflow
description: TDD 기반 3단계 개발 워크플로우 가이드. 새로운 기능 구현, 코드 작성, 비즈니스 로직 추가, UseCase 구현, API 엔드포인트 개발 시 자동 적용. "구현해줘", "만들어줘", "추가해줘", "개발해줘" 키워드에 반응.
---

# TDD 기반 개발 워크플로우

## 3단계 프로세스

### 1단계: 준비 + 설계
- Jira 티켓의 AC(인수조건) 확인
- `@backend-architect` 에이전트로 Hexagonal Architecture 설계
- 변경 범위 파악: domain → application → infrastructure → presentation

### 2단계: TDD 구현
- **Red**: 실패하는 테스트 먼저 작성
- **Green**: 테스트를 통과시키는 최소 코드 작성
- **Refactor**: 테스트 통과 유지하며 구조 개선
- 한 번에 하나의 테스트만 추가, 사이클 반복

### 3단계: 검증 + 완료
- `@code-reviewer` 에이전트로 코드 리뷰
- 이슈 발견 시 `@debugger` 에이전트로 수정
- `./gradlew test` 전체 통과 확인

## TDD 핵심 규칙

- 테스트 없이 비즈니스 로직을 작성하지 않는다
- 테스트가 실패하는 이유를 확인한 후 구현한다
- 구현 후 `./gradlew test`로 전체 테스트 통과를 확인한다

## 상세 가이드

- **테스트 작성 패턴**: [references/test-patterns.md](references/test-patterns.md)
- **Hexagonal Architecture 테스트 전략**: [references/hexagonal-testing.md](references/hexagonal-testing.md)
