#!/bin/bash

# Terraform 인프라 종료(삭제) 스크립트 (Production 환경)
# 사용법: ./scripts/terraform-destroy.sh
# 예시: ./scripts/terraform-destroy.sh

set -e  # 에러 발생 시 스크립트 중단

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

# Production 환경만 사용
ENVIRONMENT="prod"

# Terraform 디렉토리로 이동
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
TERRAFORM_DIR="$PROJECT_ROOT/terraform"
TFVARS_FILE="$TERRAFORM_DIR/environments/$ENVIRONMENT/terraform.tfvars"

cd "$TERRAFORM_DIR" || {
    log_error "Terraform 디렉토리를 찾을 수 없습니다: $TERRAFORM_DIR"
    exit 1
}

# tfvars 파일 확인
if [ ! -f "$TFVARS_FILE" ]; then
    log_error "Terraform 변수 파일을 찾을 수 없습니다: $TFVARS_FILE"
    log_info "템플릿 파일을 복사하여 생성하세요:"
    log_info "  cp $TFVARS_FILE.example $TFVARS_FILE"
    exit 1
fi

log_warning "⚠️  ⚠️  ⚠️  경고 ⚠️  ⚠️  ⚠️"
log_warning "이 작업은 Production 환경의 모든 인프라를 삭제합니다!"
log_warning "다음 리소스들이 모두 삭제됩니다:"
log_warning "  - VPC 및 네트워크"
log_warning "  - ECS 클러스터 및 서비스"
log_warning "  - ALB (Application Load Balancer)"
log_warning "  - RDS 데이터베이스"
log_warning "  - ECR 리포지토리"
log_warning "  - CloudWatch 로그"
log_warning "  - 기타 모든 관련 리소스"
echo ""

# AWS 자격 증명 확인
log_info "AWS 자격 증명 확인 중..."
if ! aws sts get-caller-identity > /dev/null 2>&1; then
    log_error "AWS 자격 증명이 설정되지 않았습니다."
    log_info "다음 명령어로 자격 증명을 설정하세요:"
    log_info "  aws configure"
    exit 1
fi

AWS_ACCOUNT=$(aws sts get-caller-identity --query Account --output text 2>/dev/null || echo "unknown")
AWS_USER=$(aws sts get-caller-identity --query Arn --output text 2>/dev/null || echo "unknown")
log_info "AWS 계정: $AWS_ACCOUNT"
log_info "AWS 사용자: $AWS_USER"
echo ""

# 최종 확인
log_warning "정말로 Production 환경의 모든 인프라를 삭제하시겠습니까?"
read -p "삭제하려면 'DELETE'를 정확히 입력하세요: " confirm

if [ "$confirm" != "DELETE" ]; then
    log_info "삭제가 취소되었습니다."
    exit 0
fi

echo ""
log_warning "마지막 확인: 정말로 삭제하시겠습니까?"
read -p "삭제하려면 'yes'를 입력하세요: " final_confirm

if [ "$final_confirm" != "yes" ]; then
    log_info "삭제가 취소되었습니다."
    exit 0
fi

echo ""
log_info "Terraform 초기화 중..."
if [ -d "terraform.d/plugins" ]; then
    terraform init -plugin-dir=terraform.d/plugins > /dev/null 2>&1
else
    terraform init > /dev/null 2>&1
fi

echo ""
log_info "인프라 삭제 시작..."
log_warning "이 작업은 몇 분이 걸릴 수 있습니다..."
echo ""

# Terraform destroy 실행
terraform destroy -var-file="$TFVARS_FILE" -auto-approve

echo ""
log_success "인프라 삭제가 완료되었습니다!"
log_info "삭제된 환경: Production"
log_info "삭제된 AWS 계정: $AWS_ACCOUNT"
