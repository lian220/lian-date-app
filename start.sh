#!/usr/bin/env bash
set -euo pipefail

usage() {
  cat <<'EOF'
사용법: ./start.sh [all|frontend|backend] [-p|--project 프로젝트명]

기본값: all
설명:
  all      - frontend, backend 재빌드 후 실행
  frontend - frontend만 재빌드 후 실행
  backend  - backend만 재빌드 후 실행
  -p, --project  - docker compose 프로젝트명 지정 (병렬 실행용)
예시:
  ./start.sh all -p lian-date-app-main
  ./start.sh backend --project lian-date-app-feature-1
EOF
}

target="all"
project_name=""

while [[ $# -gt 0 ]]; do
  case "$1" in
    all|frontend|backend)
      target="$1"
      shift
      ;;
    -p|--project)
      project_name="${2:-}"
      if [[ -z "$project_name" ]]; then
        echo "프로젝트명을 지정해주세요: -p <프로젝트명>"
        exit 1
      fi
      shift 2
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      echo "알 수 없는 옵션: $1"
      usage
      exit 1
      ;;
  esac
done

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
  *)
    echo "알 수 없는 옵션: ${target}"
    usage
    exit 1
    ;;
esac

if [[ -n "${project_name}" ]]; then
  docker compose -p "${project_name}" build --parallel "${services[@]}"
  docker compose -p "${project_name}" up -d --no-build "${services[@]}"
else
  docker compose build --parallel "${services[@]}"
  docker compose up -d --no-build "${services[@]}"
fi
