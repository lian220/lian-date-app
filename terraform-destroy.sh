#!/bin/bash

# .env 파일에서 환경변수를 읽어 Terraform destroy를 실행하는 스크립트
# 사용법: ./terraform-destroy.sh
#
# ⚠️ Terraform State 문제로 인한 destroy 실패 원인 ⚠️
#
# 문제 1: Terraform State 파일 불일치
#   - Terraform state 파일이 실제 AWS 인프라와 동기화되지 않음
#   - State 파일에는 리소스가 없지만 실제 AWS에는 리소스가 존재
#   - 원인: State 파일 손실, 수동 리소스 생성, 또는 다른 워크스페이스에서 생성
#
# 문제 2: Backend 구성 누락
#   - 로컬 state 파일과 원격(S3) state 파일이 다를 수 있음
#   - terraform/backend.tf가 이전에 삭제되어 state 추적 불가
#
# 해결 방법:
#   A. Terraform Import (권장)
#      - 기존 리소스를 terraform state에 import
#      - terraform import <resource_type>.<name> <resource_id>
#      - 모든 리소스를 import 후 terraform destroy 실행
#
#   B. AWS CLI로 수동 삭제 (현재 사용된 방법)
#      - 의존성 순서대로 리소스 삭제:
#        1. ECS Services → ECS Cluster
#        2. RDS Instance → DB Subnet Group
#        3. ALB (deletion protection 해제) → Target Groups
#        4. ECR Repositories
#        5. VPC Endpoints → NAT Gateways → Internet Gateway
#        6. Security Groups (규칙 제거 후 삭제)
#        7. Route Tables → Subnets → VPC
#        8. CloudWatch Logs, Alarms, SNS Topics
#        9. IAM Roles (정책 분리 후 삭제)
#
#   C. Terraform Refresh + Import
#      - terraform refresh로 현재 상태 동기화 시도
#      - 실패 시 terraform import로 리소스 가져오기
#
# 예방 방법:
#   - S3 backend 사용으로 state 파일 중앙 관리
#   - State locking (DynamoDB) 활성화
#   - 수동 리소스 생성 금지 (모든 변경은 Terraform으로)
#   - terraform state 파일 백업

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

# .env 파일 경로
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ENV_FILE="$SCRIPT_DIR/.env"

if [ ! -f "$ENV_FILE" ]; then
    log_error ".env 파일을 찾을 수 없습니다: $ENV_FILE"
    exit 1
fi

log_info ".env 파일에서 환경변수를 읽어옵니다..."

# .env 파일에서 환경변수 로드 (주석과 빈 줄 제외)
export $(grep -v '^#' "$ENV_FILE" | grep -v '^$' | xargs)

# Terraform 변수로 변환
export TF_VAR_openai_api_key="$OPENAI_API_KEY"
export TF_VAR_kakao_rest_api_key="$KAKAO_REST_API_KEY"
export TF_VAR_kakao_javascript_key="$KAKAO_JAVASCRIPT_KEY"
export TF_VAR_db_password="$DB_PASSWORD"
export TF_VAR_db_username="$DB_USERNAME"

log_success "환경변수 로드 완료"
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

log_warning "⚠️  ⚠️  ⚠️  경고 ⚠️  ⚠️  ⚠️"
log_warning "이 작업은 모든 AWS 인프라를 삭제합니다!"
log_warning "다음 리소스들이 모두 삭제됩니다:"
log_warning "  - VPC 및 네트워크"
log_warning "  - ECS 클러스터 및 서비스"
log_warning "  - ALB (Application Load Balancer)"
log_warning "  - RDS 데이터베이스"
log_warning "  - ECR 리포지토리"
log_warning "  - Secrets Manager"
log_warning "  - CloudWatch 로그 및 알람"
log_warning "  - 기타 모든 관련 리소스"
echo ""

# 최종 확인
log_warning "정말로 모든 인프라를 삭제하시겠습니까?"
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

# Terraform 디렉토리로 이동
cd "$SCRIPT_DIR/terraform" || {
    log_error "Terraform 디렉토리를 찾을 수 없습니다"
    exit 1
}

echo ""
log_info "Terraform 초기화 중..."
terraform init > /dev/null 2>&1

echo ""
log_info "인프라 삭제 시작..."
log_warning "이 작업은 몇 분이 걸릴 수 있습니다..."
echo ""

# Terraform destroy 실행
terraform destroy -auto-approve

echo ""
log_success "인프라 삭제가 완료되었습니다!"
log_info "삭제된 AWS 계정: $AWS_ACCOUNT"
