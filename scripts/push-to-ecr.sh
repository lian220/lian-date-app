#!/bin/bash

# Docker 이미지를 빌드하고 ECR에 푸시하는 스크립트
# 사용법: ./scripts/push-to-ecr.sh

set -e

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 함수: 로그 출력
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# AWS Profile 설정
export AWS_PROFILE=lian-sfn-personal
AWS_REGION="us-east-1"
AWS_ACCOUNT_ID="606103597454"
ECR_REGISTRY="${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"

# 프로젝트 루트 디렉토리
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

log_info "프로젝트 루트: $PROJECT_ROOT"
log_info "AWS Profile: $AWS_PROFILE"
log_info "AWS Region: $AWS_REGION"
log_info "ECR Registry: $ECR_REGISTRY"
echo ""

# 1. ECR 로그인
log_info "ECR에 로그인 중..."
aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $ECR_REGISTRY
log_success "ECR 로그인 완료"
echo ""

# 2. Backend 이미지 빌드 및 푸시
log_info "=== Backend 이미지 빌드 시작 ==="
cd "$PROJECT_ROOT/backend"

log_info "Backend Dockerfile 확인..."
if [ ! -f "Dockerfile" ]; then
    log_error "Backend Dockerfile을 찾을 수 없습니다: $PROJECT_ROOT/backend/Dockerfile"
    exit 1
fi

log_info "Backend 이미지 빌드 중 (linux/amd64 플랫폼)..."
docker build --platform linux/amd64 -t lian-date-prod-backend:latest .
log_success "Backend 이미지 빌드 완료"

log_info "Backend 이미지 태깅 중..."
docker tag lian-date-prod-backend:latest ${ECR_REGISTRY}/lian-date-prod-backend:latest
log_success "Backend 이미지 태깅 완료"

log_info "Backend 이미지를 ECR에 푸시 중..."
docker push ${ECR_REGISTRY}/lian-date-prod-backend:latest
log_success "Backend 이미지 푸시 완료"
echo ""

# 3. Frontend 이미지 빌드 및 푸시
log_info "=== Frontend 이미지 빌드 시작 ==="
cd "$PROJECT_ROOT/frontend"

log_info "Frontend Dockerfile 확인..."
if [ ! -f "Dockerfile" ]; then
    log_error "Frontend Dockerfile을 찾을 수 없습니다: $PROJECT_ROOT/frontend/Dockerfile"
    exit 1
fi

log_info "Frontend 이미지 빌드 중 (linux/amd64 플랫폼)..."
docker build --platform linux/amd64 -t lian-date-prod-frontend:latest .
log_success "Frontend 이미지 빌드 완료"

log_info "Frontend 이미지 태깅 중..."
docker tag lian-date-prod-frontend:latest ${ECR_REGISTRY}/lian-date-prod-frontend:latest
log_success "Frontend 이미지 태깅 완료"

log_info "Frontend 이미지를 ECR에 푸시 중..."
docker push ${ECR_REGISTRY}/lian-date-prod-frontend:latest
log_success "Frontend 이미지 푸시 완료"
echo ""

# 4. ECR 이미지 확인
log_info "=== ECR 이미지 확인 ==="
log_info "Backend 이미지:"
aws ecr describe-images --repository-name lian-date-prod-backend --region $AWS_REGION --query 'imageDetails[*].[imageTags[0],imagePushedAt,imageSizeInBytes]' --output table

log_info "Frontend 이미지:"
aws ecr describe-images --repository-name lian-date-prod-frontend --region $AWS_REGION --query 'imageDetails[*].[imageTags[0],imagePushedAt,imageSizeInBytes]' --output table
echo ""

# 5. ECS 서비스 업데이트 (force new deployment)
log_info "=== ECS 서비스 업데이트 ==="
log_info "Backend 서비스 재시작 중..."
aws ecs update-service \
    --cluster lian-date-prod-cluster \
    --service lian-date-prod-backend-service \
    --force-new-deployment \
    --region $AWS_REGION \
    --query 'service.[serviceName,status,desiredCount,runningCount]' \
    --output table

log_info "Frontend 서비스 재시작 중..."
aws ecs update-service \
    --cluster lian-date-prod-cluster \
    --service lian-date-prod-frontend-service \
    --force-new-deployment \
    --region $AWS_REGION \
    --query 'service.[serviceName,status,desiredCount,runningCount]' \
    --output table

log_success "ECS 서비스 업데이트 완료"
echo ""

log_success "=== 모든 작업 완료! ==="
log_info "ECS 서비스가 새로운 이미지를 가져와 시작합니다."
log_info "서비스 상태 확인: aws ecs describe-services --cluster lian-date-prod-cluster --services lian-date-prod-backend-service lian-date-prod-frontend-service --region us-east-1"
