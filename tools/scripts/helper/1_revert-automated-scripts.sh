#!/bin/bash

cd "$WORKING_DIR" || {
  echo "Error moving to the application's root directory."
  exit 1
}
TEMPLATE_DIR="$WORKING_DIR"/tools/scripts/templates

cat "$TEMPLATE_DIR"/copilot/city-tasks-api/manifest.yml                    > "$WORKING_DIR"/copilot/city-tasks-api/manifest.yml
cat "$TEMPLATE_DIR"/copilot/environments/"$AWS_WORKLOADS_ENV"/manifest.yml > "$WORKING_DIR"/copilot/environments/"$AWS_WORKLOADS_ENV"/manifest.yml
cat "$TEMPLATE_DIR"/iam/s3-alb-access-logs-policy.json                     > "$WORKING_DIR"/tools/aws/iam/s3-alb-access-logs-policy.json
cat "$TEMPLATE_DIR"/iam/ecs-task-eventbridge-put-policy.json               > "$WORKING_DIR"/tools/aws/iam/ecs-task-eventbridge-put-policy.json
cat "$TEMPLATE_DIR"/route53/tasks-api-alb-record-set-operation.json        > "$WORKING_DIR"/tools/aws/route53/tasks-api-alb-record-set-operation.json
cat "$TEMPLATE_DIR"/envoy/envoy-local.yaml                                 > "$WORKING_DIR"/apis/city-tasks-api-proxy/envoy.yaml
cat "$TEMPLATE_DIR"/angular/environment-local.ts                           > "$WORKING_DIR"/apps/city-tasks-app/src/environments/environment.ts
cat "$TEMPLATE_DIR"/docker/env/tasks-api-dev.env                           > "$WORKING_DIR"/apis/city-tasks-api/local.env

echo "subjectAltName = DNS:dev.hiperium.cloud" > "$WORKING_DIR"/tools/certs/v3.ext
