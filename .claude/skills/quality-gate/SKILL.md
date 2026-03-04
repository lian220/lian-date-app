---
name: quality-gate
description: 4단계 품질 게이트 시스템. 코드 변경, PR 생성, 배포 전 품질 검증 자동 적용. "품질 검증", "quality check", "배포 전 확인", "PR 준비" 키워드에 반응. /dev-cycle 3단계, /jira:complete 실행 시 자동 연동.
---

# Quality Gate System

## 4단계 등급

| 등급 | 의미 | 조건 | 행동 |
|------|------|------|------|
| **PASS** | 배포 가능 | 모든 게이트 통과 | PR 생성 진행 |
| **CONCERNS** | 조건부 통과 | Minor 이슈만 존재 | 이슈 목록 표시, 판단은 사용자에게 |
| **REWORK** | 재작업 필요 | Major 이슈 존재 | 수정 후 재검증 필수 |
| **FAIL** | 차단 | Critical 이슈 존재 | PR 생성 차단, 즉시 수정 |

## 게이트 체크리스트

### Gate 1: 빌드 & 테스트
```bash
# Backend
cd backend && ./gradlew clean build test --no-daemon

# Frontend (변경 시)
cd frontend && npm run lint && npm run build
```
- [ ] 빌드 성공
- [ ] 전체 테스트 통과
- [ ] 린트 에러 없음

### Gate 2: 코드 품질
- [ ] 새 코드에 테스트 존재 (비즈니스 로직)
- [ ] Hexagonal Architecture 위반 없음
- [ ] DRY 위반 없음 (중복 코드)
- [ ] 네이밍 컨벤션 준수
- [ ] 불필요한 TODO/FIXME 없음

### Gate 3: 보안
- [ ] 하드코딩된 시크릿 없음
- [ ] SQL injection 가능성 없음
- [ ] 입력 유효성 검증 있음
- [ ] 민감 정보 로그 출력 없음
- [ ] CORS 설정 적절

### Gate 4: 아키텍처
- [ ] Domain → Infrastructure 의존 없음
- [ ] Controller에 비즈니스 로직 없음
- [ ] DTO와 Domain Entity 분리
- [ ] Port/Adapter 패턴 준수
- [ ] 트랜잭션 경계 올바름

## 등급 산정 로직

```
Gate 결과 수집:
├── Gate 1 실패 → FAIL (빌드/테스트 실패는 무조건 차단)
├── Gate 2-4 중 Critical → FAIL
├── Gate 2-4 중 Major → REWORK
├── Gate 2-4 중 Minor만 → CONCERNS
└── 모두 통과 → PASS
```

## 출력 형식

```
## Quality Gate Report

### 등급: [PASS | CONCERNS | REWORK | FAIL]

📦 Gate 1 - Build & Test: ✅/❌
  - Backend: X tests passed
  - Frontend: lint ✅, build ✅

📝 Gate 2 - Code Quality: ✅/⚠️/❌
  - [항목별 결과]

🛡️ Gate 3 - Security: ✅/⚠️/❌
  - [항목별 결과]

🏗️ Gate 4 - Architecture: ✅/⚠️/❌
  - [항목별 결과]

### 결론
[등급에 따른 다음 행동 안내]
```

## 연동
- `/dev-cycle` 3단계에서 자동 실행
- `/jira:complete` 실행 시 Gate 1 자동 검증
- `/agent-teams quality` 프리셋과 연동
