#!/bin/bash

# 인프라 재시작 스크립트
# 나머지 리소스를 재생성하고 기존 RDS를 다시 연결
# 사용법: ./scripts/resume-infrastructure.sh

set -e

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 로그 함수
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

# 스크립트 디렉토리
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
TERRAFORM_DIR="$PROJECT_ROOT/terraform"
BACKUP_FILE="$PROJECT_ROOT/.rds_backup.txt"

# AWS 자격 증명 확인
log_info "AWS 자격 증명 확인 중..."
if ! aws sts get-caller-identity > /dev/null 2>&1; then
    log_error "AWS 자격 증명이 설정되지 않았습니다."
    log_info "다음 명령어로 자격 증명을 설정하세요:"
    log_info "  aws configure"
    exit 1
fi

AWS_ACCOUNT=$(aws sts get-caller-identity --query Account --output text 2>/dev/null || echo "unknown")
log_success "AWS 계정: $AWS_ACCOUNT"
echo ""

# Terraform 디렉토리 확인
if [ ! -d "$TERRAFORM_DIR" ]; then
    log_error "Terraform 디렉토리를 찾을 수 없습니다: $TERRAFORM_DIR"
    exit 1
fi

cd "$TERRAFORM_DIR"

# RDS 백업 파일 확인
if [ ! -f "$BACKUP_FILE" ]; then
    log_warning "RDS 백업 파일을 찾을 수 없습니다: $BACKUP_FILE"
    log_warning "일반 terraform apply를 수행합니다."
    read -p "계속하시겠습니까? (y/N): " confirm
    if [ "$confirm" != "y" ] && [ "$confirm" != "Y" ]; then
        log_info "재시작이 취소되었습니다."
        exit 0
    fi
    terraform init
    terraform apply
    exit 0
fi

# RDS 백업 정보 로드
log_info "RDS 백업 정보 로드 중..."
source "$BACKUP_FILE"
log_success "백업 정보 로드 완료"
cat "$BACKUP_FILE"
echo ""

# RDS 상태 확인
log_info "기존 RDS 인스턴스 확인 중..."
RDS_STATUS=$(aws rds describe-db-instances \
  --db-instance-identifier "$DB_INSTANCE_ID" \
  --query 'DBInstances[0].DBInstanceStatus' \
  --output text 2>/dev/null || echo "not-found")

if [ "$RDS_STATUS" = "not-found" ]; then
    log_error "RDS 인스턴스를 찾을 수 없습니다: $DB_INSTANCE_ID"
    log_error "RDS가 삭제되었거나 이름이 변경되었습니다."
    log_info "일반 terraform apply를 실행하여 새로 생성하시겠습니까? (y/N): "
    read -p "" confirm
    if [ "$confirm" = "y" ] || [ "$confirm" = "Y" ]; then
        terraform init
        terraform apply
    fi
    exit 1
fi

log_success "RDS 발견: $DB_INSTANCE_ID (상태: $RDS_STATUS)"
echo ""

# 비용 정보 표시
log_info "💰 비용 비교"
echo "  일시 정지 중:       ~\$30/월"
echo "  재시작 후:         ~\$157/월"
echo "  추가 비용:         ~\$127/월"
echo ""
log_info "📦 재시작 시:"
log_info "  ✅ 기존 RDS 재연결 (데이터 보존)"
log_info "  ✅ VPC, ECS, ALB, NAT 재생성"
log_info "  ⏱️  소요 시간: 5-10분"
echo ""

# 확인
log_warning "▶️  인프라를 재시작하시겠습니까?"
echo ""
read -p "계속하려면 'RESUME'을 입력하세요: " confirm

if [ "$confirm" != "RESUME" ]; then
    log_info "재시작이 취소되었습니다."
    exit 0
fi

echo ""
log_info "▶️  인프라 재시작을 시작합니다..."
echo ""

# 1단계: Terraform 초기화
log_info "[1/4] Terraform 초기화 중..."
terraform init > /dev/null 2>&1
log_success "초기화 완료"
echo ""

# 2단계: 나머지 인프라 재생성 (RDS 제외)
log_info "[2/4] 나머지 인프라 재생성 중..."
log_warning "이 작업은 5-10분 정도 걸립니다..."
log_warning "RDS 생성 오류는 무시됩니다 (이미 존재하므로)..."
echo ""

# terraform apply 실행 (RDS 오류 무시)
terraform apply -auto-approve 2>&1 | tee /tmp/terraform_apply.log || {
    log_warning "Terraform apply 중 오류 발생 (예상된 동작일 수 있음)"
    log_info "RDS 관련 오류인지 확인 중..."

    if grep -q "already exists" /tmp/terraform_apply.log; then
        log_success "RDS는 이미 존재합니다. 계속 진행..."
    else
        log_error "예상치 못한 오류가 발생했습니다."
        log_error "로그를 확인하세요: /tmp/terraform_apply.log"
        exit 1
    fi
}

echo ""
log_success "[2/4] 인프라 재생성 완료"
echo ""

# 3단계: RDS를 Terraform state로 다시 import
log_info "[3/4] RDS를 Terraform state로 다시 import 중..."

# RDS 인스턴스 import
log_info "RDS 인스턴스 import..."
terraform import module.rds.aws_db_instance.this "$DB_INSTANCE_ID" 2>/dev/null || {
    log_warning "RDS 인스턴스 import 실패 (이미 import되었거나 구조 변경됨)"
}

# RDS 서브넷 그룹 import
log_info "RDS 서브넷 그룹 import..."
terraform import module.rds.aws_db_subnet_group.this "$DB_SUBNET_GROUP" 2>/dev/null || {
    log_warning "RDS 서브넷 그룹 import 실패 (이미 import되었거나 구조 변경됨)"
}

log_success "RDS import 완료"
echo ""

# 4단계: 전체 상태 동기화
log_info "[4/4] 전체 상태 동기화 중..."
terraform refresh > /dev/null 2>&1
log_success "상태 동기화 완료"
echo ""

# 배포 확인
log_info "배포 상태 확인 중..."
echo ""

# ECS 서비스 상태
log_info "📊 ECS 서비스 상태:"
aws ecs describe-services \
  --cluster lian-date-prod-cluster \
  --services lian-date-prod-backend-service lian-date-prod-frontend-service \
  --region us-east-1 \
  --query 'services[*].[serviceName, desiredCount, runningCount]' \
  --output table 2>/dev/null || log_warning "ECS 서비스 확인 실패"

echo ""

# RDS 상태
log_info "🗄️  RDS 상태:"
aws rds describe-db-instances \
  --db-instance-identifier "$DB_INSTANCE_ID" \
  --query 'DBInstances[0].[DBInstanceIdentifier, DBInstanceStatus, Endpoint.Address]' \
  --output table 2>/dev/null || log_warning "RDS 상태 확인 실패"

echo ""

# ALB DNS
ALB_DNS=$(terraform output -raw alb_dns_name 2>/dev/null || echo "")
if [ -n "$ALB_DNS" ]; then
    log_info "🌐 ALB 접속 주소:"
    echo "  http://$ALB_DNS"
    echo ""
    log_info "Health Check (2-3분 후):"
    echo "  curl http://$ALB_DNS/health"
fi

echo ""
log_success "✅ 인프라 재시작 완료!"
echo ""
log_info "📊 현재 상태:"
log_info "  ✅ RDS: 재연결됨 (기존 데이터 보존)"
log_info "  ✅ VPC, ECS, ALB, NAT: 재생성 완료"
log_info "  💰 월 비용: ~\$157"
echo ""
log_warning "⏳ ECS 태스크가 시작되는 동안 2-3분 정도 걸릴 수 있습니다."
log_info "서비스 상태 모니터링:"
log_info "  watch -n 5 'aws ecs describe-services --cluster lian-date-prod-cluster --services lian-date-prod-backend-service --query \"services[0].runningCount\"'"
