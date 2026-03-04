# .claude 디렉토리 가이드

Date Click 프로젝트의 Claude Code 커스텀 설정 모음입니다.

---

## Agents (에이전트)

독립 컨텍스트에서 전문 분석을 수행하는 서브에이전트입니다.
`@에이전트명`으로 호출합니다.

| 에이전트 | 설명 | 사용 시점 |
|----------|------|-----------|
| **@code-reviewer** | 시니어 관점 코드 리뷰. 아키텍처 위반, 보안, 품질 검출 | 코드 변경 후, PR 전 |
| **@debugger** | 4단계 디버깅 (재현→원인분석→최소수정→검증) | 에러, 테스트 실패 시 |
| **@security-sentinel** | OWASP Top 10 + Hexagonal 경계 보안 감사 | 보안 점검, 배포 전 |
| **@performance-profiler** | JVM/SQL/API 성능 분석 및 최적화 | 느린 API, 성능 이슈 시 |
| **@test-generator** | 레이어별 테스트 자동 생성 (Domain→UseCase→Controller) | 커버리지 보강 시 |

---

## Skills (스킬)

컨텍스트 키워드에 의해 **자동 활성화**되는 전문 가이드입니다.

| 스킬 | 트리거 키워드 | 설명 |
|------|-------------|------|
| **tdd-workflow** | "구현해줘", "만들어줘", "추가해줘" | TDD Red→Green→Refactor 3단계 개발 가이드 |
| **spring-boot-expert** | "Controller", "Service", "JPA", "Entity" | Spring Boot 3.x + Hexagonal Architecture 패턴 |
| **load-testing** | "부하 테스트", "성능 테스트", "k6" | k6 기반 API 부하 테스트 스크립트 생성/실행 |
| **quality-gate** | "품질 검증", "PR 준비", "배포 전 확인" | 4단계 등급(PASS/CONCERNS/REWORK/FAIL) 품질 검증 |
| **continuous-learning** | "회고", "학습 기록", "패턴 저장" | 세션별 성공/실패 패턴 추출 → 지식 베이스 저장 |
| **youtube-collector** | "유튜브 채널 등록", "영상 수집" | YouTube 채널 등록 → 영상 수집 → 자막 추출 |
| **skill-creator** | "스킬 만들어", "skill 생성" | 새로운 Claude 스킬 생성 가이드 |
| **slash-command-creator** | "커맨드 만들어", "command 생성" | 새로운 슬래시 커맨드 생성 가이드 |
| **hook-creator** | "훅 만들어", "hook 설정" | Claude Code 훅 생성/설정 가이드 |

---

## Commands (커맨드)

`/커맨드명`으로 **수동 실행**하는 워크플로우입니다.

### 개발 워크플로우

| 커맨드 | 사용법 | 설명 |
|--------|--------|------|
| **/dev-cycle** | `/dev-cycle LAD-42` | 3단계 전체 개발 사이클 (설계→TDD→검증+PR) |
| **/agent-teams** | `/agent-teams review` | 여러 에이전트 병렬 실행으로 다각도 분석 |

**`/agent-teams` 프리셋**:
- `review` — code-reviewer + security-sentinel + debugger (PR 전 종합 리뷰)
- `fullstack` — backend-architect + code-reviewer + security-sentinel (설계 검증)
- `quality` — code-reviewer + security-sentinel + load-testing (릴리스 전)
- `debug` — debugger + code-reviewer (복잡한 버그)

### Jira 연동

| 커맨드 | 사용법 | 설명 |
|--------|--------|------|
| **/jira:start** | `/jira:start LAD-42` | 티켓 시작 + 브랜치 생성 + 상태 변경 |
| **/jira:create** | `/jira:create` | 새 Jira 티켓 생성 |
| **/jira:commit** | `/jira:commit LAD-42` | 변경사항 커밋 + Jira 진행 업데이트 |
| **/jira:test** | `/jira:test LAD-42` | 통합 테스트 실행 + 결과 보고 |
| **/jira:complete** | `/jira:complete LAD-42` | 테스트→PR 생성→티켓 완료 |

### 유틸리티

| 커맨드 | 사용법 | 설명 |
|--------|--------|------|
| **/crystalize-prompt** | `/crystalize-prompt` | 프롬프트 압축 (토큰 절약) |

---

## Hooks (훅)

`settings.json`에 설정된 자동 실행 훅입니다.

| 이벤트 | 대상 | 동작 |
|--------|------|------|
| PostToolUse (Edit/Write) | `*.kt` | ktlint 자동 포맷 |
| PostToolUse (Edit/Write) | `*.ts, *.tsx` | Prettier 자동 포맷 |

---

## 추천 워크플로우

### 새 기능 개발 (전체 사이클)
```
/dev-cycle LAD-42
```
→ 1단계(Jira+설계) → 2단계(TDD) → 3단계(리뷰+PR) 자동 진행

### 수동 단계별 개발
```
/jira:start LAD-42          # 티켓 시작 + 브랜치
@backend-architect           # 아키텍처 설계 (tdd-workflow 자동 활성화)
... 구현 ...                 # spring-boot-expert 자동 활성화
/jira:commit LAD-42          # 중간 커밋
@code-reviewer               # 코드 리뷰
@security-sentinel           # 보안 점검
/jira:complete LAD-42        # PR + 완료
```

### PR 전 종합 검증
```
/agent-teams review          # 코드리뷰 + 보안 + 버그 병렬 분석
```

### 성능 이슈 대응
```
@performance-profiler        # 성능 측정 + 분석 + 최적화 제안
```

### 테스트 보강
```
@test-generator              # 누락 테스트 자동 생성
```

### 세션 종료 시
```
회고 기록해줘                  # continuous-learning 자동 활성화
```

---

## 디렉토리 구조

```
.claude/
├── README.md                    # 이 파일
├── settings.json                # 훅 설정 (auto-format)
├── settings.local.json          # 로컬 권한 설정
├── agents/
│   ├── code-reviewer.md         # 코드 리뷰 에이전트
│   ├── debugger.md              # 디버깅 에이전트
│   ├── security-sentinel.md     # 보안 감사 에이전트
│   ├── performance-profiler.md  # 성능 분석 에이전트
│   └── test-generator.md        # 테스트 생성 에이전트
├── commands/
│   ├── dev-cycle.md             # 3단계 개발 사이클
│   ├── agent-teams.md           # 병렬 에이전트 실행
│   ├── crystalize-prompt.md     # 프롬프트 압축
│   └── jira/
│       ├── start.md             # 티켓 시작
│       ├── create.md            # 티켓 생성
│       ├── commit.md            # 커밋 + Jira 업데이트
│       ├── test.md              # 통합 테스트
│       └── complete.md          # PR + 완료
├── skills/
│   ├── tdd-workflow/            # TDD 개발 가이드
│   ├── spring-boot-expert/      # Spring Boot 패턴
│   ├── load-testing/            # k6 부하 테스트
│   ├── quality-gate/            # 품질 게이트
│   ├── continuous-learning/     # 세션 학습
│   ├── youtube-collector/       # 유튜브 수집
│   ├── skill-creator/           # 스킬 생성 가이드
│   ├── slash-command-creator/   # 커맨드 생성 가이드
│   └── hook-creator/            # 훅 생성 가이드
└── learnings/                   # (continuous-learning이 생성)
    ├── patterns.md
    ├── decisions.md
    ├── debugging.md
    └── project-knowledge.md
```
