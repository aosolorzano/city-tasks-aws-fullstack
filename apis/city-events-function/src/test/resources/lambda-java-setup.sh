#!/bin/bash

echo ""
echo "CREATING SQS-DLQ FOR LAMBDA FUNCTION..."
awslocal sqs create-queue                 \
  --queue-name 'EventsDLQ'

echo ""
echo "CREATING LAMBDA ROLE..."
awslocal iam create-role                  \
  --role-name 'lambda-role'               \
  --assume-role-policy-document '{"Version": "2012-10-17","Statement": [{ "Effect": "Allow", "Principal": {"Service": "lambda.amazonaws.com"}, "Action": "sts:AssumeRole"}]}'

echo ""
echo "ADDING DYNAMODB ACCESS TO LAMBDA ROLE..."
awslocal iam put-role-policy              \
    --role-name 'lambda-role'             \
    --policy-name DynamoDBWritePolicy     \
    --policy-document '{"Version": "2012-10-17", "Statement": [{"Effect": "Allow", "Action": "dynamodb:PutItem", "Resource": "arn:aws:dynamodb:us-east-1:000000000000:table/Events"}]}'

echo ""
echo "WAITING FOR LAMBDA RESOURCES FROM BUILDER CONTAINER..."
FUNCTION_PATH="/tmp/city/shared/city-events-function.jar"
DEPENDENCIES_PATH="/tmp/city/shared/dependencies.zip"
while [ ! -f "$FUNCTION_PATH" ] || [ ! -f "$DEPENDENCIES_PATH" ]; do
  sleep 1
done
echo "DONE!"

echo ""
echo "CREATING LAMBDA LAYER..."
awslocal lambda publish-layer-version             \
  --layer-name 'city-events-function-layer'       \
  --description "Events function dependencies"    \
  --zip-file fileb://$DEPENDENCIES_PATH           \
  --compatible-runtimes 'java17'                  \
  --license-info 'MIT'

echo ""
echo "CREATING LAMBDA FUNCTION..."
awslocal lambda create-function                                                               \
  --function-name 'city-events-function'                                                      \
  --runtime 'java17'                                                                          \
  --architectures 'arm64'                                                                     \
  --zip-file fileb://$FUNCTION_PATH                                                           \
  --handler 'org.springframework.cloud.function.adapter.aws.FunctionInvoker::handleRequest'   \
  --layers 'arn:aws:lambda:us-east-1:000000000000:layer:city-events-function-layer:1'         \
  --dead-letter-config 'TargetArn=arn:aws:sqs:us-east-1:000000000000:EventsDLQ'               \
  --timeout 20                                                                                \
  --memory-size 512                                                                           \
  --role 'arn:aws:iam::000000000000:role/lambda-role'                                         \
  --environment 'Variables={AWS_ENDPOINT_OVERRIDE=http://host.docker.internal:4566,SPRING_PROFILES_ACTIVE=dev,CITY_TIME_ZONE_ID=-05:00}'

echo ""
echo "CREATING FUNCTION LOG-GROUP..."
awslocal logs create-log-group                                    \
  --log-group-name '/aws/lambda/city-events-function'

echo ""
echo "CREATING FUNCTION LOG-STREAM..."
awslocal logs create-log-stream                                   \
  --log-group-name '/aws/lambda/city-events-function'             \
  --log-stream-name 'city-events-function-log-stream'

echo ""
echo "GRANTING EVENTBRIDGE TO INVOKE LAMBDA FUNCTION..."
awslocal lambda add-permission                                    \
    --function-name 'city-events-function'                        \
    --statement-id events-lambda-permission                       \
    --action 'lambda:InvokeFunction'                              \
    --principal events.amazonaws.com                              \
    --source-arn 'arn:aws:events:us-east-1:000000000000:rule/city-events-function-rule'

echo ""
echo "DONE!"
