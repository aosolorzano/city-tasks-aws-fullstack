#!/bin/bash

echo ""
echo "CREATING EVENTBRIDGE RULE..."
awslocal events put-rule                      \
  --name 'city-events-function-rule'    \
  --event-pattern "{\"source\":[\"com.hiperium.city.tasks.api\"],\"detail-type\":[\"TaskExecutionEvent\"]}"

echo ""
echo "DONE!"
