# Scripts

Worktree 환경에서 안전한 개발 및 테스트를 위한 헬퍼 스크립트 모음

## check-ports.sh

포트 충돌을 확인하고 자동으로 사용 가능한 포트를 할당하는 스크립트

### 사용법

```bash
./scripts/check-ports.sh
```

### 기능

- 현재 포트 8080 (Backend), 3000 (Frontend) 사용 여부 확인
- 포트 충돌 시 선택 옵션 제공:
  1. 다른 worktree의 서비스 종료 후 계속
  2. 자동으로 사용 가능한 포트 할당 (8081, 3001, ...)
  3. 취소
- `.env` 파일에 할당된 포트 자동 저장

### 사용 시나리오

```bash
# 시나리오 1: main worktree에서 포트 8080, 3000 사용 중
# feature/LAD-123 worktree에서 테스트하려고 할 때

cd ~/workspace/lian-date-app-feature-LAD-123
./scripts/check-ports.sh

# 출력 예시:
# ❌ Backend 포트 8080이 이미 사용 중입니다!
# 해결 방법을 선택하세요:
# 1) 다른 worktree의 서비스를 종료하고 계속
# 2) 이 worktree에 다른 포트 자동 할당
# 3) 취소
#
# 선택 (1-3): 2
# ✅ Backend 포트 할당: 8081
# ✅ Frontend 포트 할당: 3001
# ✅ .env 파일이 업데이트되었습니다
```

## run-tests.sh

포트 충돌 확인 후 안전하게 테스트를 실행하는 통합 스크립트

### 사용법

```bash
./scripts/run-tests.sh [frontend|backend|e2e|all]
```

### 인자

- `frontend`: 프론트엔드 테스트만 실행 (타입 체크, 린트, 단위 테스트)
- `backend`: 백엔드 테스트만 실행 (Gradle 테스트)
- `e2e`: E2E 테스트만 실행 (Playwright)
- `all`: 전체 테스트 스위트 실행 (기본값)

### 실행 단계

1. **포트 충돌 확인**: `check-ports.sh` 자동 실행
2. **서비스 시작**: Docker Compose로 서비스 실행
3. **헬스체크**: Backend `/health`, Frontend `/` 확인
4. **코드 품질 검사**: TypeScript, ESLint, 단위 테스트
5. **통합 테스트**: 선택한 타입에 따라 테스트 실행

### 예시

```bash
# 전체 테스트 실행
./scripts/run-tests.sh

# 프론트엔드만 테스트
./scripts/run-tests.sh frontend

# E2E 테스트만 실행
./scripts/run-tests.sh e2e
```

## Worktree 환경에서 사용

### 권장 워크플로우

```bash
# 1. 새로운 worktree 생성
git worktree add ../lian-date-app-feature-LAD-123 feature/LAD-123

# 2. 해당 worktree로 이동
cd ../lian-date-app-feature-LAD-123

# 3. 포트 충돌 체크 및 자동 할당
./scripts/check-ports.sh

# 4. 개발 작업...

# 5. 테스트 실행 (포트 충돌 자동 체크)
./scripts/run-tests.sh all

# 6. Jira 티켓과 연동하여 테스트 (Claude Code)
# /jira-test all
```

### 다중 Worktree 시나리오

```
main worktree          → localhost:8080, localhost:3000
feature/LAD-123        → localhost:8081, localhost:3001 (자동 할당)
feature/LAD-456        → localhost:8082, localhost:3002 (자동 할당)
```

각 worktree가 독립적인 포트를 사용하여 동시에 테스트 가능합니다.

## 트러블슈팅

### 포트가 계속 사용 중으로 표시될 때

```bash
# 사용 중인 포트 확인
lsof -i :8080
lsof -i :3000

# Docker 컨테이너 확인
docker ps

# 모든 서비스 종료
docker compose down

# 또는 특정 포트 프로세스 종료
kill -9 $(lsof -t -i:8080)
```

### .env 파일 초기화

```bash
# .env 파일 삭제 후 재생성
rm .env
cp .env.example .env

# 포트 체크 스크립트로 자동 할당
./scripts/check-ports.sh
```

### 권한 문제

```bash
# 스크립트 실행 권한 부여
chmod +x scripts/*.sh
```
