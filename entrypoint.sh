#!/usr/bin/env bash
set -euo pipefail

nginx -g "daemon off;" &
NGINX_PID=$!

java ${JAVA_OPTS:-"-XX:+UseContainerSupport -XX:MaxRAMPercentage=75 -XX:InitialRAMPercentage=25"} -jar /app/app.jar &
APP_PID=$!

cleanup() {
  kill -TERM "$APP_PID" 2>/dev/null || true
  kill -TERM "$NGINX_PID" 2>/dev/null || true
}

trap cleanup SIGINT SIGTERM

wait -n "$APP_PID" "$NGINX_PID"
STATUS=$?
cleanup
wait || true
exit "$STATUS"
