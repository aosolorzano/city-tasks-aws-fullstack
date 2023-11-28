#!/bin/bash

echo ""
echo "CREATING EVENTS TABLE..."
awslocal dynamodb create-table                \
  --table-name 'Events'                       \
  --attribute-definitions                     \
    AttributeName=id,AttributeType=S          \
    AttributeName=deviceId,AttributeType=S    \
  --key-schema                                \
    AttributeName=id,KeyType=HASH             \
    AttributeName=deviceId,KeyType=RANGE      \
  --billing-mode PAY_PER_REQUEST

echo ""
echo "WRITING EVENTS ITEMS..."
awslocal dynamodb batch-write-item            \
    --request-items file:///var/lib/localstack/events-data.json
