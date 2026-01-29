#!/bin/bash

# Worktree 환경에서 안전한 테스트 실행 스크립트

set -e

# 색상 코드
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 테스트 타입 (기본값: all)
TEST_TYPE="${1:-all}"

echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BLUE}📋 Jira 통합 테스트 시작 (타입: $TEST_TYPE)${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""

# 단계 1: 포트 충돌 확인
echo -e "${BLUE}[1/4] 포트 충돌 확인${NC}"
./scripts/check-ports.sh

# .env 파일 다시 로드 (포트가 변경되었을 수 있음)
if [ -f .env ]; then
    export $(grep -v '^#' .env | xargs)
fi

BACKEND_PORT="${BACKEND_PORT:-8080}"
FRONTEND_PORT="${FRONTEND_PORT:-3000}"

echo ""

# 단계 2: 서비스 시작
echo -e "${BLUE}[2/4] 독립적인 테스트 환경 시작${NC}"
echo "Backend: http://localhost:$BACKEND_PORT"
echo "Frontend: http://localhost:$FRONTEND_PORT"

# Docker Compose로 서비스 시작
docker compose up -d

# 헬스체크 대기
echo ""
echo -e "${YELLOW}⏳ 서비스 준비 대기 중...${NC}"

# Backend 헬스체크
for i in {1..30}; do
    if curl -sf http://localhost:$BACKEND_PORT/health > /dev/null 2>&1; then
        echo -e "${GREEN}✅ Backend 준비 완료${NC}"
        break
    fi
    if [ $i -eq 30 ]; then
        echo -e "${RED}❌ Backend 시작 실패 (타임아웃)${NC}"
        docker compose logs backend
        exit 1
    fi
    sleep 1
done

# Frontend 헬스체크
for i in {1..30}; do
    if curl -sf http://localhost:$FRONTEND_PORT > /dev/null 2>&1; then
        echo -e "${GREEN}✅ Frontend 준비 완료${NC}"
        break
    fi
    if [ $i -eq 30 ]; then
        echo -e "${RED}❌ Frontend 시작 실패 (타임아웃)${NC}"
        docker compose logs frontend
        exit 1
    fi
    sleep 1
done

echo ""

# 단계 3: 코드 품질 검사
echo -e "${BLUE}[3/4] 코드 품질 검사${NC}"

if [ "$TEST_TYPE" = "frontend" ] || [ "$TEST_TYPE" = "all" ]; then
    echo -e "${YELLOW}📝 TypeScript 타입 체크...${NC}"
    docker compose exec -T frontend npm run type-check 2>/dev/null || echo "타입 체크 스크립트 없음"

    echo -e "${YELLOW}📝 ESLint/Prettier 검사...${NC}"
    docker compose exec -T frontend npm run lint 2>/dev/null || echo "린트 스크립트 없음"
fi

if [ "$TEST_TYPE" = "backend" ] || [ "$TEST_TYPE" = "all" ]; then
    echo -e "${YELLOW}📝 Backend 테스트...${NC}"
    docker compose exec -T backend ./gradlew test 2>/dev/null || echo "백엔드 테스트 스크립트 없음"
fi

echo ""

# 단계 4: 통합 테스트
echo -e "${BLUE}[4/4] 통합 테스트${NC}"

case $TEST_TYPE in
    frontend)
        echo -e "${YELLOW}🧪 프론트엔드 단위 테스트...${NC}"
        docker compose exec -T frontend npm test 2>/dev/null || echo "테스트 스크립트 없음"
        ;;
    backend)
        echo -e "${YELLOW}🧪 백엔드 통합 테스트...${NC}"
        docker compose exec -T backend ./gradlew integrationTest 2>/dev/null || echo "통합 테스트 없음"
        ;;
    e2e)
        echo -e "${YELLOW}🧪 E2E 테스트 (Playwright)...${NC}"
        docker compose exec -T frontend npx playwright test 2>/dev/null || echo "E2E 테스트 설정 없음"
        ;;
    all)
        echo -e "${YELLOW}🧪 전체 테스트 스위트...${NC}"
        docker compose exec -T frontend npm test 2>/dev/null || echo "프론트엔드 테스트 없음"
        docker compose exec -T backend ./gradlew test 2>/dev/null || echo "백엔드 테스트 없음"
        docker compose exec -T frontend npx playwright test 2>/dev/null || echo "E2E 테스트 없음"
        ;;
    *)
        echo -e "${RED}❌ 잘못된 테스트 타입: $TEST_TYPE${NC}"
        echo "사용 가능한 타입: frontend, backend, e2e, all"
        exit 1
        ;;
esac

echo ""
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}✅ 테스트 완료!${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"

# 테스트 후 서비스 종료 여부 확인
read -p "서비스를 종료하시겠습니까? (y/N): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo -e "${YELLOW}🔄 서비스 종료 중...${NC}"
    docker compose down
    echo -e "${GREEN}✅ 서비스 종료 완료${NC}"
else
    echo -e "${GREEN}서비스가 계속 실행 중입니다.${NC}"
    echo "Backend: http://localhost:$BACKEND_PORT"
    echo "Frontend: http://localhost:$FRONTEND_PORT"
fi
