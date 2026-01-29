#!/bin/bash

# .env 파일에서 환경변수를 읽어 Terraform 변수로 변환하는 스크립트
# 사용법: ./terraform-apply.sh [action]
# 예시: ./terraform-apply.sh init
#       ./terraform-apply.sh plan
#       ./terraform-apply.sh apply
#       ./terraform-apply.sh destroy

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

# 환경 파일 경로
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROD_ENV="$SCRIPT_DIR/.env.prod"

# .env.prod 필수
if [ ! -f "$PROD_ENV" ]; then
    log_error "❌ .env.prod 파일을 찾을 수 없습니다!"
    log_error ""
    log_info "Terraform 배포에는 .env.prod 파일이 필수입니다 (로컬 .env와 분리)"
    log_info ""
    log_info "다음 명령어로 생성하세요:"
    log_info "  cp .env.prod.example .env.prod"
    log_info ""
    log_info "그 다음 .env.prod를 편집하여 실제 Supabase 정보를 입력하세요:"
    log_info "  DB_HOST=aws-1-ap-northeast-2.pooler.supabase.com"
    log_info "  DB_USERNAME=postgres.your_project_id"
    log_info "  DB_PASSWORD=your_supabase_password"
    log_info ""
    log_info "주의: .env.prod는 git 추적이 안 되므로 절대 commit되지 않습니다"
    log_info ""
    exit 1
fi

log_info ".env.prod 파일에서 Terraform 환경변수를 읽어옵니다..."

# .env.prod 파일에서 환경변수 로드 (주석과 빈 줄 제외)
export $(grep -v '^#' "$PROD_ENV" | grep -v '^$' | xargs)

# AWS Profile 설정
export AWS_PROFILE=lian-sfn-personal

# Terraform 변수로 변환
export TF_VAR_openai_api_key="$OPENAI_API_KEY"
export TF_VAR_kakao_rest_api_key="$KAKAO_REST_API_KEY"
export TF_VAR_kakao_javascript_key="$KAKAO_JAVASCRIPT_KEY"
export TF_VAR_db_host="$DB_HOST"
export TF_VAR_db_port="${DB_PORT:-6543}"
export TF_VAR_db_name="${DB_NAME:-dateclick}"
export TF_VAR_db_username="$DB_USERNAME"
export TF_VAR_db_password="$DB_PASSWORD"

# 필수 값 검증
if [ -z "$TF_VAR_db_host" ]; then
    log_error "DB_HOST가 설정되지 않았습니다 (Supabase 호스트 필요)"
    exit 1
fi

if [ -z "$TF_VAR_db_username" ]; then
    log_error "DB_USERNAME이 설정되지 않았습니다"
    exit 1
fi

if [ -z "$TF_VAR_db_password" ]; then
    log_error "DB_PASSWORD가 설정되지 않았습니다"
    exit 1
fi

if [ -z "$TF_VAR_openai_api_key" ]; then
    log_warning "OPENAI_API_KEY가 설정되지 않았습니다"
fi

if [ -z "$TF_VAR_kakao_rest_api_key" ]; then
    log_warning "KAKAO_REST_API_KEY가 설정되지 않았습니다"
fi

if [ -z "$TF_VAR_kakao_javascript_key" ]; then
    log_warning "KAKAO_JAVASCRIPT_KEY가 설정되지 않았습니다"
fi

log_success "환경변수 로드 완료"
echo ""
log_info "Loaded variables:"
echo "  - OPENAI_API_KEY: ${OPENAI_API_KEY:0:10}..."
echo "  - KAKAO_REST_API_KEY: ${KAKAO_REST_API_KEY:0:10}..."
echo "  - KAKAO_JAVASCRIPT_KEY: ${KAKAO_JAVASCRIPT_KEY:0:10}..."
echo "  - DB_HOST: $DB_HOST"
echo "  - DB_PORT: ${DB_PORT:-6543}"
echo "  - DB_NAME: ${DB_NAME:-dateclick}"
echo "  - DB_USERNAME: $DB_USERNAME"
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
log_success "AWS 계정: $AWS_ACCOUNT"
log_info "AWS 사용자: $AWS_USER"
echo ""

# Terraform 디렉토리로 이동
cd "$SCRIPT_DIR/terraform" || {
    log_error "Terraform 디렉토리를 찾을 수 없습니다"
    exit 1
}

# terraform.tfvars 파일 확인
if [ ! -f "terraform.tfvars" ]; then
    log_error "terraform.tfvars 파일을 찾을 수 없습니다"
    log_info "다음 명령어로 생성하세요:"
    log_info "  cd terraform && cp terraform.tfvars.example terraform.tfvars"
    exit 1
fi

# 함수: 고아 Elastic IP 정리
cleanup_orphaned_eips() {
    log_info "고아 Elastic IP를 정리하는 중입니다..."

    # 현재 리전에서 할당되지 않은 Elastic IP 조회
    ORPHANED_EIPS=$(aws ec2 describe-addresses \
        --region us-east-1 \
        --query 'Addresses[?AssociationId==null].AllocationId' \
        --output text 2>/dev/null)

    if [ -z "$ORPHANED_EIPS" ]; then
        log_success "고아 Elastic IP가 없습니다"
        return 0
    fi

    # 고아 Elastic IP 해제
    EIP_COUNT=0
    for ALLOCATION_ID in $ORPHANED_EIPS; do
        log_warning "Elastic IP 해제 중: $ALLOCATION_ID"
        if aws ec2 release-address \
            --allocation-id "$ALLOCATION_ID" \
            --region us-east-1 \
            2>/dev/null; then
            log_success "  ✓ 해제됨: $ALLOCATION_ID"
            ((EIP_COUNT++))
        else
            log_error "  ✗ 해제 실패: $ALLOCATION_ID"
        fi
    done

    if [ $EIP_COUNT -gt 0 ]; then
        log_success "총 $EIP_COUNT개의 Elastic IP를 해제했습니다"
    fi
}

# 함수: ALB 삭제 보호 해제 (destroy 전)
disable_alb_deletion_protection() {
    log_info "ALB 삭제 보호를 해제하는 중입니다..."

    # ALB 조회 (Terraform 상태에서 생성된 ALB)
    ALB_ARNS=$(aws elbv2 describe-load-balancers \
        --region us-east-1 \
        --query 'LoadBalancers[?LoadBalancerName==`lian-date-prod-alb`].LoadBalancerArn' \
        --output text 2>/dev/null)

    if [ -z "$ALB_ARNS" ]; then
        log_warning "⚠️  ALB를 찾을 수 없습니다 (이미 삭제되었을 수 있음)"
        return 0
    fi

    # 각 ALB의 삭제 보호 해제
    for ALB_ARN in $ALB_ARNS; do
        log_info "ALB 해제 중: $ALB_ARN"
        if aws elbv2 modify-load-balancer-attributes \
            --load-balancer-arn "$ALB_ARN" \
            --attributes Key=deletion_protection.enabled,Value=false \
            --region us-east-1 2>/dev/null; then
            log_success "  ✓ ALB 삭제 보호 해제됨"
        else
            log_warning "  ✗ ALB 삭제 보호 해제 실패 (이미 삭제되었거나 오류 발생)"
        fi
    done
}

log_info "Terraform을 실행합니다..."
echo ""

# apply 액션 전에 고아 Elastic IP 정리
if [ "$1" == "apply" ]; then
    cleanup_orphaned_eips
    echo ""
fi

# destroy 액션 전에 ALB 삭제 보호 해제
if [ "$1" == "destroy" ]; then
    log_warning "⚠️  Destroy 전 리소스 정리를 진행합니다..."
    disable_alb_deletion_protection
    echo ""
fi

# 인자가 있으면 그대로 전달, 없으면 plan 실행
if [ $# -eq 0 ]; then
    log_info "액션이 지정되지 않았습니다. 'plan'을 실행합니다."
    terraform plan
else
    terraform "$@"
fi

echo ""
log_success "Terraform 작업 완료!"

# apply 실행 시 Docker 이미지 빌드 및 푸시 옵션 제공
if [ "$1" == "apply" ]; then
    echo ""
    log_info "Docker 이미지를 빌드하고 ECR에 푸시하시겠습니까?"
    log_warning "이 작업은 시간이 걸릴 수 있습니다 (5-10분)"
    read -p "계속하시겠습니까? (y/n) " -n 1 -r
    echo ""

    if [[ $REPLY =~ ^[Yy]$ ]]; then
        log_info "Docker 이미지 빌드 및 ECR 푸시 시작..."
        cd "$SCRIPT_DIR"

        if [ -f "scripts/push-to-ecr.sh" ]; then
            ./scripts/push-to-ecr.sh
        else
            log_error "scripts/push-to-ecr.sh 파일을 찾을 수 없습니다"
            exit 1
        fi
    else
        log_info "Docker 이미지 푸시를 건너뜁니다."
        log_info "나중에 수동으로 실행하려면: ./scripts/push-to-ecr.sh"
    fi
fi

echo ""
log_success "모든 작업 완료!"
