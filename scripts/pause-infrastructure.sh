#!/bin/bash

# 스마트 인프라 일시 정지 스크립트
# RDS만 남기고 나머지 리소스 삭제
# 사용법: ./scripts/pause-infrastructure.sh

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

# Terraform 상태 확인
log_info "Terraform 상태 확인 중..."
if ! terraform state list > /dev/null 2>&1; then
    log_error "Terraform state를 찾을 수 없습니다."
    log_info "인프라가 배포되지 않았거나 이미 삭제되었습니다."
    exit 1
fi

# RDS 상태 확인
log_info "RDS 인스턴스 확인 중..."
RDS_STATUS=$(aws rds describe-db-instances \
  --db-instance-identifier lian-date-prod-db \
  --query 'DBInstances[0].DBInstanceStatus' \
  --output text 2>/dev/null || echo "not-found")

if [ "$RDS_STATUS" = "not-found" ]; then
    log_warning "RDS 인스턴스를 찾을 수 없습니다."
    log_warning "일반 terraform destroy를 수행합니다."
    read -p "계속하시겠습니까? (y/N): " confirm
    if [ "$confirm" != "y" ] && [ "$confirm" != "Y" ]; then
        log_info "일시 정지가 취소되었습니다."
        exit 0
    fi
    terraform destroy
    exit 0
fi

log_success "RDS 상태: $RDS_STATUS"
echo ""

# 비용 정보 표시
log_warning "💰 비용 비교"
echo "  현재 (실행 중):     ~\$157/월"
echo "  일시 정지 후:       ~\$30/월"
echo "  절감액:            ~\$127/월 (81% 절감)"
echo ""
log_info "📦 일시 정지 시:"
log_info "  ✅ RDS만 보존 (데이터 유지)"
log_info "  ❌ VPC, ECS, ALB, NAT 등 삭제"
log_info "  ⏱️  재시작 시간: 5-10분"
echo ""

# 확인
log_warning "🛑 스마트 일시 정지를 실행하시겠습니까?"
log_warning "RDS를 제외한 모든 인프라가 삭제됩니다."
echo ""
read -p "계속하려면 'PAUSE'를 입력하세요: " confirm

if [ "$confirm" != "PAUSE" ]; then
    log_info "일시 정지가 취소되었습니다."
    exit 0
fi

echo ""
log_info "🛑 스마트 일시 정지를 시작합니다..."
echo ""

# 1단계: RDS 정보 백업
log_info "[1/4] RDS 정보 백업 중..."
cat > "$BACKUP_FILE" << EOF
# RDS Backup Information
# Created: $(date)
# AWS Account: $AWS_ACCOUNT

DB_INSTANCE_ID=lian-date-prod-db
DB_SUBNET_GROUP=lian-date-prod-db-subnet-group
DB_INSTANCE_ADDRESS=$(terraform output -raw db_instance_address 2>/dev/null || echo "")
DB_ENDPOINT=$(terraform output -raw db_instance_endpoint 2>/dev/null || echo "")
DB_NAME=$(terraform output -raw db_name 2>/dev/null || echo "")
EOF

log_success "RDS 정보 저장: $BACKUP_FILE"
cat "$BACKUP_FILE"
echo ""

# 2단계: RDS를 Terraform state에서 제거
log_info "[2/4] RDS를 Terraform state에서 제거 중..."
log_warning "주의: AWS에서는 삭제되지 않습니다. State에서만 제거됩니다."

terraform state rm module.rds.aws_db_instance.this 2>/dev/null || log_warning "RDS 인스턴스 state 제거 실패 (이미 제거됨?)"
terraform state rm module.rds.aws_db_subnet_group.this 2>/dev/null || log_warning "RDS 서브넷 그룹 state 제거 실패 (이미 제거됨?)"

log_success "RDS state 제거 완료"
echo ""

# 3단계: 나머지 리소스 삭제
log_info "[3/4] 나머지 인프라 삭제 중 (terraform destroy)..."
log_warning "이 작업은 5-10분 정도 걸립니다..."
echo ""

terraform destroy -auto-approve

echo ""
log_success "[3/4] 인프라 삭제 완료"
echo ""

# 4단계: RDS 상태 확인
log_info "[4/4] RDS 보존 확인 중..."
RDS_FINAL_STATUS=$(aws rds describe-db-instances \
  --db-instance-identifier lian-date-prod-db \
  --query 'DBInstances[0].[DBInstanceIdentifier, DBInstanceStatus, Endpoint.Address]' \
  --output table 2>/dev/null || echo "")

if [ -n "$RDS_FINAL_STATUS" ]; then
    log_success "RDS가 정상적으로 보존되었습니다:"
    echo "$RDS_FINAL_STATUS"
else
    log_error "RDS 상태를 확인할 수 없습니다."
fi

echo ""
log_success "✅ 스마트 일시 정지 완료!"
echo ""
log_info "📊 현재 상태:"
log_info "  ✅ RDS: 보존됨 (데이터 유지)"
log_info "  ❌ VPC, ECS, ALB, NAT: 삭제됨"
log_info "  💰 월 비용: ~\$30 (RDS만)"
echo ""
log_info "📋 백업 정보: $BACKUP_FILE"
log_info "▶️  재시작: ./scripts/resume-infrastructure.sh"
