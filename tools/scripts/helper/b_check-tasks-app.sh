#!/bin/bash
set -e

cd "$WORKING_DIR" || {
  echo "Error moving to the application's root directory."
  exit 1
}

SERVICE_NAME="city-tasks-app"
MAX_WAIT=300
INTERVAL=10
COUNT=0

echo ""
echo "Waiting for the '$SERVICE_NAME' container to be active..."
while true; do
  if docker compose ps | grep "$SERVICE_NAME" | grep "Up"; then
    echo "The 'city-tasks-app' container is active."
    break
  fi
  COUNT=$((COUNT + INTERVAL))
  if [ $COUNT -ge $MAX_WAIT ]; then
    echo "Maximum wait time reached. The service is not active."
    exit 0
  fi
  sleep $INTERVAL
done

### OPENING THE APP IN THE BROWSER
CITY_TASKS_APP_URL="http://localhost/index.html"
MAX_ATTEMPTS=5
ATTEMPT=0
INTERVAL=2

echo ""
echo "Validating the access to the City Tasks App..."
while ! curl -s --head --request GET $CITY_TASKS_APP_URL | grep "200 OK" > /dev/null; do
  ATTEMPT=$((ATTEMPT + 1))
  if [ $ATTEMPT -eq $MAX_ATTEMPTS ]; then
    echo "Maximum attempts reached. The service is not active."
    exit 0
  fi
  sleep $INTERVAL
done

echo ""
echo "City Tasks App is active. Opening it in your browser..."
open $CITY_TASKS_APP_URL
echo "DONE!"
