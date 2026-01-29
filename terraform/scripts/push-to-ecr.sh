#!/bin/bash

set -e

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# 함수: 로그 출력
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 스크립트 디렉토리
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

# Terraform output에서 ECR 정보 가져오기
log_info "Terraform 출력에서 ECR 저장소 정보를 가져오는 중..."

BACKEND_REPO=$(cd "$SCRIPT_DIR/terraform" && terraform output -raw backend_repository_url 2>/dev/null)
FRONTEND_REPO=$(cd "$SCRIPT_DIR/terraform" && terraform output -raw frontend_repository_url 2>/dev/null)
AWS_REGION=$(cd "$SCRIPT_DIR/terraform" && terraform output -raw region 2>/dev/null)
AWS_ACCOUNT=$(cd "$SCRIPT_DIR/terraform" && terraform output -raw account_id 2>/dev/null)

if [ -z "$BACKEND_REPO" ] || [ -z "$FRONTEND_REPO" ]; then
    log_error "ECR 저장소 정보를 가져올 수 없습니다"
    exit 1
fi

log_success "ECR 저장소 정보 로드 완료"
echo "  Backend: $BACKEND_REPO"
echo "  Frontend: $FRONTEND_REPO"
echo ""

# AWS CLI 설정
export AWS_PROFILE=${AWS_PROFILE:-lian-sfn-personal}

# ECR 로그인
log_info "ECR에 로그인하는 중..."
aws ecr get-login-password --region "$AWS_REGION" | \
    docker login --username AWS --password-stdin "$AWS_ACCOUNT.dkr.ecr.$AWS_REGION.amazonaws.com"

log_success "ECR 로그인 완료"
echo ""

# Backend 이미지 빌드 및 푸시
log_info "Backend 이미지를 빌드하는 중..."
cd "$SCRIPT_DIR"

TIMESTAMP=$(date +%s)
docker build -f backend/Dockerfile -t "$BACKEND_REPO:latest" -t "$BACKEND_REPO:$TIMESTAMP" backend/
log_success "Backend 이미지 빌드 완료"

log_info "Backend 이미지를 ECR에 푸시하는 중..."
docker push "$BACKEND_REPO:latest"
docker push "$BACKEND_REPO:$TIMESTAMP"
log_success "Backend 이미지 푸시 완료"
echo ""

# Frontend 이미지 빌드 및 푸시
log_info "Frontend 이미지를 빌드하는 중..."
docker build -f frontend/Dockerfile -t "$FRONTEND_REPO:latest" -t "$FRONTEND_REPO:$TIMESTAMP" frontend/
log_success "Frontend 이미지 빌드 완료"

log_info "Frontend 이미지를 ECR에 푸시하는 중..."
docker push "$FRONTEND_REPO:latest"
docker push "$FRONTEND_REPO:$TIMESTAMP"
log_success "Frontend 이미지 푸시 완료"
echo ""

log_success "✅ 모든 이미지가 ECR에 푸시되었습니다!"
