#!/usr/bin/env bash
set -euo pipefail

# dateclick-shared-network 없으면 생성 (DB/백엔드/프론트 공용)
ensure_network() {
  if ! docker network inspect dateclick-shared-network &>/dev/null; then
    echo "네트워크 dateclick-shared-network 생성 중..."
    docker network create dateclick-shared-network
  fi
}

# 컨테이너 헬스체크 대기
wait_for_healthy() {
  local container_name="${1:?container_name required}"
  local timeout_seconds="${2:-60}"

  local start_ts
  start_ts="$(date +%s)"

  while true; do
    # 컨테이너가 running이 아니면 실패
    if ! docker ps --filter "name=${container_name}" --filter "status=running" -q | grep -q .; then
      echo "❌ ${container_name} 컨테이너가 실행 중이 아닙니다."
      docker ps -a --filter "name=${container_name}" || true
      docker logs --tail 50 "${container_name}" 2>/dev/null || true
      return 1
    fi

    # healthcheck가 없을 수도 있으니, 있으면 healthy까지 대기
    local health
    health="$(docker inspect -f '{{if .State.Health}}{{.State.Health.Status}}{{else}}no-healthcheck{{end}}' "${container_name}" 2>/dev/null || echo "unknown")"

    case "${health}" in
      healthy)
        return 0
        ;;
      no-healthcheck)
        # healthcheck 없는 컨테이너는 running이면 OK로 간주
        return 0
        ;;
      unhealthy)
        echo "❌ ${container_name} 헬스체크가 unhealthy 입니다."
        docker logs --tail 80 "${container_name}" 2>/dev/null || true
        return 1
        ;;
      starting|unknown)
        # 계속 대기
        ;;
      *)
        # 기타 상태도 대기
        ;;
    esac

    local now_ts
    now_ts="$(date +%s)"
    if (( now_ts - start_ts >= timeout_seconds )); then
      echo "❌ ${container_name} 헬스체크 대기 시간 초과 (${timeout_seconds}s)"
      docker logs --tail 80 "${container_name}" 2>/dev/null || true
      return 1
    fi

    sleep 2
  done
}

# 로컬 포트 열림 대기
wait_for_local_port() {
  local port="${1:?port required}"
  local timeout_seconds="${2:-30}"

  local start_ts
  start_ts="$(date +%s)"

  while true; do
    if is_port_in_use "${port}"; then
      return 0
    fi

    local now_ts
    now_ts="$(date +%s)"
    if (( now_ts - start_ts >= timeout_seconds )); then
      return 1
    fi
    sleep 1
  done
}

# 포트 사용 여부 확인 (LISTEN)
is_port_in_use() {
  local port="${1:?port required}"

  if command -v lsof &>/dev/null; then
    lsof -nP -iTCP:"${port}" -sTCP:LISTEN &>/dev/null
    return $?
  fi

  if command -v nc &>/dev/null; then
    nc -z 127.0.0.1 "${port}" &>/dev/null
    return $?
  fi

  # lsof/nc 둘 다 없으면 포트 체크 생략 (사용 중 아님으로 간주)
  return 1
}

# DB(postgres) 안 떠 있으면 띄우기 (로컬에서는 Chroma 미사용, backend는 NoopPlaceMemory 사용)
ensure_db() {
  # 1) 이미 postgres 컨테이너가 떠 있으면 "정상 기동(healthy)"까지 기다린 뒤 계속
  if docker ps --filter "name=dateclick-postgres" --filter "status=running" -q | grep -q .; then
    echo "Postgres 컨테이너가 이미 실행 중입니다. 정상 기동을 확인 중..."
    wait_for_healthy "dateclick-postgres" 60
    return 0
  fi

  # 2) 컨테이너가 없는데 5432가 사용 중이면(다른 DB가 떠 있음) postgres 기동은 스킵하고,
  #    backend가 호스트의 5432로 붙도록 POSTGRES_HOST를 host.docker.internal로 맞춘다.
  if is_port_in_use 5432; then
    echo "⚠️  5432 포트가 이미 사용 중입니다. postgres 컨테이너는 기동하지 않고 기존 DB를 사용합니다."
    export POSTGRES_HOST="${POSTGRES_HOST:-host.docker.internal}"
    echo "   - 적용: POSTGRES_HOST=${POSTGRES_HOST}"

    if ! wait_for_local_port 5432 10; then
      echo "❌ 5432 포트가 열려있지 않습니다. DB 상태를 확인해주세요."
      return 1
    fi

    return 0
  fi

  # 3) 5432가 비어있으면 postgres만 올리고 healthy까지 기다린다.
  echo "DB가 실행 중이 아닙니다. Postgres 실행 중..."
  if [[ -n "${project_name}" ]]; then
    docker compose -p "${project_name}" --profile db up -d postgres
  else
    docker compose --profile db up -d postgres
  fi

  echo "Postgres 정상 기동을 확인 중..."
  wait_for_healthy "dateclick-postgres" 60
}

usage() {
  cat <<'EOF'
사용법: ./start.sh [all|frontend|backend|db] [-p|--project 프로젝트명]

기본값: all
설명:
  all      - DB, frontend, backend 모두 실행
  db       - DB만 실행 (postgres)
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
    ensure_network
    ensure_db

    # Backend, Frontend 빌드 후 재시작
    echo "Backend, Frontend 빌드 및 재시작 중..."
    services=(backend frontend)
    if [[ -n "${project_name}" ]]; then
      docker compose -p "${project_name}" up -d --build --force-recreate "${services[@]}"
    else
      docker compose up -d --build --force-recreate "${services[@]}"
    fi
    ;;
  db)
    ensure_network
    ensure_db
    ;;
  frontend)
    ensure_network
    ensure_db
    services=(frontend)
    if [[ -n "${project_name}" ]]; then
      docker compose -p "${project_name}" up -d --build --force-recreate "${services[@]}"
    else
      docker compose up -d --build --force-recreate "${services[@]}"
    fi
    ;;
  backend)
    ensure_network
    ensure_db
    services=(backend)
    if [[ -n "${project_name}" ]]; then
      docker compose -p "${project_name}" up -d --build --force-recreate "${services[@]}"
    else
      docker compose up -d --build --force-recreate "${services[@]}"
    fi
    ;;
  *)
    echo "알 수 없는 옵션: ${target}"
    usage
    exit 1
    ;;
esac
