#!/usr/bin/env bash
set -euo pipefail

usage() {
  cat <<'EOF'
사용법: ./stop.sh [all|frontend|backend]

기본값: all
설명:
  all      - frontend, backend 중지
  frontend - frontend만 중지
  backend  - backend만 중지
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

docker compose stop "${services[@]}"
