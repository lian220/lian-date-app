#!/bin/bash

# Terraform 배포 스크립트 (Production 환경)
# 사용법: ./scripts/terraform-deploy.sh [action]
# 예시: ./scripts/terraform-deploy.sh plan
#      ./scripts/terraform-deploy.sh apply
#      ./scripts/terraform-deploy.sh all
#      ./scripts/terraform-deploy.sh destroy

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

# 함수: 사용법 출력
usage() {
    echo "사용법: $0 [action]"
    echo ""
    echo "액션 (action):"
    echo "  init     - Terraform 초기화"
    echo "  plan     - 실행 계획 확인"
    echo "  apply    - 인프라 생성/변경"
    echo "  destroy  - 인프라 삭제"
    echo "  all      - init -> plan -> apply (전체 실행)"
    echo ""
    echo "예시:"
    echo "  $0 plan"
    echo "  $0 apply"
    echo "  $0 all"
    echo "  $0 destroy"
    exit 1
}

# 인자 확인
if [ $# -lt 1 ]; then
    usage
fi

# Production 환경만 사용
ENVIRONMENT="prod"
ACTION=$1

# 액션 검증
VALID_ACTIONS=("init" "plan" "apply" "destroy" "all")
if [[ ! " ${VALID_ACTIONS[@]} " =~ " ${ACTION} " ]]; then
    log_error "잘못된 액션입니다: $ACTION"
    log_info "사용 가능한 액션: ${VALID_ACTIONS[*]}"
    exit 1
fi

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

log_info "환경: $ENVIRONMENT (Production)"
log_info "액션: $ACTION"
log_info "Terraform 디렉토리: $TERRAFORM_DIR"
log_info "변수 파일: $TFVARS_FILE"
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

# IAM 권한 확인 및 자동 추가
check_and_fix_iam_permissions() {
    log_info "IAM 권한 확인 중..."
    
    # 현재 사용자 이름 추출 (arn:aws:iam::ACCOUNT:user/USERNAME 형식)
    local user_name=$(echo "$AWS_USER" | awk -F'/' '{print $NF}')
    
    if [ -z "$user_name" ] || [ "$user_name" == "unknown" ]; then
        log_warning "IAM 사용자 이름을 확인할 수 없습니다. 수동으로 권한을 확인하세요."
        return 0
    fi
    
    log_info "IAM 사용자: $user_name"
    
    # 현재 연결된 정책 확인
    local attached_policies=$(aws iam list-attached-user-policies --user-name "$user_name" --query 'AttachedPolicies[*].PolicyArn' --output text 2>/dev/null || echo "")
    
    local needs_autoscaling=false
    local needs_sns=false
    
    # Application AutoScaling 권한 확인
    if echo "$attached_policies" | grep -q "AWSApplicationAutoScalingFullAccess"; then
        log_success "Application AutoScaling 권한: 이미 설정됨"
    else
        log_warning "Application AutoScaling 권한: 없음"
        needs_autoscaling=true
    fi
    
    # SNS 권한 확인
    if echo "$attached_policies" | grep -q "AmazonSNSFullAccess"; then
        log_success "SNS 권한: 이미 설정됨"
    else
        log_warning "SNS 권한: 없음"
        needs_sns=true
    fi
    
    # 필요한 권한이 있으면 자동 추가
    if [ "$needs_autoscaling" = true ] || [ "$needs_sns" = true ]; then
        echo ""
        log_warning "필요한 IAM 권한이 없습니다. 자동으로 추가합니다..."
        echo ""
        
        if [ "$needs_autoscaling" = true ]; then
            log_info "Application AutoScaling 권한 추가 중..."
            log_warning "AWS는 Application AutoScaling에 대한 단일 FullAccess 정책을 제공하지 않습니다."
            log_info "인라인 정책으로 필요한 권한을 추가합니다..."
            
            # 인라인 정책 생성
            local policy_doc='{
                "Version": "2012-10-17",
                "Statement": [
                    {
                        "Effect": "Allow",
                        "Action": [
                            "application-autoscaling:*",
                            "ecs:DescribeServices",
                            "ecs:UpdateService"
                        ],
                        "Resource": "*"
                    }
                ]
            }'
            
            local policy_name="${user_name}-ApplicationAutoScalingPolicy"
            
            # 기존 정책 삭제 (있다면)
            aws iam delete-user-policy --user-name "$user_name" --policy-name "$policy_name" 2>/dev/null || true
            
            # 새 정책 추가
            local attach_result=$(echo "$policy_doc" | aws iam put-user-policy \
                --user-name "$user_name" \
                --policy-name "$policy_name" \
                --policy-document file:///dev/stdin 2>&1)
            local attach_exit_code=$?
            
            if [ $attach_exit_code -eq 0 ]; then
                log_success "Application AutoScaling 인라인 정책 추가 완료"
            else
                log_error "Application AutoScaling 권한 추가 실패"
                log_error "에러 메시지: $attach_result"
                log_error ""
                log_error "수동으로 AWS Console에서 인라인 정책을 추가하세요:"
                log_error "  IAM → Users → $user_name → Add permissions → Create inline policy"
                log_error "  JSON 탭에서 다음 정책을 붙여넣으세요:"
                echo "$policy_doc"
                exit 1
            fi
        fi
        
        if [ "$needs_sns" = true ]; then
            log_info "SNS 권한 추가 중..."
            # 먼저 관리형 정책 시도
            local attach_result=$(aws iam attach-user-policy \
                --user-name "$user_name" \
                --policy-arn arn:aws:iam::aws:policy/AmazonSNSFullAccess 2>&1)
            local attach_exit_code=$?
            
            if [ $attach_exit_code -eq 0 ]; then
                log_success "SNS 권한 추가 완료 (관리형 정책)"
            elif echo "$attach_result" | grep -qi "EntityAlreadyExists\|already exists"; then
                log_warning "SNS 권한이 이미 존재합니다."
            elif echo "$attach_result" | grep -qi "NoSuchEntity"; then
                # 관리형 정책이 없으면 인라인 정책 사용
                log_warning "SNS 관리형 정책을 찾을 수 없습니다. 인라인 정책으로 추가합니다..."
                
                local sns_policy_doc='{
                    "Version": "2012-10-17",
                    "Statement": [
                        {
                            "Effect": "Allow",
                            "Action": [
                                "sns:CreateTopic",
                                "sns:DeleteTopic",
                                "sns:GetTopicAttributes",
                                "sns:SetTopicAttributes",
                                "sns:Subscribe",
                                "sns:Unsubscribe",
                                "sns:ListSubscriptionsByTopic",
                                "sns:Publish"
                            ],
                            "Resource": "*"
                        }
                    ]
                }'
                
                local sns_policy_name="${user_name}-SNSPolicy"
                
                # 기존 정책 삭제 (있다면)
                aws iam delete-user-policy --user-name "$user_name" --policy-name "$sns_policy_name" 2>/dev/null || true
                
                # 새 정책 추가
                local sns_attach_result=$(echo "$sns_policy_doc" | aws iam put-user-policy \
                    --user-name "$user_name" \
                    --policy-name "$sns_policy_name" \
                    --policy-document file:///dev/stdin 2>&1)
                local sns_attach_exit_code=$?
                
                if [ $sns_attach_exit_code -eq 0 ]; then
                    log_success "SNS 인라인 정책 추가 완료"
                else
                    log_error "SNS 권한 추가 실패"
                    log_error "에러 메시지: $sns_attach_result"
                    exit 1
                fi
            else
                log_error "SNS 권한 추가 실패"
                log_error "에러 메시지: $attach_result"
                log_error ""
                log_error "수동으로 AWS Console에서 인라인 정책을 추가하세요:"
                log_error "  IAM → Users → $user_name → Add permissions → Create inline policy"
                exit 1
            fi
        fi
        
        echo ""
        log_info "IAM 권한 변경사항이 적용되기까지 시간이 걸릴 수 있습니다..."
        log_info "권한 전파를 위해 20초 대기 중..."
        
        # 최대 3번 재시도 (총 60초 대기)
        local max_retries=3
        local retry_count=0
        local all_permissions_ok=false
        
        while [ $retry_count -lt $max_retries ] && [ "$all_permissions_ok" = false ]; do
            sleep 20
            retry_count=$((retry_count + 1))
            
            log_info "권한 확인 시도 $retry_count/$max_retries..."
            local updated_policies=$(aws iam list-attached-user-policies --user-name "$user_name" --query 'AttachedPolicies[*].PolicyArn' --output text 2>/dev/null || echo "")
            
            local autoscaling_ok=false
            local sns_ok=false
            
            if [ "$needs_autoscaling" = true ]; then
            # 인라인 정책 확인
            local inline_policies=$(aws iam list-user-policies --user-name "$user_name" --query 'PolicyNames' --output text 2>/dev/null || echo "")
            if echo "$inline_policies" | grep -q "ApplicationAutoScalingPolicy"; then
                log_success "✓ Application AutoScaling 권한 확인됨"
                autoscaling_ok=true
            else
                log_warning "✗ Application AutoScaling 권한이 아직 확인되지 않습니다."
            fi
            else
                autoscaling_ok=true
            fi
            
            if [ "$needs_sns" = true ]; then
            # SNS 권한 확인 (관리형 정책 또는 인라인 정책)
            local sns_attached=$(echo "$updated_policies" | grep -q "AmazonSNSFullAccess" && echo "yes" || echo "no")
            local sns_inline=$(aws iam list-user-policies --user-name "$user_name" --query 'PolicyNames' --output text 2>/dev/null | grep -q "SNSPolicy" && echo "yes" || echo "no")
            
            if [ "$sns_attached" = "yes" ] || [ "$sns_inline" = "yes" ]; then
                log_success "✓ SNS 권한 확인됨"
                sns_ok=true
            else
                log_warning "✗ SNS 권한이 아직 확인되지 않습니다."
            fi
            else
                sns_ok=true
            fi
            
            if [ "$autoscaling_ok" = true ] && [ "$sns_ok" = true ]; then
                all_permissions_ok=true
                log_success "모든 권한이 확인되었습니다!"
                break
            fi
        done
        
        if [ "$all_permissions_ok" = false ]; then
            log_error "권한이 아직 적용되지 않았습니다."
            log_error "IAM 권한 전파에 시간이 더 필요할 수 있습니다."
            log_error ""
            log_error "다음 중 하나를 시도하세요:"
            log_error "1. 몇 분 후 다시 실행"
            log_error "2. AWS Console에서 권한을 직접 확인하고 추가"
            log_error "3. 다음 명령어로 수동 추가:"
            log_error "   aws iam attach-user-policy --user-name $user_name --policy-arn arn:aws:iam::aws:policy/AWSApplicationAutoScalingFullAccess"
            log_error "   aws iam attach-user-policy --user-name $user_name --policy-arn arn:aws:iam::aws:policy/AmazonSNSFullAccess"
            exit 1
        fi
    fi
    
    echo ""
}

# IAM 권한 확인 및 자동 수정
check_and_fix_iam_permissions

# Terraform 초기화
terraform_init() {
    log_info "Terraform 초기화 중..."
    if [ -d "terraform.d/plugins" ]; then
        terraform init -plugin-dir=terraform.d/plugins
    else
        terraform init
    fi
    log_success "Terraform 초기화 완료"
    echo ""
}

# Terraform 계획
terraform_plan() {
    log_info "Terraform 실행 계획 생성 중..."
    terraform plan -var-file="$TFVARS_FILE" -out=tfplan
    log_success "실행 계획 생성 완료"
    echo ""
}

# Terraform 적용
terraform_apply() {
    log_info "Terraform 적용 중..."
    
    # Stale plan 파일은 항상 삭제하고 새로 생성 (안전한 방법)
    if [ -f "tfplan" ]; then
        log_warning "기존 실행 계획 파일이 있습니다. state 변경 가능성이 있어 새로 생성합니다."
        rm -f tfplan
    fi
    
    # 새 Plan 파일 생성
    log_info "새로운 실행 계획 생성 중..."
    terraform plan -var-file="$TFVARS_FILE" -out=tfplan
    
    # Plan 파일로 Apply 실행
    log_info "실행 계획 적용 중..."
    terraform apply tfplan
    
    # Plan 파일 정리
    rm -f tfplan
    log_success "Terraform 적용 완료"
    echo ""
}

# Terraform 삭제
terraform_destroy() {
    log_warning "⚠️  경고: 이 작업은 모든 인프라를 삭제합니다!"
    read -p "정말로 삭제하시겠습니까? (yes 입력): " confirm
    
    if [ "$confirm" != "yes" ]; then
        log_info "삭제가 취소되었습니다."
        exit 0
    fi
    
    log_info "Terraform 인프라 삭제 중..."
    terraform destroy -var-file="$TFVARS_FILE" -auto-approve
    log_success "인프라 삭제 완료"
    echo ""
}

# 전체 실행
terraform_all() {
    log_info "=== Terraform 전체 실행 시작 ==="
    echo ""
    
    terraform_init
    terraform_plan
    
    log_warning "인프라를 생성/변경하시겠습니까?"
    read -p "계속하려면 'yes'를 입력하세요: " confirm
    
    if [ "$confirm" != "yes" ]; then
        log_info "적용이 취소되었습니다."
        exit 0
    fi
    
    terraform_apply
    
    log_success "=== Terraform 전체 실행 완료 ==="
}

# 액션 실행
case "$ACTION" in
    init)
        terraform_init
        ;;
    plan)
        terraform_init
        terraform_plan
        ;;
    apply)
        terraform_init
        terraform_apply
        ;;
    destroy)
        terraform_init
        terraform_destroy
        ;;
    all)
        terraform_all
        ;;
    *)
        log_error "알 수 없는 액션: $ACTION"
        usage
        ;;
esac

log_success "작업 완료!"
