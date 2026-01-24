#!/usr/bin/env bash
set -euo pipefail

usage() {
  cat <<'EOF'
사용법: ./start.sh [all|frontend|backend]

기본값: all
설명:
  all      - frontend, backend 재빌드 후 실행
  frontend - frontend만 재빌드 후 실행
  backend  - backend만 재빌드 후 실행
EOF
}

target="${1:-all}"

case "${target}" in
  all)
    services=(backend frontend)
    ;;
  frontend)
    services=(frontend)
    ;;
  backend)
    services=(backend)
    ;;
  -h|--help)
    usage
    exit 0
    ;;
  *)
    echo "알 수 없는 옵션: ${target}"
    usage
    exit 1
    ;;
esac

for service in "${services[@]}"; do
  container_name="dateclick-${service}"
  if docker ps -a --format '{{.Names}}' | grep -Fxq "${container_name}"; then
    docker rm -f "${container_name}" >/dev/null
  fi
done

docker compose up -d --build "${services[@]}"
