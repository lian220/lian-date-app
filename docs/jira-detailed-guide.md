# Jira 티켓 생성 상세 가이드

> 💡 **빠른 참고가 필요하신가요?** → [Jira 빠른 참고 가이드](./jira-quick-reference.md) (1페이지, 실무용)

## 📋 개요

이 문서는 PRD를 기반으로 체계적인 Jira 티켓을 생성하는 방법론을 제공합니다.

> **활용 방법**: 이 가이드는 스크럼, 칸반 등 다양한 애자일 방법론에 적용 가능하며, 프로젝트 규모와 팀 구성에 맞게 조정하여 사용하세요.

## 🎯 Jira 티켓의 목적

- **작업 추적**: 개발 진행 상황을 투명하게 관리
- **업무 분배**: 팀원별 역할과 책임 명확화
- **일정 관리**: 스프린트와 마일스톤 기반 계획 수립
- **커뮤니케이션**: 요구사항과 구현 상세를 문서화
- **품질 관리**: 인수 조건 기반 완료 기준 설정

## 📊 티켓 계층 구조

Jira는 3단계 계층 구조를 권장합니다:

```
Epic (대규모 기능)
  └─ Story (사용자 관점 기능)
      ├─ Sub-task (Frontend)
      ├─ Sub-task (Backend)
      └─ Sub-task (Design/QA 등)
```

### Epic
- **정의**: 여러 Sprint에 걸쳐 완성되는 대규모 기능 또는 제품 목표
- **범위**: 보통 한 분기 또는 릴리즈 단위
- **예시**: "[MVP] 사용자 인증 시스템", "[Q1] 결제 기능 구현"

### Story
- **정의**: 사용자 관점에서 가치를 제공하는 기능 단위
- **범위**: 1-2주 내 완료 가능한 크기
- **예시**: "사용자가 소셜 로그인을 할 수 있다", "관리자가 대시보드를 조회할 수 있다"

### Sub-task
- **정의**: Story를 기술적으로 분해한 작업 단위
- **범위**: 1-3일 내 완료 가능한 크기
- **분류**: Frontend, Backend, Design, QA, DevOps 등

## ✍️ User Story 작성법

### 표준 포맷
```
"[역할]로서, [행동]하고 싶다. [이유] 때문이다."
```

### 예시
```markdown
**좋은 예시** ✅
"고객으로서, 장바구니에 상품을 추가하고 싶다. 나중에 구매하기 위해서다."
"관리자로서, 월별 매출 리포트를 조회하고 싶다. 비즈니스 성과를 분석하기 위해서다."

**나쁜 예시** ❌
"장바구니 기능을 만든다" (역할과 이유 누락)
"데이터베이스에 상품 테이블을 추가한다" (기술 구현 중심, 사용자 가치 부재)
```

### 작성 원칙
- **독립적**: 다른 Story와 독립적으로 가치 제공
- **협상 가능**: 구현 방법은 개발팀과 협의 가능
- **가치 중심**: 사용자에게 명확한 가치 제공
- **추정 가능**: 팀이 effort를 추정할 수 있는 크기
- **작은 크기**: 1-2 Sprint 내 완료 가능
- **테스트 가능**: 완료 기준이 명확

## ✅ Acceptance Criteria (인수 조건) 작성법

### Given-When-Then 포맷
```markdown
**Given** (전제 조건)
- 사용자가 로그인된 상태
- 장바구니가 비어있지 않음

**When** (사용자 행동)
- 사용자가 "결제하기" 버튼을 클릭

**Then** (시스템 반응)
- [ ] 결제 페이지로 이동한다
- [ ] 장바구니 총액이 표시된다
- [ ] 결제 수단 선택 옵션이 보인다
- [ ] 주문 정보가 세션에 저장된다
```

### 체크리스트 포맷 (간단한 AC)
```markdown
- [ ] 사용자가 이메일 형식으로만 입력 가능
- [ ] 중복 이메일 검증 (서버 사이드)
- [ ] 인증 메일 발송 성공
- [ ] 에러 발생 시 사용자 친화적 메시지 표시
```

### 작성 원칙
- **구체적**: 모호함 없이 명확한 조건
- **측정 가능**: 통과/실패 판단 가능
- **완전성**: 기능의 모든 케이스 포함
- **비기술적**: 비즈니스 관점에서 작성 (가능하면)
- **우선순위**: 필수(MUST) vs 선택(SHOULD) 구분

## 📦 Frontend/Backend Sub-task 분해

### Frontend Sub-task 작성 패턴

```markdown
#### [FE] 로그인 폼 UI 구현
**설명**: 이메일/비밀번호 입력 폼과 로그인 버튼 구현

**세부 작업**:
- [ ] 이메일 입력 필드 (validation 포함)
- [ ] 비밀번호 입력 필드 (show/hide 토글)
- [ ] 로그인 버튼 (로딩 상태 처리)
- [ ] 에러 메시지 표시 영역
- [ ] 소셜 로그인 버튼 (Google, Apple)

**의존성**:
- API: `POST /v1/auth/login`
- API: `POST /v1/auth/social`

**우선순위**: P0
```

### Backend Sub-task 작성 패턴

```markdown
#### [BE] 로그인 API 구현
**설명**: 이메일/비밀번호 기반 인증 API

**세부 작업**:
- [ ] `POST /v1/auth/login` 엔드포인트
- [ ] Request DTO validation
- [ ] 비밀번호 해싱 검증
- [ ] JWT 토큰 발급
- [ ] Refresh Token 저장
- [ ] Rate limiting (5회/분)

**도메인 모델**:
- User Entity
- AuthToken Value Object
- UserRepository

**의존성**:
- JWT 라이브러리 설정
- Redis (Refresh Token 저장)

**우선순위**: P0
```

### 분해 원칙

| 기준 | Frontend | Backend |
|------|----------|---------|
| **단위** | 화면/컴포넌트 단위 | API/도메인 단위 |
| **설명** | UI/UX 상세, 사용자 인터랙션 | API 명세, 비즈니스 로직 |
| **의존성** | API 엔드포인트, 디자인 시스템 | 외부 API, 인프라, 도메인 모델 |
| **완료 기준** | 스토리북/화면 동작 검증 | API 테스트, 단위 테스트 통과 |

## 🎯 우선순위 설정 기준

### P0 (Critical) - MVP 필수
- **정의**: 이것 없이는 출시 불가능
- **범위**: 핵심 사용자 플로우, 비즈니스 크리티컬 기능
- **예시**: 회원가입, 로그인, 결제, 주문 생성

### P1 (High) - MVP 권장
- **정의**: UX 향상, 사용자 만족도 개선
- **범위**: 편의 기능, 에러 처리 개선, 성능 최적화
- **예시**: 검색 필터, 정렬 옵션, 프로필 수정, 푸시 알림

### P2 (Medium) - Post-MVP
- **정의**: Nice to have, 차기 버전에서 구현
- **범위**: 부가 기능, 실험적 기능
- **예시**: 다크모드, 고급 통계, 커스터마이징 옵션

### P3 (Low) - 백로그
- **정의**: 미래 고려사항
- **범위**: 아이디어 수준, 검증 필요
- **예시**: AI 추천, 소셜 기능 확장

## 📝 티켓 작성 템플릿

### Epic 템플릿

```markdown
# [Epic] {기능명}

## 📌 Epic 개요
> {한 문장 요약: 이 Epic이 제공하는 사용자 가치}

## 🎯 비즈니스 목표
- 목표 1: {구체적 목표와 측정 지표}
- 목표 2: {구체적 목표와 측정 지표}

## 📊 성공 지표
| 지표 | 목표값 | 측정 방법 |
|------|--------|-----------|
| ... | ... | ... |

## 🔗 관련 문서
- PRD: [링크]
- 디자인: [Figma 링크]
- 기술 명세: [링크]

## 📅 타임라인
- 시작: {날짜}
- 목표 완료: {날짜}
- 릴리즈: {날짜}

## 🏷️ Labels
`mvp`, `core-feature`, `frontend`, `backend`
```

### Story 템플릿

```markdown
# {기능명}

## 👤 User Story
> "{역할}로서, {행동}하고 싶다. {이유} 때문이다."

## ✅ Acceptance Criteria

### AC 1: {세부 기능명}

**Given** (전제 조건)
- ...

**When** (사용자 행동)
- ...

**Then** (시스템 반응)
- [ ] ...
- [ ] ...

### AC 2: {세부 기능명}
...

## 🎨 UI/UX 참고
- Figma: [링크]
- 스크린샷: [첨부]

## 🔗 관련 API
- `METHOD /v1/resource` - 설명

## 📋 Sub-tasks
- [ ] [FE] UI 구현
- [ ] [BE] API 구현
- [ ] [QA] 테스트 케이스 작성

## 🏷️ Labels
`story`, `frontend`, `backend`, `p0`

## 📊 Story Points
{피보나치: 1, 2, 3, 5, 8, 13}
```

### Sub-task 템플릿

```markdown
# [FE/BE] {작업명}

## 📝 설명
{무엇을 구현하는지 1-2 문장}

## ✅ 세부 작업
- [ ] 작업 1
- [ ] 작업 2
- [ ] 작업 3

## 🔗 의존성
- API: `POST /v1/...`
- 라이브러리: ...
- 선행 작업: {티켓 번호}

## 📋 완료 기준
- [ ] 코드 리뷰 완료
- [ ] 단위 테스트 작성 (커버리지 80% 이상)
- [ ] 로컬 동작 검증

## 🏷️ Labels
`frontend` or `backend`, `p0`

## ⏱️ 예상 시간
{1일, 2일, 3일}
```

## 🔄 PRD → Jira 티켓 변환 프로세스

### 1단계: Epic 생성
```
PRD의 "제품 목표" → Epic
- Epic 제목: [MVP] {제품명}
- Epic 설명: PRD의 배경 및 문제 정의 요약
- 성공 지표: PRD의 HEART 프레임워크 → Epic 성공 지표
```

### 2단계: Story 생성 (기능별)
```
PRD의 "MVP 기능 명세" → Story
- 각 기능 (F1, F2, F3...) → 1개 Story
- Story 제목: {기능명}
- User Story: PRD의 User Story 복사
- AC: PRD의 Acceptance Criteria 복사
```

### 3단계: Sub-task 생성
```
Story → Frontend/Backend Sub-task
- Frontend Sub-task: UI/UX 요구사항 → 화면/컴포넌트 단위
- Backend Sub-task: API 명세 + 비즈니스 로직 → API/도메인 단위
```

### 4단계: 우선순위 배정
```
PRD의 "MVP 범위" 확인
- MVP 필수 기능 → P0
- MVP 권장 기능 → P1
- Nice to have → P2
- Out of Scope → 백로그 (우선순위 없음)
```

### 5단계: Sprint 배정
```
Story Points 추정 → Sprint 배정
- Story Points 합산 (팀 Velocity 고려)
- 의존성 고려 (선행 작업 먼저 배정)
- 병렬 작업 가능 여부 확인
```

## 📊 티켓 생성 예시

### 예시 1: 이커머스 프로젝트

```markdown
Epic: [MVP] 주문 및 결제 시스템
├─ Story 1: 장바구니 관리
│   ├─ [FE] 장바구니 목록 UI (P0, 2SP)
│   ├─ [FE] 상품 수량 조절 (P0, 1SP)
│   ├─ [BE] 장바구니 CRUD API (P0, 3SP)
│   └─ [BE] 재고 검증 로직 (P0, 2SP)
│
├─ Story 2: 결제 프로세스
│   ├─ [FE] 결제 정보 입력 폼 (P0, 3SP)
│   ├─ [FE] 결제 수단 선택 (P0, 2SP)
│   ├─ [BE] 결제 API 연동 (P0, 5SP)
│   ├─ [BE] 주문 생성 로직 (P0, 3SP)
│   └─ [BE] 결제 웹훅 처리 (P1, 3SP)
│
└─ Story 3: 주문 내역 조회
    ├─ [FE] 주문 목록 화면 (P1, 2SP)
    ├─ [FE] 주문 상세 화면 (P1, 2SP)
    └─ [BE] 주문 조회 API (P1, 2SP)

총합: 3 Stories, 12 Sub-tasks, 30 Story Points
```

### 예시 2: SaaS 대시보드

```markdown
Epic: [MVP] 분석 대시보드
├─ Story 1: 데이터 시각화
│   ├─ [FE] 차트 컴포넌트 구현 (P0, 5SP)
│   ├─ [FE] 필터 옵션 UI (P0, 3SP)
│   ├─ [BE] 통계 데이터 API (P0, 5SP)
│   └─ [BE] 데이터 집계 배치 작업 (P1, 5SP)
│
└─ Story 2: 리포트 내보내기
    ├─ [FE] PDF 내보내기 버튼 (P1, 2SP)
    ├─ [BE] PDF 생성 API (P1, 3SP)
    └─ [BE] 이메일 발송 (P2, 2SP)

총합: 2 Stories, 7 Sub-tasks, 25 Story Points
```

## 📈 티켓 관리 베스트 프랙티스

### DO ✅

1. **명확한 제목**
   - ✅ "[FE] 로그인 폼 - 이메일 validation 구현"
   - ❌ "로그인 수정"

2. **구체적인 AC**
   - ✅ "사용자가 잘못된 이메일 형식 입력 시 '유효한 이메일을 입력하세요' 메시지 표시"
   - ❌ "이메일 검증 추가"

3. **의존성 명시**
   - API 엔드포인트, 선행 작업, 외부 서비스 명시
   - Blocked by: {티켓 번호}

4. **완료 기준 설정**
   - 코드 리뷰, 테스트, QA 검증 기준 명시

5. **정기적 업데이트**
   - 진행 상황, 블로커, 완료율 주기적 업데이트

### DON'T ❌

1. **모호한 설명**
   - ❌ "기능 개선", "버그 수정", "리팩토링"

2. **너무 큰 티켓**
   - Story > 13SP → 분해 필요
   - Sub-task > 3일 → 분해 필요

3. **의존성 무시**
   - FE 작업인데 API 준비 안 됨
   - BE 작업인데 DB 스키마 미정

4. **우선순위 남발**
   - 모든 게 P0면 우선순위가 없는 것

5. **AC 누락**
   - 완료 기준이 없으면 "완료"를 판단할 수 없음

## 🔧 도구 및 자동화

### Jira Automation 활용
```yaml
# 예시: Story 완료 시 Epic 진행률 업데이트
when: Story status → Done
then: Update Epic progress percentage

# 예시: Sub-task 모두 완료 시 Story 자동 완료
when: All Sub-tasks → Done
then: Move Story to Review
```

### Git 연동
```bash
# 커밋 메시지에 티켓 번호 포함
git commit -m "PROJECT-123: feat(auth): 로그인 API 구현"

# Jira 자동 업데이트
- 작업 로그 추가
- 상태 전환 (In Progress → In Review)
```

### Claude Code + Jira 연동
```bash
# .claude/commands/jira-commit.md 활용
jira-commit PROJECT-123 "feat(feature): 기능 구현"

# 자동화:
- Conventional Commits 검증
- Jira 작업 로그 추가
- 상태 전환
```

## 📊 티켓 요약 템플릿

프로젝트 완료 후 또는 Sprint 계획 시 사용:

```markdown
## 티켓 요약

### 전체 현황
| 구분 | 수량 |
|------|------|
| Epic | X |
| Story | Y |
| Frontend Sub-tasks | Z |
| Backend Sub-tasks | W |
| **총합** | **N개** |

### 기능별 분포
| 기능 | Story | FE Tasks | BE Tasks |
|------|-------|----------|----------|
| 기능 1 | 1 | 5 | 3 |
| 기능 2 | 1 | 4 | 6 |

### 우선순위 분포
| 우선순위 | 설명 | 수량 |
|----------|------|------|
| **P0** | MVP 필수 | X |
| **P1** | MVP 권장 | Y |
| **P2** | Nice to have | Z |

### Story Points 분포
| Sprint | Story Points | Stories | 완료율 |
|--------|--------------|---------|--------|
| Sprint 1 | 40 | 5 | 80% |
| Sprint 2 | 35 | 4 | 60% |
```

## ❓ FAQ

### Q1: Story와 Task의 차이는?
**A**:
- **Story**: 사용자 관점 가치 제공 단위 (비즈니스 관점)
- **Task/Sub-task**: 기술적 구현 단위 (개발 관점)

Story는 "왜, 무엇"에 집중하고, Task는 "어떻게"에 집중합니다.

### Q2: Story Points는 어떻게 산정하나요?
**A**:
- **피보나치 수열 사용**: 1, 2, 3, 5, 8, 13
- **상대적 추정**: 기준 Story 대비 복잡도
- **팀 합의**: Planning Poker 등 활용
- **재추정**: Sprint 회고 후 Velocity 조정

### Q3: Sub-task는 언제 만드나요?
**A**:
- Story가 너무 클 때 (>8SP)
- 여러 역할이 협업할 때 (FE + BE + Design)
- 병렬 작업이 가능할 때
- 진행 상황을 세밀하게 추적하고 싶을 때

### Q4: 티켓이 너무 많으면?
**A**:
- Epic을 더 큰 단위로 통합
- Sub-task를 Story로 승격 (독립적 가치 제공 시)
- MVP 범위 재검토 (P2 이하는 백로그로)

### Q5: 긴급 버그는 어떻게 처리하나요?
**A**:
- 별도 Bug 티켓 타입 생성
- 우선순위: P0 (Critical), P1 (High)
- Sprint 중간 투입 시 팀 합의 필요
- Sprint Scope 조정 (다른 티켓 연기)

---

## 📝 변경 이력

| 버전 | 날짜 | 작성자 | 변경 내용 |
|------|------|--------|-----------|
| v1.0 | 2026-01-24 | Claude | 초안 작성 |
| v1.1 | 2026-01-25 | Claude | 문서 분리 (Quick Reference 추가) |

---

**문서 관리**
- 위치: `docs/jira-detailed-guide.md` (상세 가이드)
- Quick Reference: `docs/jira-quick-reference.md` (빠른 참고용)
- 관련 문서: `docs/how-to-write-prd.md`, `.claude/commands/jira-commit.md`
- 다음 리뷰: 프로젝트 Jira 티켓 생성 시
