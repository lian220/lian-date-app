# 개발 사이클 (3단계 워크플로우)

**사용법**: `/dev-cycle LAD-42`

Jira 티켓 기반 3단계 개발 사이클을 실행합니다.
각 단계 완료 후 사용자 확인을 받고 다음 단계로 진행합니다.

## 인자
- 첫 번째: Jira 티켓 ID (필수, 예: LAD-42)

## 실행 내용

$ARGUMENTS

---

## 1단계: 준비 + 설계

### 1-1. Jira 티켓 시작
1. `jira:start $1`과 동일한 절차 실행:
   - Jira 티켓 정보 조회 (mcp__atlassian__getJiraIssue)
   - 티켓 상태 "진행 중"으로 전환
   - feature 브랜치 생성 (형식: `feature/{티켓ID}-{제목-kebab-case}`)
2. **AC(수락 조건) 추출** → 이후 단계에서 검증 기준으로 사용

### 1-2. 아키텍처 설계 (@backend-architect)
1. AC 기반으로 Hexagonal Architecture 설계:
   - **변경 범위 파악**: domain → application → infrastructure → presentation
   - **새로 추가/수정할 파일 목록** 도출
   - **의존성 방향 검증**: Presentation → Application → Domain ← Infrastructure
2. 설계 결과물:
   - 레이어별 변경 파일 목록
   - 새로 만들 Port/Adapter/Entity/VO 정리
   - 테스트 전략 (어떤 레이어부터 테스트할지)

### 1단계 완료 출력
```
📋 1단계 완료: 준비 + 설계

🎫 티켓: {티켓ID} - {제목}
🌿 브랜치: feature/{브랜치명}
📝 AC: {수락 조건 목록}

🏗️ 설계 결과:
- Domain: {변경할 entity/vo/port/service}
- Application: {변경할 usecase}
- Infrastructure: {변경할 adapter/repository}
- Presentation: {변경할 controller/dto}

🧪 테스트 전략: {레이어별 테스트 순서}
```

**→ 사용자에게 확인 요청**: "1단계 설계 결과를 확인해주세요. 2단계(TDD 구현)로 진행할까요?"

---

## 2단계: TDD 구현

1단계 설계를 기반으로 Red→Green→Refactor 사이클을 반복합니다.

### 2-1. 구현 순서 (Hexagonal Architecture)
설계에서 정한 순서대로 진행. 기본 순서:

1. **Domain Layer** (entity, VO, port interface, domain service)
2. **Application Layer** (use case)
3. **Infrastructure Layer** (JPA entity, mapper, repository adapter, 외부 API adapter)
4. **Presentation Layer** (controller, request/response DTO, mapper)

### 2-2. 레이어별 TDD 사이클
각 레이어마다 아래를 반복:

#### Red: 실패하는 테스트 작성
- 테스트 위치 규칙:
  - Domain Service → `test/.../domain/{도메인}/service/`
  - UseCase → `test/.../application/{도메인}/`
  - Controller → `test/.../presentation/rest/`
  - Repository → `test/.../infrastructure/persistence/`
- Given-When-Then 패턴 사용
- 테스트 실행하여 **실패 확인**: `./gradlew test --tests "TestClass"`

#### Green: 테스트 통과하는 최소 코드 작성
- 테스트를 통과시키는 **최소한의 코드**만 작성
- 과도한 설계나 미래 대비 코드 금지 (YAGNI)
- 테스트 실행하여 **통과 확인**

#### Refactor: 코드 구조 개선
- 테스트 통과를 유지하며 중복 제거, 네이밍 개선
- `./gradlew test`로 전체 테스트 통과 확인

### 2-3. 중간 커밋
- 의미 있는 단위로 커밋 (레이어 완성, 주요 기능 완성 등)
- Conventional Commits: `feat({scope}): {메시지} [{티켓ID}]`

### 2단계 완료 출력
```
🔨 2단계 완료: TDD 구현

✅ 구현 완료 파일:
- {파일별 변경 요약}

🧪 테스트 결과:
- 전체: X개 통과 / 0개 실패
- Domain: X개
- Application: X개
- Presentation: X개
- Infrastructure: X개

📝 커밋 이력:
- {커밋 목록}
```

**→ 사용자에게 확인 요청**: "2단계 구현이 완료되었습니다. 3단계(검증 + 완료)로 진행할까요?"

---

## 3단계: 검증 + 완료

### 3-1. 코드 리뷰 (@code-reviewer)
`@code-reviewer` 에이전트를 호출하여 변경사항 리뷰:
- Architecture (Hexagonal 위반 검사)
- Backend (SOLID, exception handling, SQL injection)
- Security (OWASP Top 10)
- General (DRY, dead code, naming)

### 3-2. 이슈 수정 (@debugger)
- 코드 리뷰에서 **Critical/Warning** 이슈가 발견되면:
  - `@debugger` 에이전트로 수정
  - 수정 후 `./gradlew test` 재확인
- 이슈 없으면 이 단계 스킵

### 3-3. 최종 테스트
```bash
cd backend && ./gradlew test --no-daemon
```
- 백엔드 전체 테스트 통과 확인
- 프론트엔드 변경 시: `cd frontend && npm run lint && npm run build`

### 3-4. Jira 완료 처리
`jira:complete $1`과 동일한 절차:
1. docs/ 문서 영향 분석 및 업데이트
2. PR 생성 (gh cli)
   - 제목: `[$1] {티켓 제목}`
   - 본문: 변경사항 요약, 테스트 결과, AC 체크리스트
3. Jira 티켓 상태 업데이트
4. PR 링크를 Jira 티켓에 연결

### 3단계 완료 출력
```
🎉 3단계 완료: 검증 + 완료

📝 코드 리뷰 결과:
- Critical: 0개
- Warning: 0개
- Suggestion: N개

🧪 최종 테스트: ✅ 전체 통과

🔗 PR: {PR URL}
🎫 Jira: {티켓 상태}

✅ 개발 사이클 완료!
```

---

## 중단 및 재개

- 각 단계에서 사용자가 "중단"하면 현재 상태를 커밋하고 중단
- 재개 시 `/dev-cycle $1`을 다시 실행하면 현재 브랜치/상태를 감지하여 적절한 단계부터 재개
- 이미 브랜치가 존재하면 1-1 스킵 → 설계 또는 구현부터 시작

## 핵심 규칙

1. **테스트 없이 비즈니스 로직을 작성하지 않는다**
2. **각 단계 사이에 반드시 사용자 확인을 받는다**
3. **Critical 이슈가 있으면 PR을 생성하지 않는다**
4. **AC를 모두 충족해야 완료 처리한다**
