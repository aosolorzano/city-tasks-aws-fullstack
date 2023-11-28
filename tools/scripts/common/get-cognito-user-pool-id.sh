#!/bin/bash
set -e

cognito_user_pool_id=$(aws cognito-idp list-user-pools --max-results 1 --output text  \
  --query "UserPools[?contains(Name, 'CityUserPool')].[Id]"                           \
  --profile "$AWS_IDP_PROFILE")
if [ -z "$cognito_user_pool_id" ]; then
  echo ""
  echo "ERROR: Not Cognito User Pool ID was found with name: 'CityUserPool'."
  sh "$WORKING_DIR"/tools/scripts/helper/1_revert-automated-scripts.sh
  exit 1
fi

echo "$cognito_user_pool_id"
