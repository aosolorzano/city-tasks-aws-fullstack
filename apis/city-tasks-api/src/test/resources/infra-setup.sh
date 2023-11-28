#!/bin/bash

echo ""
echo "CREATING DEVICES TABLE..."
awslocal dynamodb create-table              \
  --table-name 'Devices'                    \
  --attribute-definitions                   \
    AttributeName=id,AttributeType=S        \
  --key-schema                              \
    AttributeName=id,KeyType=HASH           \
  --billing-mode PAY_PER_REQUEST

echo ""
echo "WRITING DEVICE ITEMS..."
awslocal dynamodb batch-write-item          \
    --request-items file:///var/lib/localstack/api-data.json
