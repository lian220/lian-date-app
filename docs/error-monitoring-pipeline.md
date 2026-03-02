# 에러 모니터링 자동화 파이프라인 설계 문서 (Slack Bot 중심)

> **프로젝트**: Date Click (데이트 딸깍)
> **작성일**: 2026-03-02
> **상태**: 제안(Proposal)
> **대상 독자**: 백엔드/프론트엔드 엔지니어, DevOps, 팀 리드

---

## 목차

1. [개요](#1-개요)
2. [아키텍처 설계](#2-아키텍처-설계)
3. [Slack Bot 설계 상세](#3-slack-bot-설계-상세)
4. [Sentry 연동 설계 (분석 도구로서)](#4-sentry-연동-설계-분석-도구로서)
5. [Alert Rules 설계](#5-alert-rules-설계)
6. [Jira 티켓 자동 생성 스펙](#6-jira-티켓-자동-생성-스펙)
7. [코드 변경 사항](#7-코드-변경-사항)
8. [구현 로드맵](#8-구현-로드맵)
9. [운영 가이드](#9-운영-가이드)
10. [전문가 패널 리뷰](#10-전문가-패널-리뷰)

---

## 1. 개요

### 1.1 문서 목적

이 문서는 Date Click 서비스의 에러 모니터링 파이프라인을 **Sentry 직접 Slack 웹훅 방식**에서 **Slack Bot 중심 자동화 파이프라인**으로 전환하기 위한 설계 명세이다. 기존 시스템에서 Sentry와 Slack이 독립적으로 동작하던 구조를 개선하여, Slack #error 채널을 단일 허브로 삼고 Slack Bot이 에러 분석, Jira 티켓 생성, 스레드 댓글 보고까지 자동화하는 아키텍처를 상세히 기술한다.

핵심 변경점은 다음과 같다:
- **Slack이 허브**이다. Sentry가 아닌 Slack #error 채널이 모든 에러 정보의 중심이 된다.
- **Slack Bot이 핵심 엔진**이다. 메시지 감지, 분석, Jira 티켓 생성, 결과 보고를 Bot이 수행한다.
- **Sentry는 분석 도구**이다. 에러 알림 허브가 아니라, Bot이 API로 호출하여 상세 분석에 활용하는 도구이다.
- **수동 접수도 처리**한다. 사용자/QA가 직접 작성한 에러 보고("뭐가 안돼요", "확인해주세요")도 Bot이 분류하고 대응한다.

### 1.2 현재 상태 분석 (AS-IS)

#### 현재 에러 처리 흐름

```
에러 발생
  |
  v
GlobalExceptionHandler.handleException()
  |
  |---> logger.error()              (1) 서버 로그 기록
  |---> Sentry.captureException()   (2) Sentry로 전송
  +---> slackNotificationService    (3) Slack으로 직접 전송
       .sendErrorAlert()
```

#### 식별된 문제점

| 번호 | 문제 | 영향 | 심각도 |
|------|------|------|--------|
| P1 | Sentry와 Slack이 독립적으로 동작 | 같은 에러가 두 채널에서 서로 다른 맥락으로 보고됨 | 높음 |
| P2 | 에러 1건 = Slack 메시지 1건 | 동일 에러 100회 발생 시 Slack 메시지 100건 (메시지 폭탄) | 높음 |
| P3 | Slack 메시지에 에러 타입만 포함 | `ex.javaClass.simpleName`만 전송, 스택 트레이스/컨텍스트 부재 | 중간 |
| P4 | Jira 연동 부재 | 에러 발견 후 수동으로 티켓 생성 필요 | 중간 |
| P5 | GitHub 연동 부재 | 어떤 커밋이 에러를 유발했는지 추적 불가 | 중간 |
| P6 | 중복 제거 없음 | Sentry의 이슈 그루핑과 Slack 알림이 분리되어 중복 알림 발생 | 높음 |
| P7 | 환경별 분기 없음 | local 개발 환경에서도 Slack 알림 발송 가능 | 낮음 |
| P8 | 수동 에러 접수 불가 | 사용자/QA가 보고한 문제를 자동으로 처리할 수 없음 | 중간 |

#### 현재 코드의 구체적 문제

**`GlobalExceptionHandler.kt`** (77-85행):

```kotlin
@ExceptionHandler(Exception::class)
fun handleException(ex: Exception): ResponseEntity<ApiResponse<Nothing>> {
    logger.error("Unexpected error occurred", ex)
    Sentry.captureException(ex)                      // 문제: Sentry 전송
    slackNotificationService.sendErrorAlert(ex)       // 문제: Slack 직접 전송
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponse.error("INTERNAL_ERROR", "서버 오류가 발생했습니다"))
}
```

**`SlackNotificationService.kt`**: 자체적으로 Slack Webhook을 호출하여 에러 타입(`ex.javaClass.simpleName`)과 발생 시간만 전송한다. Sentry가 제공하는 이슈 그루핑, 발생 빈도, 영향 사용자 수 등의 정보가 모두 누락된다.

### 1.3 목표 상태 (TO-BE)

#### Slack Bot 중심 에러 처리 흐름

```
+---------------------------------------------------+
|              Slack #error 채널 (허브)                |
|                                                     |
|  자동 에러:  Backend/Sentry --> Webhook --> 메시지   |
|  수동 접수:  사용자/QA가 직접 메시지 작성             |
+------------------------+----------------------------+
                         |
                         v
+---------------------------------------------------+
|              Slack Bot (핵심 엔진)                   |
|                                                     |
|  1. 메시지 감지 (Event Subscription)                |
|  2. 메시지 분류 (자동 에러 vs 수동 접수)             |
|  3. 분석                                            |
|     |-- 코드 에러 --> Sentry API 조회 (상세 분석)    |
|     |-- 사용자 신고 --> AI 분석 (문제 분류/파악)      |
|     +-- GitHub API --> 관련 코드/커밋 조회           |
|  4. Jira 티켓 자동 생성                              |
|  5. Slack 스레드에 분석 결과 댓글                    |
+---------------------------------------------------+
```

#### 핵심 설계 원칙

1. **Slack이 허브(Hub)**: 모든 에러 인지와 커뮤니케이션이 Slack #error 채널을 통해 이루어진다
2. **Bot이 엔진(Engine)**: Slack Bot이 에러 분석, 분류, Jira 연동, 결과 보고를 자동으로 수행한다
3. **Sentry는 분석 도구(Analysis Tool)**: Sentry는 에러 상세 데이터의 저장소이자 분석 도구로, Bot이 API로 호출하여 사용한다
4. **수동+자동 통합**: 코드에서 발생한 자동 에러와 사용자/QA가 직접 보고한 수동 에러를 모두 동일한 파이프라인으로 처리한다
5. **스레드 기반 보고**: 분석 결과가 원본 메시지의 스레드 댓글로 달려서 맥락이 보존된다

### 1.4 기대 효과

| 지표 | 현재 (AS-IS) | 목표 (TO-BE) | 개선율 |
|------|-------------|-------------|--------|
| 에러 인지 시간 (MTTD) | 불규칙 (Slack 확인 시점) | 1분 이내 (Bot 자동 감지) | 대폭 단축 |
| 에러 해결 시간 (MTTR) | 수동 Jira 생성 후 착수 | Bot이 Jira 자동 생성, 분석 결과 즉시 제공 | 40% 이상 단축 |
| 수동 에러 접수 처리 | Slack에서 읽고 수동 대응 | Bot이 자동 분류/분석/티켓 생성 | 신규 자동화 |
| 중복 알림 | 에러 횟수 = 알림 횟수 | Sentry 이슈 그루핑 + Bot 중복 검사 | 90% 이상 감소 |
| 근본 원인 파악 | 수동 스택 트레이스 분석 | Sentry API + AI 자동 분석, 스레드 댓글로 즉시 제공 | 50% 이상 단축 |
| 담당자 배정 | 수동 | Suspect Commit 기반 자동 배정 | 자동화 |
| Slack 메시지 품질 | 에러 타입만 포함 | 분석 결과 스레드 댓글 (원인, Sentry 링크, Jira 링크 등) | 대폭 개선 |

---

## 2. 아키텍처 설계

### 2.1 전체 파이프라인 다이어그램

```
+--------------------------------------------------------------------+
|                      Date Click 서비스                               |
|                                                                      |
|  +-------------------+       +-------------------+                   |
|  |   Spring Boot     |       |    Next.js         |                  |
|  |   Backend         |       |    Frontend        |                  |
|  |                   |       |                    |                  |
|  |  Sentry SDK       |       |  @sentry/nextjs    |                  |
|  |  (자동 캡처)      |       |  (자동 캡처)       |                  |
|  +--------+----------+       +--------+-----------+                  |
|           |                           |                              |
+-----------+---------------------------+------------------------------+
            |                           |
            |  에러 이벤트 전송          |
            v                           v
+--------------------------------------------------------------------+
|                      Sentry Platform                                 |
|  - 이벤트 수집 & 이슈 그루핑                                         |
|  - GitHub 연동 (Code Mapping, Suspect Commits)                       |
|  - Seer AI (근본 원인 분석)                                          |
|  - Alert Rules --> Slack #error 채널로 메시지 전송                    |
+------------------------------+-------------------------------------+
                               |
            Sentry Alert       | Webhook 메시지
                               v
+====================================================================+
||                  Slack #error 채널 (허브)                          ||
||                                                                    ||
||  [자동 에러 메시지]     [수동 접수 메시지]                          ||
||  Sentry Alert 포맷      "코스 생성이 안돼요"                       ||
||  Backend 에러 포맷      "결제 페이지 에러 확인해주세요"             ||
||                         "@bot 이거 확인 부탁"                      ||
+============================+=======================================+
                             |
                             | Event Subscription
                             v
+--------------------------------------------------------------------+
|                      Slack Bot (핵심 엔진)                           |
|                                                                      |
|  +----------------+    +------------------+    +-----------------+   |
|  | Message        |    | Error Analyzer   |    | Thread Reply    |   |
|  | Classifier     |--->|                  |--->| Composer        |   |
|  |                |    | - Sentry API     |    |                 |   |
|  | 자동 에러 vs   |    | - GitHub API     |    | 분석 결과를     |   |
|  | 수동 접수 vs   |    | - AI 분석        |    | 스레드 댓글로   |   |
|  | 무시           |    |                  |    | 작성            |   |
|  +----------------+    +--------+---------+    +-----------------+   |
|                                 |                                    |
|                                 v                                    |
|                        +-----------------+                           |
|                        | Jira Ticket     |                           |
|                        | Creator         |                           |
|                        |                 |                           |
|                        | - 티켓 생성     |                           |
|                        | - 중복 방지     |                           |
|                        | - 담당자 배정   |                           |
|                        +-----------------+                           |
+--------------------------------------------------------------------+
            |                         |
            v                         v
   +-----------------+       +-----------------+
   |   Jira           |       |   GitHub         |
   |                  |       |                  |
   |  LAD 프로젝트    |       |  Suspect Commits |
   |  - 티켓 생성     |       |  - 코드 라인 조회 |
   |  - 담당자 배정   |       |  - 커밋 이력     |
   |  - 우선순위      |       |                  |
   +-----------------+       +-----------------+
```

### 2.2 각 도구의 역할 재정의

#### Slack: 에러 인지 허브 + 커뮤니케이션 채널

| 역할 | 설명 |
|------|------|
| 에러 메시지 수신 | Sentry Alert, Backend Webhook 등 자동 에러 메시지를 수신한다 |
| 수동 접수 접수 | 사용자/QA가 직접 에러를 보고하는 메시지를 수신한다 |
| 분석 결과 공유 | Bot이 스레드 댓글로 분석 결과를 공유한다 |
| 팀 커뮤니케이션 | 에러 관련 논의가 스레드 내에서 이루어진다 |

#### Slack Bot: 분석 엔진 + 자동화 엔진

| 역할 | 설명 |
|------|------|
| 메시지 감지 | #error 채널의 모든 메시지를 Event Subscription으로 감지한다 |
| 메시지 분류 | 자동 에러, 수동 접수, 무시 대상을 구분한다 |
| 에러 분석 | Sentry API, GitHub API, AI를 활용하여 에러를 분석한다 |
| Jira 생성 | 분석 결과를 기반으로 Jira 티켓을 자동 생성한다 |
| 결과 보고 | 분석 결과를 원본 메시지의 스레드 댓글로 작성한다 |

#### Sentry: 에러 상세 분석 도구 (API로만 사용)

| 역할 | 설명 |
|------|------|
| 에러 수집 | SDK를 통해 Backend/Frontend 에러를 자동 수집한다 |
| 이슈 그루핑 | 동일 에러를 하나의 이슈로 병합하여 중복을 제거한다 |
| Slack 알림 전송 | Alert Rule을 통해 #error 채널에 에러 메시지를 전송한다 (알림 원본) |
| API 제공 | Bot이 호출하여 이슈 상세, 스택 트레이스, 발생 횟수 등을 조회한다 |
| GitHub 연동 | Code Mapping, Suspect Commits 등 코드 분석 기능을 제공한다 |
| AI 분석 | Seer를 통해 근본 원인 분석과 Autofix를 제안한다 |

#### Jira: 이슈 추적 + 해결 관리

| 역할 | 설명 |
|------|------|
| 티켓 관리 | Bot이 생성한 에러 티켓의 라이프사이클을 관리한다 |
| 작업 추적 | 에러 해결 과정을 Sprint 내에서 추적한다 |
| 우선순위 관리 | Bot이 판정한 우선순위에 따라 대응 순서를 결정한다 |

#### GitHub: 소스 코드 분석 + Suspect Commit 조회

| 역할 | 설명 |
|------|------|
| Suspect Commits | Sentry를 통해 에러를 유발한 커밋을 식별한다 |
| 코드 라인 조회 | Bot이 GitHub API로 관련 코드를 직접 조회한다 |
| 커밋 이력 | 에러 발생 파일의 최근 변경 이력을 추적한다 |

### 2.3 데이터 흐름 상세

#### Case 1: Backend 코드 에러

```
[Spring Boot 요청 처리]
       |
       v
[예외 발생] --> [Sentry SDK 자동 캡처]
                      |
                      v
               [Sentry Platform]
               - 이슈 그루핑
               - Suspect Commits 계산
               - Seer AI 분석
                      |
                      v
               [Sentry Alert Rule 트리거]
                      |
                      v
               [Slack #error 채널에 메시지 전송]
               "NullPointerException in CourseService.kt:142"
                      |
                      v
               [Slack Bot 메시지 감지]
                      |
                      v
               [Bot: 메시지 분류 --> "자동 에러 (Sentry 포맷)"]
                      |
                      v
               [Bot: Sentry API 호출]
               - 이슈 상세 조회
               - 스택 트레이스 가져오기
               - 발생 횟수, 영향 사용자 수
               - Suspect Commits 조회
               - Seer AI 분석 결과 조회
                      |
                      v
               [Bot: GitHub API 호출]
               - Suspect Commit의 코드 변경 내역
               - 커밋 작성자 정보
                      |
                      v
               [Bot: Jira API 호출]
               - 중복 티켓 확인 (Sentry Issue ID 기반)
               - 신규 티켓 생성 (또는 기존 티켓 업데이트)
               - 담당자 배정 (Suspect Author 기반)
                      |
                      v
               [Bot: Slack 스레드 댓글 작성]
               분석 결과를 원본 메시지의 스레드에 댓글로 작성
```

#### Case 2: 사용자/QA 수동 접수

```
[사용자/QA가 Slack #error 채널에 메시지 작성]
"코스 생성이 안돼요, 강남역 선택하면 계속 로딩만 돌아요"
       |
       v
[Slack Bot 메시지 감지]
       |
       v
[Bot: 메시지 분류 --> "수동 접수 (에러 키워드 감지)"]
       |
       v
[Bot: AI 분석 (Claude API)]
- 메시지에서 문제 유형 분류 (코스 생성 실패)
- 영향 기능 추정 (데이트 코스 생성)
- 관련 키워드 추출 (강남역, 로딩, 코스 생성)
       |
       v
[Bot: Sentry API 호출]
- 최근 관련 에러 검색 (CourseService, OpenAI 등)
- 유사 이슈 조회 (키워드 기반)
- 최근 30분 에러 통계
       |
       v
[Bot: Jira API 호출]
- 유사 티켓 존재 여부 확인
- 신규 티켓 생성 (수동 접수용 템플릿)
- 담당자: 미배정 (Triage 필요)
       |
       v
[Bot: Slack 스레드 댓글 작성]
분석 결과를 원본 메시지의 스레드에 댓글로 작성
```

#### Case 3: Frontend Sentry 에러

```
[Next.js 클라이언트 에러 발생]
       |
       v
[@sentry/nextjs SDK 자동 캡처]
       |
       v
[Sentry Platform]
- 이슈 그루핑
- Session Replay 연결
- Source Map으로 원본 코드 매핑
       |
       v
[Sentry Alert Rule 트리거]
       |
       v
[Slack #error 채널에 메시지 전송]
"TypeError: Cannot read properties of undefined (reading 'map')"
       |
       v
[Slack Bot 메시지 감지]
       |
       v
(이후 Case 1과 동일한 흐름:
 Sentry API 조회 --> GitHub API 조회 --> Jira 생성 --> 스레드 댓글)
```

---

## 3. Slack Bot 설계 상세

### 3.1 기술 스택 선택

#### 권장: Node.js + Bolt for Slack

| 항목 | 선택 | 근거 |
|------|------|------|
| 런타임 | Node.js 20 LTS | Slack SDK 생태계가 가장 풍부, Bolt 공식 지원 |
| 프레임워크 | Bolt for Slack | Slack 공식 프레임워크, Event Subscription/Socket Mode 지원 |
| 언어 | TypeScript | 타입 안전성, 프론트엔드 팀과 기술 공유 |
| 배포 | Docker (별도 컨테이너) | 기존 docker-compose에 추가, 독립 배포 가능 |

#### 대안: Spring Boot 추가 모듈

기존 Spring Boot 백엔드에 모듈로 통합하는 방식도 가능하나, 다음 이유로 별도 마이크로서비스를 권장한다:

| 비교 항목 | Node.js (Bolt) | Spring Boot 모듈 |
|-----------|----------------|-------------------|
| Slack SDK 지원 | 공식 Bolt for Slack (최우선 지원) | jslack 등 3rd-party 라이브러리 |
| 개발 속도 | Slack 개발에 최적화됨 | 일반 웹 프레임워크 위에 추가 구현 필요 |
| 배포 독립성 | Bot 장애가 백엔드에 영향 없음 | 백엔드 배포 시 Bot도 함께 재시작 |
| 리소스 사용 | 경량 (메모리 약 64-128MB) | JVM 오버헤드 (메모리 약 256MB+) |
| Slack 생태계 | Block Kit, Modals 등 공식 지원 | 직접 JSON 구성 필요 |

### 3.2 Slack App 설정

#### Bot Token Scopes 필요 목록

```yaml
bot_token_scopes:
  # 채널 메시지 읽기 (Event Subscription에 필요)
  - channels:history
  - channels:read

  # 메시지 작성 (스레드 댓글 작성에 필요)
  - chat:write

  # 채널 멤버 조회 (멘션에 필요)
  - users:read

  # 리액션 추가 (처리 상태 표시에 필요)
  - reactions:write
  - reactions:read

  # 파일 업로드 (스크린샷 등 첨부 시)
  - files:write
```

#### Event Subscriptions 설정

```yaml
event_subscriptions:
  # 구독할 이벤트 목록
  events:
    - message.channels    # 공개 채널 메시지 수신

  # 수신 대상 채널 제한 (Bot이 설치된 채널만)
  # --> Slack App 설정에서 Bot을 #error 채널에만 추가하여 범위 제한
```

#### Socket Mode vs HTTP Endpoint

| 방식 | 장점 | 단점 | 권장 여부 |
|------|------|------|-----------|
| Socket Mode | 방화벽/NAT 뒤에서 동작, 별도 URL 불필요 | WebSocket 연결 유지 필요, 스케일링 제한 | 개발/소규모 팀 권장 |
| HTTP Endpoint | 스케일링 용이, 로드밸런서 가능 | 공인 URL 필요, SSL 인증서 필요 | 대규모 운영 시 |

**이 프로젝트에서는 Socket Mode를 권장한다.** 2-3명 팀에서 별도 공인 URL을 관리할 필요가 없고, Docker 내부에서 안정적으로 동작한다.

### 3.3 프로젝트 구조

```
slack-error-bot/
|-- src/
|   |-- app.ts                    # 엔트리 포인트
|   |-- config/
|   |   +-- env.ts                # 환경변수 설정
|   |-- listeners/
|   |   +-- messageHandler.ts     # 메시지 이벤트 핸들러
|   |-- classifiers/
|   |   +-- messageClassifier.ts  # 메시지 분류기
|   |-- analyzers/
|   |   |-- sentryAnalyzer.ts     # Sentry API 연동 분석
|   |   |-- githubAnalyzer.ts     # GitHub API 연동 분석
|   |   +-- aiAnalyzer.ts         # AI 기반 분석
|   |-- integrations/
|   |   |-- sentryClient.ts       # Sentry API 클라이언트
|   |   |-- jiraClient.ts         # Jira API 클라이언트
|   |   +-- githubClient.ts       # GitHub API 클라이언트
|   |-- composers/
|   |   +-- threadReplyComposer.ts # 스레드 댓글 구성
|   +-- types/
|       +-- index.ts              # 타입 정의
|-- Dockerfile
|-- package.json
|-- tsconfig.json
+-- .env.example
```

### 3.4 핵심 코드: 앱 엔트리 포인트

```typescript
// src/app.ts
import { App, LogLevel } from '@slack/bolt';
import { handleMessage } from './listeners/messageHandler';
import { config } from './config/env';

const app = new App({
  token: config.SLACK_BOT_TOKEN,
  appToken: config.SLACK_APP_TOKEN,  // Socket Mode용
  socketMode: true,
  logLevel: LogLevel.INFO,
});

// #error 채널 메시지 이벤트 구독
app.message(async ({ message, client, logger }) => {
  try {
    // Bot 자신의 메시지는 무시 (무한 루프 방지)
    if ('bot_id' in message) return;

    // #error 채널의 메시지만 처리
    if (message.channel !== config.ERROR_CHANNEL_ID) return;

    await handleMessage({ message, client, logger });
  } catch (error) {
    logger.error('메시지 처리 중 오류 발생:', error);

    // Bot 자체 에러도 스레드로 보고
    if ('ts' in message) {
      await client.chat.postMessage({
        channel: message.channel,
        thread_ts: message.ts,
        text: ':warning: Bot 분석 중 오류가 발생했습니다. 수동 확인이 필요합니다.',
      });
    }
  }
});

(async () => {
  await app.start();
  console.log('Error Monitor Bot이 시작되었습니다.');
})();
```

### 3.5 메시지 분류 로직

#### 분류기 구현

```typescript
// src/classifiers/messageClassifier.ts

export type MessageType = 'sentry_alert' | 'manual_report' | 'ignore';

interface ClassificationResult {
  type: MessageType;
  confidence: number;
  metadata: {
    sentryIssueId?: string;
    sentryIssueUrl?: string;
    keywords?: string[];
    reportedProblem?: string;
  };
}

// Sentry Alert 메시지 감지 패턴
const SENTRY_PATTERNS = [
  /sentry\.io\/issues\/(\d+)/,           // Sentry 이슈 링크
  /\[Sentry\]/i,                         // [Sentry] 접두사
  /DATECLICK-\w+-\w+/,                   // Sentry 이슈 키
  /first seen|regression detected/i,      // Sentry Alert 키워드
];

// 수동 접수 키워드 (한국어)
const MANUAL_REPORT_KEYWORDS = [
  '안돼요', '안되요', '안됩니다', '안 돼요', '안 되요',
  '에러', '오류', '버그', '장애',
  '확인해주세요', '확인 부탁', '확인해 주세요',
  '동작 안', '작동 안', '안 됨', '안됨',
  '실패', '로딩', '무한 로딩', '먹통',
  '깨짐', '안 나와요', '안나와요', '안 보여요', '안보여요',
  '느려요', '느립니다', '응답 없',
  '500', '404', '에러 코드',
];

// 무시 대상 패턴
const IGNORE_PATTERNS = [
  /^$/,                                   // 빈 메시지
  /^\s*$/,                                // 공백만 있는 메시지
  /joined the channel/i,                  // 채널 입장 메시지
  /set the channel/i,                     // 채널 설정 메시지
];

export function classifyMessage(text: string): ClassificationResult {
  // 1단계: 무시 대상 확인
  for (const pattern of IGNORE_PATTERNS) {
    if (pattern.test(text)) {
      return { type: 'ignore', confidence: 1.0, metadata: {} };
    }
  }

  // 2단계: Sentry Alert 패턴 확인
  for (const pattern of SENTRY_PATTERNS) {
    const match = text.match(pattern);
    if (match) {
      const issueUrlMatch = text.match(/https:\/\/[^\s]*sentry\.io\/issues\/(\d+)/);
      return {
        type: 'sentry_alert',
        confidence: 0.95,
        metadata: {
          sentryIssueId: issueUrlMatch?.[1],
          sentryIssueUrl: issueUrlMatch?.[0],
        },
      };
    }
  }

  // 3단계: 수동 접수 키워드 확인
  const matchedKeywords = MANUAL_REPORT_KEYWORDS.filter(
    (keyword) => text.includes(keyword)
  );

  if (matchedKeywords.length > 0) {
    return {
      type: 'manual_report',
      confidence: Math.min(0.5 + matchedKeywords.length * 0.15, 0.95),
      metadata: {
        keywords: matchedKeywords,
        reportedProblem: text,
      },
    };
  }

  // 4단계: 분류 불가 --> 무시 (노이즈 방지)
  // confidence가 낮은 경우 무시 처리하여 모든 메시지에 반응하는 것을 방지
  return { type: 'ignore', confidence: 0.3, metadata: {} };
}
```

#### 분류 불가 메시지 처리 전략

```
메시지 수신
    |
    v
분류 가능한가?
    |
    |-- Sentry Alert 포맷 --> "sentry_alert" (confidence >= 0.9)
    |-- 에러 키워드 포함  --> "manual_report" (confidence >= 0.5)
    +-- 어디에도 해당 안됨 --> "ignore"
         |
         v
    무시 (스레드 댓글 없음)
    Bot이 모든 메시지에 반응하면 노이즈가 됨.
    에러 관련 키워드가 없는 일반 대화는 무시한다.
```

### 3.6 메시지 핸들러

```typescript
// src/listeners/messageHandler.ts
import { classifyMessage } from '../classifiers/messageClassifier';
import { analyzeSentryError } from '../analyzers/sentryAnalyzer';
import { analyzeManualReport } from '../analyzers/aiAnalyzer';
import { createJiraTicket } from '../integrations/jiraClient';
import { composeAutoErrorReply, composeManualReportReply } from '../composers/threadReplyComposer';

interface MessageContext {
  message: any;
  client: any;
  logger: any;
}

export async function handleMessage({ message, client, logger }: MessageContext) {
  const text = message.text || '';
  const classification = classifyMessage(text);

  logger.info(`메시지 분류: ${classification.type} (confidence: ${classification.confidence})`);

  if (classification.type === 'ignore') {
    return; // 무시 대상은 처리하지 않음
  }

  // 처리 시작 리액션 추가 (eyes 이모지)
  await client.reactions.add({
    channel: message.channel,
    timestamp: message.ts,
    name: 'eyes',
  });

  try {
    let analysisResult;
    let jiraResult;
    let replyBlocks;

    if (classification.type === 'sentry_alert') {
      // 자동 에러: Sentry API로 상세 분석
      analysisResult = await analyzeSentryError(classification.metadata.sentryIssueId!);
      jiraResult = await createJiraTicket({
        type: 'auto_error',
        analysis: analysisResult,
      });
      replyBlocks = composeAutoErrorReply(analysisResult, jiraResult);

    } else if (classification.type === 'manual_report') {
      // 수동 접수: AI 분석 + Sentry 유사 이슈 검색
      analysisResult = await analyzeManualReport(text);
      jiraResult = await createJiraTicket({
        type: 'manual_report',
        analysis: analysisResult,
      });
      replyBlocks = composeManualReportReply(analysisResult, jiraResult);
    }

    // 스레드 댓글로 분석 결과 작성
    await client.chat.postMessage({
      channel: message.channel,
      thread_ts: message.ts,
      blocks: replyBlocks,
      text: '분석 결과가 준비되었습니다.', // fallback text
    });

    // 처리 완료 리액션 (체크 이모지)
    await client.reactions.add({
      channel: message.channel,
      timestamp: message.ts,
      name: 'white_check_mark',
    });

    // 처리 시작 리액션 제거
    await client.reactions.remove({
      channel: message.channel,
      timestamp: message.ts,
      name: 'eyes',
    });

  } catch (error) {
    logger.error('분석 처리 실패:', error);

    // 에러 리액션
    await client.reactions.add({
      channel: message.channel,
      timestamp: message.ts,
      name: 'x',
    });

    throw error;
  }
}
```

### 3.7 분석 엔진

#### Sentry API 연동

```typescript
// src/analyzers/sentryAnalyzer.ts
import { sentryClient } from '../integrations/sentryClient';
import { githubClient } from '../integrations/githubClient';

interface SentryAnalysisResult {
  issueId: string;
  issueUrl: string;
  errorType: string;
  errorMessage: string;
  firstSeen: string;
  lastSeen: string;
  eventCount: number;
  userCount: number;
  level: string;
  stackTrace: string;
  suspectCommits: Array<{
    commitHash: string;
    author: string;
    message: string;
    timestamp: string;
  }>;
  seerAnalysis?: {
    rootCause: string;
    suggestedFix: string;
  };
  relatedSentryIssues: string[];
}

export async function analyzeSentryError(issueId: string): Promise<SentryAnalysisResult> {
  // 1. Sentry 이슈 상세 조회
  const issue = await sentryClient.getIssue(issueId);

  // 2. 최신 이벤트의 스택 트레이스 조회
  const latestEvent = await sentryClient.getLatestEvent(issueId);
  const stackTrace = extractTopFrames(latestEvent, 5);

  // 3. Suspect Commits 조회
  const commits = await sentryClient.getSuspectCommits(issueId);

  // 4. Seer AI 분석 결과 조회 (있는 경우)
  const seerResult = await sentryClient.getSeerAnalysis(issueId);

  return {
    issueId,
    issueUrl: `https://sfn-oh.sentry.io/issues/${issueId}/`,
    errorType: issue.metadata?.type || issue.title,
    errorMessage: issue.culprit || '',
    firstSeen: issue.firstSeen,
    lastSeen: issue.lastSeen,
    eventCount: issue.count,
    userCount: issue.userCount,
    level: issue.level,
    stackTrace,
    suspectCommits: commits.map((c: any) => ({
      commitHash: c.id.substring(0, 8),
      author: c.author?.name || 'unknown',
      message: c.message,
      timestamp: c.dateCreated,
    })),
    seerAnalysis: seerResult ? {
      rootCause: seerResult.rootCause,
      suggestedFix: seerResult.suggestedFix,
    } : undefined,
    relatedSentryIssues: [],
  };
}

function extractTopFrames(event: any, count: number): string {
  const frames = event?.entries
    ?.find((e: any) => e.type === 'exception')
    ?.data?.values?.[0]?.stacktrace?.frames
    ?.slice(-count)
    ?.reverse();

  if (!frames) return '스택 트레이스를 가져올 수 없습니다.';

  return frames
    .map((f: any) => `  at ${f.function || '?'} (${f.filename}:${f.lineNo})`)
    .join('\n');
}
```

#### Sentry API 클라이언트

```typescript
// src/integrations/sentryClient.ts
import { config } from '../config/env';

const SENTRY_BASE_URL = 'https://sentry.io/api/0';
const headers = {
  Authorization: `Bearer ${config.SENTRY_AUTH_TOKEN}`,
  'Content-Type': 'application/json',
};

export const sentryClient = {
  async getIssue(issueId: string) {
    const res = await fetch(`${SENTRY_BASE_URL}/issues/${issueId}/`, { headers });
    if (!res.ok) throw new Error(`Sentry API 오류: ${res.status}`);
    return res.json();
  },

  async getLatestEvent(issueId: string) {
    const res = await fetch(`${SENTRY_BASE_URL}/issues/${issueId}/events/latest/`, { headers });
    if (!res.ok) throw new Error(`Sentry API 오류: ${res.status}`);
    return res.json();
  },

  async getSuspectCommits(issueId: string) {
    const res = await fetch(`${SENTRY_BASE_URL}/issues/${issueId}/committers/`, { headers });
    if (!res.ok) return [];
    const data = await res.json();
    return data.committers?.flatMap((c: any) => c.commits) || [];
  },

  async getSeerAnalysis(issueId: string) {
    try {
      const res = await fetch(
        `${SENTRY_BASE_URL}/issues/${issueId}/autofix/`,
        { headers }
      );
      if (!res.ok) return null;
      return res.json();
    } catch {
      return null; // Seer가 비활성화되어 있거나 분석이 없는 경우
    }
  },

  async searchIssues(query: string, project: string) {
    const res = await fetch(
      `${SENTRY_BASE_URL}/organizations/${config.SENTRY_ORG}/issues/?query=${encodeURIComponent(query)}&project=${project}`,
      { headers }
    );
    if (!res.ok) return [];
    return res.json();
  },
};
```

#### AI 분석 (수동 접수용)

```typescript
// src/analyzers/aiAnalyzer.ts
import { sentryClient } from '../integrations/sentryClient';
import { config } from '../config/env';

interface ManualReportAnalysis {
  problemCategory: string;
  affectedFeature: string;
  relatedSentryIssues: Array<{
    issueId: string;
    title: string;
    url: string;
    similarity: string;
  }>;
  initialAnalysis: string;
  suggestedPriority: 'P0' | 'P1' | 'P2' | 'P3';
}

export async function analyzeManualReport(reportText: string): Promise<ManualReportAnalysis> {
  // 1. AI로 문제 분류 및 키워드 추출
  const aiResult = await callAI(reportText);

  // 2. 추출된 키워드로 Sentry에서 유사 이슈 검색
  const relatedIssues = await searchRelatedSentryIssues(aiResult.keywords);

  // 3. 최근 에러 통계 조회
  const recentErrors = await getRecentErrorStats(aiResult.affectedFeature);

  return {
    problemCategory: aiResult.category,
    affectedFeature: aiResult.affectedFeature,
    relatedSentryIssues: relatedIssues,
    initialAnalysis: buildAnalysisText(aiResult, recentErrors),
    suggestedPriority: determinePriority(relatedIssues, recentErrors),
  };
}

async function callAI(text: string) {
  // Claude API 또는 OpenAI API 호출
  const response = await fetch('https://api.anthropic.com/v1/messages', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'x-api-key': config.AI_API_KEY,
      'anthropic-version': '2023-06-01',
    },
    body: JSON.stringify({
      model: 'claude-sonnet-4-20250514',
      max_tokens: 500,
      messages: [{
        role: 'user',
        content: `다음 에러 보고를 분석하여 JSON으로 응답해주세요.

에러 보고: "${text}"

응답 형식:
{
  "category": "문제 분류 (코스 생성 실패/결제 오류/페이지 로딩 실패/기타)",
  "affectedFeature": "영향받는 기능명",
  "keywords": ["검색에 사용할 기술 키워드"],
  "severity": "high/medium/low",
  "summary": "문제 요약 (1줄)"
}`,
      }],
    }),
  });

  const data = await response.json();
  return JSON.parse(data.content[0].text);
}

async function searchRelatedSentryIssues(keywords: string[]) {
  const results = [];
  for (const keyword of keywords.slice(0, 3)) {
    const issues = await sentryClient.searchIssues(
      keyword,
      config.SENTRY_BACKEND_PROJECT
    );
    for (const issue of issues.slice(0, 2)) {
      results.push({
        issueId: issue.id,
        title: issue.title,
        url: `https://sfn-oh.sentry.io/issues/${issue.id}/`,
        similarity: '키워드 일치',
      });
    }
  }
  return results;
}

async function getRecentErrorStats(feature: string) {
  // 최근 30분 관련 에러 수 조회
  const query = `${feature} is:unresolved`;
  const issues = await sentryClient.searchIssues(query, config.SENTRY_BACKEND_PROJECT);
  return {
    count: issues.length,
    issues: issues.slice(0, 5),
  };
}

function buildAnalysisText(
  aiResult: any,
  recentErrors: { count: number; issues: any[] }
): string {
  let text = '';
  if (recentErrors.count > 0) {
    text += `최근 30분 내 ${aiResult.affectedFeature} 관련 에러 ${recentErrors.count}건 감지.\n`;
    text += recentErrors.issues.map((i: any) => `- ${i.title}`).join('\n');
  } else {
    text += `Sentry에서 관련 에러를 찾지 못했습니다. 재현 확인이 필요합니다.`;
  }
  return text;
}

function determinePriority(
  relatedIssues: any[],
  recentErrors: { count: number }
): 'P0' | 'P1' | 'P2' | 'P3' {
  if (recentErrors.count >= 10) return 'P1';
  if (relatedIssues.length > 0) return 'P2';
  return 'P3';
}
```

### 3.8 Jira 연동

```typescript
// src/integrations/jiraClient.ts
import { config } from '../config/env';

const JIRA_BASE_URL = `https://${config.JIRA_HOST}/rest/api/3`;
const headers = {
  Authorization: `Basic ${Buffer.from(`${config.JIRA_EMAIL}:${config.JIRA_API_TOKEN}`).toString('base64')}`,
  'Content-Type': 'application/json',
};

interface JiraTicketResult {
  ticketKey: string;
  ticketUrl: string;
  isNew: boolean;
  assignee?: string;
  priority: string;
}

interface CreateTicketOptions {
  type: 'auto_error' | 'manual_report';
  analysis: any;
}

export async function createJiraTicket(options: CreateTicketOptions): Promise<JiraTicketResult> {
  // 1. 중복 티켓 확인
  const existingTicket = await findExistingTicket(options);
  if (existingTicket) {
    // 기존 티켓이 있으면 코멘트만 추가
    await addComment(existingTicket.key, buildCommentText(options));
    return {
      ticketKey: existingTicket.key,
      ticketUrl: `https://${config.JIRA_HOST}/browse/${existingTicket.key}`,
      isNew: false,
      priority: existingTicket.fields.priority.name,
    };
  }

  // 2. 신규 티켓 생성
  const ticketBody = options.type === 'auto_error'
    ? buildAutoErrorTicket(options.analysis)
    : buildManualReportTicket(options.analysis);

  const res = await fetch(`${JIRA_BASE_URL}/issue`, {
    method: 'POST',
    headers,
    body: JSON.stringify(ticketBody),
  });

  if (!res.ok) {
    const error = await res.text();
    throw new Error(`Jira 티켓 생성 실패: ${res.status} - ${error}`);
  }

  const data = await res.json();

  return {
    ticketKey: data.key,
    ticketUrl: `https://${config.JIRA_HOST}/browse/${data.key}`,
    isNew: true,
    assignee: ticketBody.fields.assignee?.accountId,
    priority: ticketBody.fields.priority.name,
  };
}

// 중복 티켓 방지: Sentry Issue ID 기반 검색
async function findExistingTicket(options: CreateTicketOptions) {
  let jql = '';
  if (options.type === 'auto_error' && options.analysis.issueId) {
    jql = `project = LAD AND labels = "sentry" AND labels = "sentry-${options.analysis.issueId}" AND status != Done`;
  }
  if (!jql) return null;

  const res = await fetch(
    `${JIRA_BASE_URL}/search?jql=${encodeURIComponent(jql)}&maxResults=1`,
    { headers }
  );
  if (!res.ok) return null;
  const data = await res.json();
  return data.issues?.[0] || null;
}

// 자동 에러용 티켓 템플릿
function buildAutoErrorTicket(analysis: any) {
  return {
    fields: {
      project: { key: 'LAD' },
      issuetype: { name: 'Bug' },
      summary: `[Sentry] ${analysis.errorType}: ${analysis.errorMessage.substring(0, 80)}`,
      description: {
        type: 'doc',
        version: 1,
        content: [
          {
            type: 'heading',
            attrs: { level: 2 },
            content: [{ type: 'text', text: '에러 요약' }],
          },
          {
            type: 'bulletList',
            content: [
              makeListItem(`에러 타입: ${analysis.errorType}`),
              makeListItem(`발생 횟수: ${analysis.eventCount}회`),
              makeListItem(`영향 사용자: ${analysis.userCount}명`),
              makeListItem(`최초 발생: ${analysis.firstSeen}`),
              makeListItem(`최근 발생: ${analysis.lastSeen}`),
            ],
          },
          {
            type: 'heading',
            attrs: { level: 2 },
            content: [{ type: 'text', text: 'Sentry 이슈' }],
          },
          {
            type: 'paragraph',
            content: [{
              type: 'text',
              text: analysis.issueUrl,
              marks: [{ type: 'link', attrs: { href: analysis.issueUrl } }],
            }],
          },
          {
            type: 'heading',
            attrs: { level: 2 },
            content: [{ type: 'text', text: '스택 트레이스 (상위 5프레임)' }],
          },
          {
            type: 'codeBlock',
            content: [{ type: 'text', text: analysis.stackTrace }],
          },
        ],
      },
      priority: { name: mapPriority(analysis) },
      labels: ['auto-created', 'sentry', `sentry-${analysis.issueId}`],
      assignee: analysis.suspectCommits?.[0]
        ? { accountId: resolveJiraAccountId(analysis.suspectCommits[0].author) }
        : undefined,
    },
  };
}

// 수동 접수용 티켓 템플릿
function buildManualReportTicket(analysis: any) {
  return {
    fields: {
      project: { key: 'LAD' },
      issuetype: { name: 'Bug' },
      summary: `[수동접수] ${analysis.problemCategory}: ${analysis.affectedFeature}`,
      description: {
        type: 'doc',
        version: 1,
        content: [
          {
            type: 'heading',
            attrs: { level: 2 },
            content: [{ type: 'text', text: '접수 내용' }],
          },
          {
            type: 'paragraph',
            content: [{ type: 'text', text: analysis.reportedProblem || '상세 내용은 Slack 스레드를 참고하세요.' }],
          },
          {
            type: 'heading',
            attrs: { level: 2 },
            content: [{ type: 'text', text: 'Bot 초기 분석' }],
          },
          {
            type: 'paragraph',
            content: [{ type: 'text', text: analysis.initialAnalysis }],
          },
          {
            type: 'heading',
            attrs: { level: 2 },
            content: [{ type: 'text', text: '관련 Sentry 이슈' }],
          },
          {
            type: 'bulletList',
            content: analysis.relatedSentryIssues.map((issue: any) =>
              makeListItem(`${issue.title} - ${issue.url}`)
            ),
          },
        ],
      },
      priority: { name: analysis.suggestedPriority === 'P1' ? 'High' : 'Medium' },
      labels: ['auto-created', 'manual-report', 'triage-needed'],
      // 수동 접수는 담당자 미배정 (Triage 필요)
    },
  };
}

function makeListItem(text: string) {
  return {
    type: 'listItem',
    content: [{
      type: 'paragraph',
      content: [{ type: 'text', text }],
    }],
  };
}

function mapPriority(analysis: any): string {
  if (analysis.level === 'fatal') return 'Highest';
  if (analysis.eventCount >= 50 && analysis.userCount >= 10) return 'High';
  if (analysis.eventCount >= 10 || analysis.userCount >= 5) return 'Medium';
  return 'Low';
}

function resolveJiraAccountId(gitAuthor: string): string | undefined {
  // GitHub 사용자명 --> Jira 계정 ID 매핑
  // 실제 구현 시 환경변수나 설정 파일로 매핑 테이블 관리
  const mapping: Record<string, string> = {
    // 'github-username': 'jira-account-id',
  };
  return mapping[gitAuthor];
}

async function addComment(issueKey: string, commentText: string) {
  await fetch(`${JIRA_BASE_URL}/issue/${issueKey}/comment`, {
    method: 'POST',
    headers,
    body: JSON.stringify({
      body: {
        type: 'doc',
        version: 1,
        content: [{
          type: 'paragraph',
          content: [{ type: 'text', text: commentText }],
        }],
      },
    }),
  });
}

function buildCommentText(options: CreateTicketOptions): string {
  if (options.type === 'auto_error') {
    return `[Bot 업데이트] Sentry에서 동일 이슈가 다시 감지되었습니다.\n발생 횟수: ${options.analysis.eventCount}회\n영향 사용자: ${options.analysis.userCount}명`;
  }
  return `[Bot 업데이트] 수동 접수를 통해 추가 보고가 접수되었습니다.`;
}
```

### 3.9 스레드 댓글 포맷

#### 자동 에러용 댓글 (Block Kit)

Slack 스레드에 작성되는 분석 결과 메시지의 포맷이다.

```
+--------------------------------------------------+
| :mag: 분석 완료                                    |
|                                                    |
| :pushpin: 에러 유형: NullPointerException           |
| :round_pushpin: 발생 위치: CourseService.kt:142     |
| :fire: 발생 횟수: 37회 / 영향: 12 users             |
| :link: Sentry: https://sentry.io/issues/...        |
|                                                    |
| :bulb: 원인 분석:                                   |
| places 리스트가 빈 상태에서 인덱스 접근.             |
| CreateCourseUseCase에서 빈 places 검증 누락 추정.   |
|                                                    |
| :ticket: Jira 티켓: LAD-123 생성됨                  |
| :bust_in_silhouette: 담당자: @imdoyeong            |
|            (Suspect Commit 기반)                    |
| :label: 우선순위: P1 (High)                         |
+--------------------------------------------------+
```

#### Block Kit JSON (자동 에러용)

```typescript
// src/composers/threadReplyComposer.ts

export function composeAutoErrorReply(analysis: any, jiraResult: any) {
  const blocks: any[] = [
    {
      type: 'header',
      text: {
        type: 'plain_text',
        text: ':mag: 분석 완료',
      },
    },
    {
      type: 'section',
      fields: [
        {
          type: 'mrkdwn',
          text: `:pushpin: *에러 유형*\n${analysis.errorType}`,
        },
        {
          type: 'mrkdwn',
          text: `:round_pushpin: *발생 위치*\n${analysis.errorMessage}`,
        },
        {
          type: 'mrkdwn',
          text: `:fire: *발생 횟수*\n${analysis.eventCount}회`,
        },
        {
          type: 'mrkdwn',
          text: `:busts_in_silhouette: *영향 사용자*\n${analysis.userCount}명`,
        },
      ],
    },
    {
      type: 'section',
      text: {
        type: 'mrkdwn',
        text: `:link: *Sentry*: <${analysis.issueUrl}|이슈 상세 보기>`,
      },
    },
    { type: 'divider' },
  ];

  // 원인 분석 (Seer AI 결과가 있는 경우)
  if (analysis.seerAnalysis) {
    blocks.push({
      type: 'section',
      text: {
        type: 'mrkdwn',
        text: `:bulb: *원인 분석 (AI)*\n${analysis.seerAnalysis.rootCause}`,
      },
    });
  }

  // 스택 트레이스 (축약)
  blocks.push({
    type: 'section',
    text: {
      type: 'mrkdwn',
      text: `:page_facing_up: *스택 트레이스*\n\`\`\`\n${analysis.stackTrace}\n\`\`\``,
    },
  });

  // Suspect Commits
  if (analysis.suspectCommits?.length > 0) {
    const commitText = analysis.suspectCommits
      .slice(0, 3)
      .map((c: any) => `- \`${c.commitHash}\` ${c.message} (by ${c.author})`)
      .join('\n');
    blocks.push({
      type: 'section',
      text: {
        type: 'mrkdwn',
        text: `:suspect: *Suspect Commits*\n${commitText}`,
      },
    });
  }

  blocks.push({ type: 'divider' });

  // Jira 티켓 정보
  const jiraStatus = jiraResult.isNew ? '생성됨' : '기존 티켓 업데이트';
  blocks.push({
    type: 'section',
    fields: [
      {
        type: 'mrkdwn',
        text: `:ticket: *Jira 티켓*\n<${jiraResult.ticketUrl}|${jiraResult.ticketKey}> ${jiraStatus}`,
      },
      {
        type: 'mrkdwn',
        text: `:label: *우선순위*\n${jiraResult.priority}`,
      },
    ],
  });

  if (jiraResult.assignee) {
    blocks.push({
      type: 'section',
      text: {
        type: 'mrkdwn',
        text: `:bust_in_silhouette: *담당자*: ${jiraResult.assignee} (Suspect Commit 기반)`,
      },
    });
  }

  return blocks;
}
```

#### 수동 접수용 댓글 (Block Kit)

```
+--------------------------------------------------+
| :mag: 접수 완료                                    |
|                                                    |
| :pushpin: 문제 분류: 코스 생성 실패                 |
| :iphone: 영향 기능: 데이트 코스 생성                |
| :link: 관련 Sentry 이슈: DATECLICK-42              |
|            (유사 에러 발견)                          |
|                                                    |
| :bulb: 초기 분석:                                   |
| 최근 30분 내 CourseService 관련 에러 5건 감지.       |
| OpenAI API 응답 지연과 관련된 것으로 추정.           |
|                                                    |
| :ticket: Jira 티켓: LAD-124 생성됨                  |
| :bust_in_silhouette: 담당자: 미배정 (Triage 필요)   |
| :label: 우선순위: P2 (Medium)                       |
+--------------------------------------------------+
```

```typescript
export function composeManualReportReply(analysis: any, jiraResult: any) {
  const blocks: any[] = [
    {
      type: 'header',
      text: {
        type: 'plain_text',
        text: ':mag: 접수 완료',
      },
    },
    {
      type: 'section',
      fields: [
        {
          type: 'mrkdwn',
          text: `:pushpin: *문제 분류*\n${analysis.problemCategory}`,
        },
        {
          type: 'mrkdwn',
          text: `:iphone: *영향 기능*\n${analysis.affectedFeature}`,
        },
      ],
    },
  ];

  // 관련 Sentry 이슈
  if (analysis.relatedSentryIssues?.length > 0) {
    const issueText = analysis.relatedSentryIssues
      .slice(0, 3)
      .map((i: any) => `- <${i.url}|${i.title}> (${i.similarity})`)
      .join('\n');
    blocks.push({
      type: 'section',
      text: {
        type: 'mrkdwn',
        text: `:link: *관련 Sentry 이슈*\n${issueText}`,
      },
    });
  } else {
    blocks.push({
      type: 'section',
      text: {
        type: 'mrkdwn',
        text: ':link: *관련 Sentry 이슈*\n유사한 이슈를 찾지 못했습니다.',
      },
    });
  }

  blocks.push({ type: 'divider' });

  // 초기 분석
  blocks.push({
    type: 'section',
    text: {
      type: 'mrkdwn',
      text: `:bulb: *초기 분석*\n${analysis.initialAnalysis}`,
    },
  });

  blocks.push({ type: 'divider' });

  // Jira 티켓 정보
  blocks.push({
    type: 'section',
    fields: [
      {
        type: 'mrkdwn',
        text: `:ticket: *Jira 티켓*\n<${jiraResult.ticketUrl}|${jiraResult.ticketKey}> 생성됨`,
      },
      {
        type: 'mrkdwn',
        text: `:label: *우선순위*\n${jiraResult.priority}`,
      },
    ],
  });

  blocks.push({
    type: 'section',
    text: {
      type: 'mrkdwn',
      text: ':bust_in_silhouette: *담당자*: 미배정 (Triage 필요)',
    },
  });

  blocks.push({
    type: 'context',
    elements: [
      {
        type: 'mrkdwn',
        text: ':information_source: 이 분석은 AI 기반 초기 판단입니다. 정확한 원인 파악을 위해 수동 확인이 필요할 수 있습니다.',
      },
    ],
  });

  return blocks;
}
```

### 3.10 환경변수 설정

```bash
# slack-error-bot/.env.example

# ====================================
# Slack Bot 설정
# ====================================
SLACK_BOT_TOKEN=xoxb-...           # Bot User OAuth Token
SLACK_APP_TOKEN=xapp-...           # App-Level Token (Socket Mode용)
ERROR_CHANNEL_ID=C0XXXXXXX        # #error 채널 ID

# ====================================
# Sentry API 설정
# ====================================
SENTRY_AUTH_TOKEN=sntrys_...       # Sentry API 인증 토큰
SENTRY_ORG=sfn-oh                  # Sentry 조직 슬러그
SENTRY_BACKEND_PROJECT=lian-date-app-backend
SENTRY_FRONTEND_PROJECT=lian-date-app-frontend

# ====================================
# Jira API 설정
# ====================================
JIRA_HOST=dateclick.atlassian.net
JIRA_EMAIL=dev@dateclick.com
JIRA_API_TOKEN=ATATT...           # Jira API 토큰

# ====================================
# GitHub API 설정
# ====================================
GITHUB_TOKEN=ghp_...              # GitHub Personal Access Token
GITHUB_REPO=org/lian-date-app

# ====================================
# AI API 설정 (선택적)
# ====================================
AI_API_KEY=sk-ant-...             # Claude API Key (수동 접수 분석용)

# ====================================
# Bot 동작 설정
# ====================================
LOG_LEVEL=info
NODE_ENV=production
```

---

## 4. Sentry 연동 설계 (분석 도구로서)

이 아키텍처에서 Sentry는 에러 알림의 중심이 아니라, Bot이 호출하여 상세 분석에 활용하는 **도구**이다. Sentry의 역할은 (1) 에러 데이터 수집/저장, (2) GitHub 연동 분석, (3) Slack 채널로 에러 메시지 전송(Alert Rule)이다.

### 4.1 GitHub Integration (Code Mapping, Suspect Commits)

#### Code Mapping 설정

Sentry 스택 트레이스의 파일 경로를 GitHub 저장소의 실제 파일로 매핑한다. Bot이 Sentry API를 통해 Suspect Commits를 조회하려면 이 설정이 필수이다.

```
# Backend Code Mapping
Repository:        [조직명]/lian-date-app
Branch:            main
Stack Trace Root:  com/dateclick/api/
Source Code Root:  backend/src/main/kotlin/com/dateclick/api/

# Frontend Code Mapping
Repository:        [조직명]/lian-date-app
Branch:            main
Stack Trace Root:  app://
Source Code Root:  frontend/src/
```

**설정 경로**: Sentry > Settings > Projects > [프로젝트] > Source Code > Add Code Mapping

#### Suspect Commits 동작 원리

```
[에러 발생]
     |
     v
[Sentry가 스택 트레이스 분석]
     |
     v
[Code Mapping으로 관련 파일 식별]
     |  예: com.dateclick.api.domain.course.service.CourseService
     |  --> backend/src/main/kotlin/.../CourseService.kt
     |
     v
[해당 파일의 최근 커밋 이력 조회]
     |
     v
[에러 발생 시점 전후 커밋을 Suspect Commit으로 표시]
     |
     v
[Bot이 Sentry API로 Suspect Commits 조회]
     |  GET /api/0/issues/{issueId}/committers/
     |
     v
[Jira 티켓 담당자 자동 배정 + 스레드 댓글에 포함]
```

#### CI/CD에서 Sentry Release 등록

Suspect Commits가 정확하게 동작하려면 CI/CD에서 Release를 등록해야 한다.

```yaml
# .github/workflows/deploy.yml 에 추가
- name: Create Sentry Release
  uses: getsentry/action-release@v1
  env:
    SENTRY_AUTH_TOKEN: ${{ secrets.SENTRY_AUTH_TOKEN }}
    SENTRY_ORG: sfn-oh
  with:
    environment: production
    projects: lian-date-app-backend lian-date-app-frontend
    version: ${{ github.sha }}
    set_commits: auto
```

### 4.2 Sentry API 사용 방법 (Bot이 호출)

Bot이 활용하는 Sentry API 엔드포인트 목록이다.

| API | 용도 | HTTP Method | Endpoint |
|-----|------|-------------|----------|
| 이슈 상세 | 에러 타입, 메시지, 발생 횟수 등 조회 | GET | `/api/0/issues/{issueId}/` |
| 최신 이벤트 | 스택 트레이스, 요청 정보 조회 | GET | `/api/0/issues/{issueId}/events/latest/` |
| Suspect Commits | 에러 관련 커밋/작성자 조회 | GET | `/api/0/issues/{issueId}/committers/` |
| Seer 분석 | AI 근본 원인 분석 결과 | GET | `/api/0/issues/{issueId}/autofix/` |
| 이슈 검색 | 키워드로 유사 이슈 검색 | GET | `/api/0/organizations/{org}/issues/?query=...` |
| 이벤트 통계 | 기간별 에러 수 통계 | GET | `/api/0/organizations/{org}/events-stats/` |

**인증**: `Authorization: Bearer {SENTRY_AUTH_TOKEN}` 헤더 사용

**필요 권한**: Sentry > Settings > Developer Settings > Internal Integration에서 다음 권한 부여:
- `project:read`
- `event:read`
- `issue:read`
- `org:read`

### 4.3 Seer AI 분석 활용

Seer는 Sentry의 AI 기반 에러 분석 기능이다. Bot이 Sentry API를 통해 Seer 분석 결과를 가져와서 스레드 댓글에 포함한다.

#### 활성화 방법

1. Sentry > Settings > Organization > AI/ML Features
2. Seer - Autofix 활성화
3. Seer - Issue Grouping 활성화
4. Seer - Root Cause Analysis 활성화

#### Bot에서의 활용 흐름

```
[Bot이 Sentry 이슈 분석 시]
     |
     v
[Seer 분석 결과 API 조회]
     |  GET /api/0/issues/{issueId}/autofix/
     |
     |-- 결과 있음 --> 스레드 댓글에 "원인 분석 (AI)" 섹션으로 포함
     +-- 결과 없음 --> 스택 트레이스 기반 자체 분석만 제공
```

### 4.4 Sentry Alert Rules (Slack 채널 메시지 전송용)

Sentry Alert Rules는 이 아키텍처에서 **Slack #error 채널에 에러 메시지를 전송하는 용도로만** 사용한다. Jira 연동이나 분석은 Bot이 담당하므로, Alert Rule의 액션은 Slack 전송만 설정한다.

```
+------------------------------------------------------------+
| Sentry Alert Rules (메시지 전송 전용)                        |
+------------------------------------------------------------+
|                                                              |
|  Rule 1: 신규 이슈 발생                                      |
|  |-- 조건: A new issue is created                            |
|  |-- 환경: production                                        |
|  +-- 액션: Slack #error 채널로 메시지 전송 (Jira 생성 안함)   |
|                                                              |
|  Rule 2: 에러 급증                                           |
|  |-- 조건: 에러 수 급증 (5분 내 200% 이상)                    |
|  +-- 액션: Slack #error 채널로 메시지 전송                    |
|                                                              |
|  Rule 3: 회귀 에러                                           |
|  |-- 조건: Resolved --> Unresolved (regression)              |
|  +-- 액션: Slack #error 채널로 메시지 전송                    |
|                                                              |
|  Rule 4: Fatal 에러                                          |
|  |-- 조건: level is fatal                                    |
|  +-- 액션: Slack #error 채널로 메시지 전송                    |
|                                                              |
|  *** Jira 티켓 생성은 모든 Rule에서 제외 ***                  |
|  *** Jira는 Bot이 분석 후 직접 생성 ***                       |
|                                                              |
+------------------------------------------------------------+
```

**Sentry 직접 Jira 연동을 사용하지 않는 이유**:
- Bot이 분석 결과를 포함하여 더 풍부한 Jira 티켓을 생성할 수 있다
- 수동 접수 메시지도 같은 파이프라인으로 Jira 티켓을 생성할 수 있다
- 중복 방지 로직을 Bot에서 통합 관리할 수 있다
- 스레드 댓글과 Jira 티켓이 동시에 생성되어 일관성이 보장된다

---

## 5. Alert Rules 설계

### 5.1 Sentry --> Slack 채널 메시지 전송 Rule

#### Rule 1: 신규 이슈 발생 (New Issue)

| 항목 | 설정 값 |
|------|---------|
| Alert Name | `[Production] 신규 에러 이슈` |
| Type | Issue Alert |
| Conditions | `A new issue is created` |
| Filters | `The issue's level is equal to error, fatal` |
| Environment | `production` |
| Actions | `Send a Slack notification to #error` |
| Action Interval | `30 minutes` |

#### Rule 2: 에러 급증 감지 (Error Spike)

| 항목 | 설정 값 |
|------|---------|
| Alert Name | `[Production] 에러 급증 감지` |
| Type | Metric Alert |
| Metric | `count()` for `event.type:error` |
| Time Window | `5 minutes` |
| Critical Threshold | `200%` (직전 1시간 대비) |
| Warning Threshold | `150%` |
| Actions | `Send Slack to #error` |

#### Rule 3: 회귀 에러 (Regression)

| 항목 | 설정 값 |
|------|---------|
| Alert Name | `[Production] 회귀 에러 감지` |
| Type | Issue Alert |
| Conditions | `A regression event occurs` |
| Environment | `production` |
| Actions | `Send Slack to #error` |
| Action Interval | `1440 minutes (24시간)` |

#### Rule 4: Fatal 에러 (Critical)

| 항목 | 설정 값 |
|------|---------|
| Alert Name | `[Production] Fatal 에러` |
| Type | Issue Alert |
| Conditions | Level is `fatal` OR title contains `OutOfMemoryError`, `StackOverflowError`, `Database connection` |
| Environment | `production` |
| Actions | `Send Slack to #error` with `@channel` mention |
| Action Interval | `1회` |

### 5.2 Bot의 우선순위 판정 (P0-P3)

Sentry Alert Rule은 Slack 채널에 메시지만 전송한다. **우선순위 판정은 Bot이 담당**한다. Bot은 Sentry API로 조회한 데이터를 기반으로 우선순위를 자동 판정한다.

```
[Bot이 Sentry API로 이슈 상세 조회]
     |
     v
에러 레벨이 "fatal"인가? ---- Yes ---> P0 (Critical)
     | No
     v
OOM/SOF/DB 연결 에러인가? ---- Yes ---> P0 (Critical)
     | No
     v
회귀 에러인가? ---- Yes ---> P1 (High)
     | No
     v
5분 내 50회 이상? ---- Yes ---> P1 (High)
     | No
     v
영향 사용자 10명 이상? ---- Yes ---> P1 (High)
     | No
     v
5분 내 10회 이상? ---- Yes ---> P2 (Medium)
     | No
     v
영향 사용자 5명 이상? ---- Yes ---> P2 (Medium)
     | No
     v
P3 (Low)
```

#### 우선순위별 대응 기준

| 우선순위 | 설명 | 대응 시간 | Jira 우선순위 |
|---------|------|-----------|--------------|
| P0 (Critical) | 서비스 전체 장애, DB 연결 실패 | 즉시 확인, 30분 이내 해결 착수 | Highest |
| P1 (High) | 핵심 기능 장애, 에러 급증, 회귀 에러 | 4시간 이내 확인, 당일 해결 착수 | High |
| P2 (Medium) | 일반 서버 에러, 외부 API 일시 장애 | 현재 Sprint 내 해결 | Medium |
| P3 (Low) | 저빈도 에러, 미미한 영향 | 백로그 | Low |

### 5.3 중복 방지 전략

#### Sentry 레벨: 이슈 그루핑

```
에러 이벤트 100건 (같은 NullPointerException)
     |
     v
Sentry Fingerprinting
     |
     v
1개의 이슈 (이벤트 카운트: 100)
     |
     v
Alert Rule: "신규 이슈 발생 시"
     |
     v
Slack 메시지 1건 (100건이 아닌 1건)
     |
     v
Bot이 1번만 분석/Jira 생성
```

#### Bot 레벨: Sentry Issue ID 기반 중복 검사

```
Bot이 Sentry Issue ID 추출
     |
     v
Jira에서 "sentry-{issueId}" 라벨로 검색
     |
     |-- 기존 티켓 존재 --> 코멘트만 추가 (신규 생성 안함)
     +-- 기존 티켓 없음 --> 신규 티켓 생성
```

#### Sentry Alert Rule 레벨: Action Interval

```
같은 이슈에 대한 Alert Rule 재트리거
     |
     v
Action Interval 확인
     |
     |-- 30분 이내 --> 알림 발송 안함 (중복 방지)
     +-- 30분 경과 --> 알림 발송 (Bot이 다시 분석)
                        |
                        v
                   Bot: Jira 중복 검사 --> 코멘트만 추가
```

### 5.4 알림 피로 방지

```
+------------------------------------------------------------+
|                    알림 피로 방지 계층                        |
+------------------------------------------------------------+
|                                                              |
|  Layer 1: Sentry 이슈 그루핑                                 |
|  +-- 동일 에러 100건 --> 1개 이슈 (가장 강력한 중복 제거)     |
|                                                              |
|  Layer 2: Sentry Alert Rule Action Interval                  |
|  +-- 같은 이슈에 대해 일정 시간 내 재알림 방지                |
|      |-- Rule 1 (신규): 30분                                 |
|      |-- Rule 2 (급증): 10분                                 |
|      |-- Rule 3 (회귀): 24시간                                |
|      +-- Rule 4 (Fatal): 1회                                 |
|                                                              |
|  Layer 3: Bot 메시지 분류기                                   |
|  +-- 분류 불가 메시지는 무시 (노이즈 방지)                    |
|  +-- Bot 자신의 메시지는 무시 (무한 루프 방지)                |
|                                                              |
|  Layer 4: Bot Jira 중복 검사                                  |
|  +-- 같은 Sentry Issue ID의 티켓이 있으면 코멘트만 추가       |
|                                                              |
|  Layer 5: 환경 필터                                           |
|  +-- production 환경에서만 Alert Rule 동작                    |
|                                                              |
+------------------------------------------------------------+
```

---

## 6. Jira 티켓 자동 생성 스펙

### 6.1 자동 에러용 티켓 템플릿

#### 제목 형식

```
[Sentry] {에러 타입}: {에러 메시지 요약 (최대 80자)}
```

**예시**:
```
[Sentry] NullPointerException: CourseService.createCourse에서 null 참조
[Sentry] AiGenerationException: OpenAI API 호출 타임아웃
[Sentry] TypeError: Cannot read properties of undefined (reading 'map')
```

#### 설명 내용

```markdown
## 에러 요약
- 에러 타입: {errorType}
- 발생 횟수: {eventCount}회
- 영향 사용자: {userCount}명
- 최초 발생: {firstSeen}
- 최근 발생: {lastSeen}

## Sentry 이슈
{sentry_issue_url}

## 스택 트레이스 (상위 5프레임)
{top_5_stack_frames}

## Suspect Commits
{suspect_commits_list}

## 참고
이 티켓은 Slack Bot에 의해 자동 생성되었습니다.
상세 분석은 Slack #error 채널의 스레드 댓글과 Sentry 이슈 링크에서 확인하세요.
```

#### 라벨

| 라벨 | 적용 조건 |
|------|-----------|
| `auto-created` | 모든 Bot 생성 티켓 |
| `sentry` | Sentry 에러 기반 티켓 |
| `sentry-{issueId}` | 중복 방지용 Sentry Issue ID |
| `backend` / `frontend` | 프로젝트 기반 자동 태깅 |
| `regression` | 회귀 에러인 경우 |

### 6.2 수동 접수용 티켓 템플릿

#### 제목 형식

```
[수동접수] {문제 분류}: {영향 기능}
```

**예시**:
```
[수동접수] 코스 생성 실패: 데이트 코스 생성
[수동접수] 페이지 로딩 실패: 메인 화면
[수동접수] 결제 오류: 프리미엄 결제
```

#### 설명 내용

```markdown
## 접수 내용
{원본 Slack 메시지 내용}

## Bot 초기 분석
{AI 기반 문제 분류 및 분석 결과}

## 관련 Sentry 이슈
{유사 에러 목록 또는 "발견되지 않음"}

## 참고
이 티켓은 Slack #error 채널 수동 접수를 기반으로 Bot이 자동 생성했습니다.
Triage가 필요합니다.
```

#### 라벨

| 라벨 | 적용 조건 |
|------|-----------|
| `auto-created` | 모든 Bot 생성 티켓 |
| `manual-report` | 수동 접수 기반 티켓 |
| `triage-needed` | Triage 필요 표시 |

### 6.3 우선순위 자동 판정 기준 (P0-P3)

```
+------------------------------------------------------------+
|                   우선순위 자동 판정 매트릭스                  |
+------------------------------------------------------------+
|                                                              |
|  P0 (Highest) - 즉시 대응                                    |
|  |-- 조건:                                                   |
|  |   |-- 에러 레벨이 "fatal"                                 |
|  |   |-- OutOfMemoryError, StackOverflowError                |
|  |   |-- 데이터베이스 연결 불가                               |
|  |   +-- 핵심 기능 (코스 생성) 완전 불가                      |
|  |-- 대응 시간: 30분 이내 확인, 2시간 이내 해결 착수          |
|  +-- Jira: Highest 우선순위                                  |
|                                                              |
|  P1 (High) - 당일 대응                                       |
|  |-- 조건:                                                   |
|  |   |-- 5분 내 동일 에러 50회 이상                           |
|  |   |-- 영향 사용자 10명 이상                                |
|  |   |-- 회귀 에러 (이전에 해결됨)                            |
|  |   +-- 핵심 API 에러 (POST /v1/courses)                    |
|  |-- 대응 시간: 4시간 이내 확인, 당일 해결 착수               |
|  +-- Jira: High 우선순위                                     |
|                                                              |
|  P2 (Medium) - 스프린트 내 대응                               |
|  |-- 조건:                                                   |
|  |   |-- 일반 서버 에러 (500)                                 |
|  |   |-- 외부 API 일시적 장애 (OpenAI, Kakao)                 |
|  |   |-- 영향 사용자 1-9명                                    |
|  |   +-- 수동 접수 중 Sentry 유사 이슈 발견                   |
|  |-- 대응 시간: 현재 스프린트 내                               |
|  +-- Jira: Medium 우선순위                                    |
|                                                              |
|  P3 (Low) - 백로그                                            |
|  |-- 조건:                                                   |
|  |   |-- 에러 발생 빈도 낮음 (일 1회 미만)                    |
|  |   |-- 사용자 영향 없거나 미미                              |
|  |   +-- 수동 접수 중 Sentry 유사 이슈 미발견                 |
|  |-- 대응 시간: 다음 스프린트 또는 백로그                     |
|  +-- Jira: Low 우선순위                                       |
|                                                              |
+------------------------------------------------------------+
```

### 6.4 Assignee 배정 로직

```
담당자 배정 순서:

1순위: Suspect Author (Sentry GitHub 연동)
  +-- Bot이 Sentry API로 Suspect Commits 조회
  +-- 커밋 작성자를 Jira 계정으로 매핑
  +-- 매핑 테이블: GitHub username --> Jira account ID

2순위: 프로젝트 기본 담당자
  +-- Backend 에러: 백엔드 리드
  +-- Frontend 에러: 프론트엔드 리드

3순위: 미배정 (Triage 필요)
  +-- 수동 접수 메시지: 기본적으로 미배정
  +-- Suspect Author를 찾지 못한 경우: 미배정
  +-- 라벨 "triage-needed" 추가
```

### 6.5 중복 방지 전략

```
[Bot이 Jira 티켓 생성 전]
     |
     v
[1단계: Sentry Issue ID 기반 검색]
JQL: project = LAD AND labels = "sentry-{issueId}" AND status != Done
     |
     |-- 결과 있음 --> 기존 티켓에 코멘트만 추가
     |                  "동일 이슈가 다시 감지되었습니다."
     |                  스레드 댓글에 "기존 Jira 티켓 참조" 표시
     |
     +-- 결과 없음 --> 다음 단계
          |
          v
[2단계: 유사 티켓 검색 (수동 접수의 경우)]
JQL: project = LAD AND summary ~ "{키워드}" AND status != Done
     |
     |-- 유사 티켓 존재 --> 스레드 댓글에 "유사 티켓 존재" 안내
     |                      신규 티켓은 생성하되 링크 포함
     |
     +-- 유사 티켓 없음 --> 신규 티켓 생성
```
