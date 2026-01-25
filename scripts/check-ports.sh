#!/bin/bash

# Worktree 환경에서 포트 충돌 확인 및 자동 설정 스크립트

set -e

# 색상 코드
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# .env 파일 로드
if [ -f .env ]; then
    export $(grep -v '^#' .env | xargs)
fi

# 기본 포트 설정
BACKEND_PORT="${BACKEND_PORT:-8080}"
FRONTEND_PORT="${FRONTEND_PORT:-3000}"

echo -e "${BLUE}🔍 Worktree 포트 충돌 확인 중...${NC}"

# 포트 사용 중인지 확인하는 함수
check_port_in_use() {
    local port=$1
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
        return 0 # 사용 중
    else
        return 1 # 사용 가능
    fi
}

# 포트 사용 중인 프로세스 정보 출력
show_port_info() {
    local port=$1
    echo -e "${YELLOW}포트 $port 사용 중인 프로세스:${NC}"
    lsof -Pi :$port -sTCP:LISTEN
}

# 사용 가능한 포트 찾기
find_available_port() {
    local start_port=$1
    local port=$start_port

    while check_port_in_use $port; do
        ((port++))
    done

    echo $port
}

# .env 파일에 포트 설정 추가
update_env_file() {
    local backend_port=$1
    local frontend_port=$2

    if [ ! -f .env ]; then
        cp .env.example .env 2>/dev/null || touch .env
    fi

    # 기존 포트 설정 제거
    sed -i.bak '/^BACKEND_PORT=/d' .env 2>/dev/null || true
    sed -i.bak '/^FRONTEND_PORT=/d' .env 2>/dev/null || true
    rm -f .env.bak

    # 새 포트 설정 추가
    echo "BACKEND_PORT=$backend_port" >> .env
    echo "FRONTEND_PORT=$frontend_port" >> .env
}

# 메인 로직
main() {
    local backend_conflict=false
    local frontend_conflict=false

    # Backend 포트 확인
    if check_port_in_use $BACKEND_PORT; then
        echo -e "${RED}❌ Backend 포트 $BACKEND_PORT이 이미 사용 중입니다!${NC}"
        show_port_info $BACKEND_PORT
        backend_conflict=true
    else
        echo -e "${GREEN}✅ Backend 포트 $BACKEND_PORT 사용 가능${NC}"
    fi

    # Frontend 포트 확인
    if check_port_in_use $FRONTEND_PORT; then
        echo -e "${RED}❌ Frontend 포트 $FRONTEND_PORT이 이미 사용 중입니다!${NC}"
        show_port_info $FRONTEND_PORT
        frontend_conflict=true
    else
        echo -e "${GREEN}✅ Frontend 포트 $FRONTEND_PORT 사용 가능${NC}"
    fi

    # 충돌이 있으면 처리
    if [ "$backend_conflict" = true ] || [ "$frontend_conflict" = true ]; then
        echo ""
        echo -e "${YELLOW}⚠️  포트 충돌이 감지되었습니다!${NC}"
        echo ""
        echo "해결 방법을 선택하세요:"
        echo "1) 다른 worktree의 서비스를 종료하고 계속"
        echo "2) 이 worktree에 다른 포트 자동 할당"
        echo "3) 취소"
        echo ""
        read -p "선택 (1-3): " choice

        case $choice in
            1)
                echo -e "${BLUE}다른 worktree의 서비스를 종료한 후 다시 실행하세요.${NC}"
                exit 1
                ;;
            2)
                echo -e "${BLUE}사용 가능한 포트를 자동으로 할당합니다...${NC}"

                NEW_BACKEND_PORT=$BACKEND_PORT
                NEW_FRONTEND_PORT=$FRONTEND_PORT

                if [ "$backend_conflict" = true ]; then
                    NEW_BACKEND_PORT=$(find_available_port 8080)
                    echo -e "${GREEN}✅ Backend 포트 할당: $NEW_BACKEND_PORT${NC}"
                fi

                if [ "$frontend_conflict" = true ]; then
                    NEW_FRONTEND_PORT=$(find_available_port 3000)
                    echo -e "${GREEN}✅ Frontend 포트 할당: $NEW_FRONTEND_PORT${NC}"
                fi

                update_env_file $NEW_BACKEND_PORT $NEW_FRONTEND_PORT

                echo ""
                echo -e "${GREEN}✅ .env 파일이 업데이트되었습니다:${NC}"
                echo "   BACKEND_PORT=$NEW_BACKEND_PORT"
                echo "   FRONTEND_PORT=$NEW_FRONTEND_PORT"
                echo ""
                echo -e "${BLUE}이제 서비스를 시작할 수 있습니다.${NC}"
                ;;
            3)
                echo -e "${YELLOW}취소되었습니다.${NC}"
                exit 1
                ;;
            *)
                echo -e "${RED}잘못된 선택입니다.${NC}"
                exit 1
                ;;
        esac
    else
        echo ""
        echo -e "${GREEN}✅ 모든 포트 사용 가능! 테스트를 진행할 수 있습니다.${NC}"
    fi
}

main
