---
name: continuous-learning
description: 세션 학습 시스템. 작업 완료 후 성공/실패 패턴을 자동 추출하여 지식 베이스에 저장. "회고", "학습 기록", "패턴 저장", "lessons learned" 키워드에 반응. 세션 종료 시 자동 활성화 권장.
---

# Continuous Learning System

## 개요
매 작업/세션 후 경험을 구조화하여 `.claude/learnings/`에 저장합니다.
이후 세션에서 동일한 문제를 더 빠르게 해결할 수 있습니다.

## 학습 기록 프로세스

### 1단계: 패턴 추출
작업 완료 후 아래 항목을 분석:
- **성공 패턴**: 효과적이었던 접근 방식
- **실패 패턴**: 시간을 낭비한 시도와 원인
- **발견**: 프로젝트에서 새로 알게 된 사실
- **의사결정**: 중요한 기술적 선택과 근거

### 2단계: 분류 및 저장

#### 저장 위치
```
.claude/learnings/
├── patterns.md          # 반복되는 성공/실패 패턴
├── decisions.md         # 기술적 의사결정 기록
├── debugging.md         # 디버깅 경험과 해결책
└── project-knowledge.md # 프로젝트 특화 지식
```

#### 기록 형식
```markdown
## [날짜] 제목

**컨텍스트**: 어떤 작업을 하다가
**문제/상황**: 무엇이 발생했고
**해결/결과**: 어떻게 해결했으며
**교훈**: 다음에는 이렇게 하면 된다
**관련 파일**: 해당 파일 경로
```

### 3단계: 지식 활용
- 새 세션 시작 시 관련 learnings 파일 참조
- 유사한 문제 발생 시 기존 패턴 검색
- 의사결정 시 과거 decisions.md 참고

## 자동 트리거 시점

### 작업 완료 후 (권장)
- `/dev-cycle` 완료 시
- `/jira:complete` 실행 시
- 복잡한 디버깅 해결 후

### 기록할 가치가 있는 경험
- 30분 이상 걸린 디버깅
- 처음 시도한 접근이 실패한 경우
- 프로젝트 컨벤션을 새로 발견한 경우
- 외부 API 연동에서 삽질한 경우
- 테스트 작성 시 주의할 점을 발견한 경우

### 기록하지 않을 것
- 단순 타이포 수정
- 이미 기록된 동일 패턴
- 일반적인 프로그래밍 지식 (프로젝트 특화만 기록)

## 예시

### patterns.md
```markdown
## [2024-12-15] JPA Entity 매핑 시 lazy loading 주의

**컨텍스트**: Course 조회 API에서 places 포함 응답
**문제**: LazyInitializationException 발생
**해결**: @EntityGraph 또는 FETCH JOIN 사용
**교훈**: 1:N 관계 조회 시 항상 fetch 전략 확인
**관련 파일**: infrastructure/persistence/repository/CourseRepositoryAdapter.kt
```

### debugging.md
```markdown
## [2024-12-16] Docker 환경에서 DB 연결 실패

**컨텍스트**: docker-compose up 후 backend 컨테이너 시작
**문제**: Connection refused to postgres:5432
**원인**: postgres 컨테이너가 ready 되기 전에 backend가 연결 시도
**해결**: depends_on + healthcheck 추가
**교훈**: docker-compose depends_on은 "시작"만 보장, "ready"는 healthcheck 필요
```
