#!/bin/bash
# .env.prod 파일을 읽어서 TF_VAR_* 환경 변수로 export

ENV_FILE="${1:-.env.prod}"

if [ ! -f "$ENV_FILE" ]; then
  echo "Error: $ENV_FILE 파일이 없습니다."
  exit 1
fi

# .env 파일에서 환경 변수 읽어서 TF_VAR_로 변환
while IFS='=' read -r key value; do
  # 주석과 빈 줄 무시
  [[ $key =~ ^#.*$ ]] && continue
  [[ -z $key ]] && continue

  # 값에서 따옴표 제거
  value="${value%\"}"
  value="${value#\"}"
  value="${value%\'}"
  value="${value#\'}"

  # TF_VAR_ prefix 추가해서 export
  export "TF_VAR_${key}=${value}"
  echo "Loaded: TF_VAR_${key}"
done < "$ENV_FILE"

echo ""
echo "환경 변수 로드 완료. 이제 terraform 명령을 실행하세요."
