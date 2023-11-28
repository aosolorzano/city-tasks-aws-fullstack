#!/bin/bash

echo ""
echo "GETTING LAMBDA FUNCTION ARN..."
lambda_arn=$(awslocal lambda get-function       \
  --function-name 'city-events-function'        \
  --query 'Configuration.FunctionArn'           \
  --output text)
echo "ARN: $lambda_arn"

echo ""
echo "ADDING LAMBDA FUNCTION AS EVENTBRIDGE TARGET..."
awslocal events put-targets                     \
  --rule 'city-events-function-rule'            \
  --targets Id=1,Arn="$lambda_arn"

echo ""
echo "DONE!"
