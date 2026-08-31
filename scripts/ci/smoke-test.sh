#!/usr/bin/env bash
set -euo pipefail

# Simple CI smoke test: start springboot jar and check /actuator/health and trace headers
ROOT_DIR=$(cd "$(dirname "$0")/../.." && pwd)
cd "$ROOT_DIR"

# Build the springboot module
mvn -pl springboot -DskipTests -T 1C package -B

JAR=$(ls springboot/target/*springboot-*-SNAPSHOT.jar 2>/dev/null | head -n1)
if [ -z "$JAR" ]; then
  echo "Springboot jar not found"
  exit 1
fi

# Start app in background
java -jar "$JAR" &
PID=$!

echo "Started springboot (pid=$PID), waiting for startup..."
# wait for actuator to be available
for i in {1..30}; do
  HTTP_CODE=$(curl -sS -o /dev/null -w "%{http_code}" http://localhost:8080/actuator/health || true)
  if [ "$HTTP_CODE" = "200" ]; then
    echo "Actuator healthy"
    break
  fi
  sleep 1
done

# Call health and check headers
RESP=$(curl -s -D - "http://localhost:8080/actuator/health" -o /dev/null || true)
if echo "$RESP" | grep -qi "X-Request-Id"; then
  echo "X-Request-Id header present"
else
  echo "X-Request-Id header missing" >&2
  kill $PID || true
  exit 2
fi

if echo "$RESP" | grep -qi "traceparent"; then
  echo "traceparent header present"
else
  echo "traceparent header missing" >&2
  kill $PID || true
  exit 3
fi

# Clean up
kill $PID || true
exit 0
