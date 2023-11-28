#!/bin/bash
set -e

cognito_user_pool_id=$1
if [ -z "$cognito_user_pool_id" ]; then
  echo ""
  echo "ERROR: Cognito User Pool ID is required."
  exit 1
fi

cognito_app_client_id_web=$(aws cognito-idp list-user-pool-clients              \
  --user-pool-id "$cognito_user_pool_id" --max-results 1 --output text          \
  --query "UserPoolClients[?contains(ClientName, 'app_clientWeb')].[ClientId]"  \
  --profile "$AWS_IDP_PROFILE")
if [ -z "$cognito_app_client_id_web" ]; then
  echo "ERROR: No Cognito Client ID Web found."
  exit 1
fi

echo "$cognito_app_client_id_web"
