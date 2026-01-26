#!/usr/bin/env bash
set -euo pipefail

usage() {
  cat <<'EOF'
사용법: ./start.sh [all|frontend|backend|db] [-p|--project 프로젝트명]

기본값: all
설명:
  all      - DB, frontend, backend 모두 실행
  db       - DB만 실행 (postgres, chroma)
  frontend - frontend만 재빌드 후 실행
  backend  - backend만 재빌드 후 실행
  -p, --project  - docker compose 프로젝트명 지정 (병렬 실행용)
예시:
  ./start.sh all -p lian-date-app-main
  ./start.sh backend --project lian-date-app-feature-1
  ./start.sh db
EOF
}

target="all"
project_name=""

while [[ $# -gt 0 ]]; do
  case "$1" in
    all|frontend|backend|db)
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
    # DB 먼저 실행 (profile db 포함)
    echo "DB 실행 중..."
    if [[ -n "${project_name}" ]]; then
      docker compose -p "${project_name}" --profile db up -d postgres chroma
    else
      docker compose --profile db up -d postgres chroma
    fi

    # Backend, Frontend 빌드 및 실행
    echo "Backend, Frontend 빌드 및 실행 중..."
    services=(backend frontend)
    if [[ -n "${project_name}" ]]; then
      docker compose -p "${project_name}" build --parallel "${services[@]}"
      docker compose -p "${project_name}" up -d --no-build "${services[@]}"
    else
      docker compose build --parallel "${services[@]}"
      docker compose up -d --no-build "${services[@]}"
    fi
    ;;
  db)
    # DB만 실행
    echo "DB 실행 중..."
    if [[ -n "${project_name}" ]]; then
      docker compose -p "${project_name}" --profile db up -d postgres chroma
    else
      docker compose --profile db up -d postgres chroma
    fi
    ;;
  frontend)
    services=(frontend)
    if [[ -n "${project_name}" ]]; then
      docker compose -p "${project_name}" build --parallel "${services[@]}"
      docker compose -p "${project_name}" up -d --no-build "${services[@]}"
    else
      docker compose build --parallel "${services[@]}"
      docker compose up -d --no-build "${services[@]}"
    fi
    ;;
  backend)
    services=(backend)
    if [[ -n "${project_name}" ]]; then
      docker compose -p "${project_name}" build --parallel "${services[@]}"
      docker compose -p "${project_name}" up -d --no-build "${services[@]}"
    else
      docker compose build --parallel "${services[@]}"
      docker compose up -d --no-build "${services[@]}"
    fi
    ;;
  *)
    echo "알 수 없는 옵션: ${target}"
    usage
    exit 1
    ;;
esac
