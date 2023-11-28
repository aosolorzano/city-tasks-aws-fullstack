#!/bin/bash
set -e

cd "$WORKING_DIR" || {
  echo "Error moving to the application's root directory."
  exit 1
}

### VERIFY CA AND CSR CERTIFICATES FILES
CA_CERTS_DIR="$WORKING_DIR"/tools/certs
TASKS_CERTS_DIR="$WORKING_DIR"/tools/certs/"$AWS_WORKLOADS_ENV"
if [ ! -f "$CA_CERTS_DIR"/ca-cert.pem ] || [ ! -f "$TASKS_CERTS_DIR"/server-key.pem ] || [ ! -f "$TASKS_CERTS_DIR"/server-cert-"$AWS_WORKLOADS_ENV".pem ]; then
  echo ""
  echo "ERROR: Not TLS certificates was found for the '$AWS_WORKLOADS_ENV' environment."
  echo "You can create <TLS Certificates> using the 'Helper Menu', option 3."
  exit 1
fi

### REVERTING CONFIGURATION FILES
sh "$WORKING_DIR"/tools/scripts/helper/1_revert-automated-scripts.sh

### ASKING TO PRUNE DOCKER SYSTEM
sh "$WORKING_DIR"/tools/scripts/helper/2_docker-system-prune.sh

echo ""
echo "GETTING INFORMATION FROM AWS. PLEASE WAIT A MOMENT..."

### GETTING COGNITO USER POOL ID
cognito_user_pool_id=$(sh "$WORKING_DIR"/tools/scripts/common/get-cognito-user-pool-id.sh)
echo "- Cognito User Pool ID: $cognito_user_pool_id"

### GETTING COGNITO CLIENT ID WEB
cognito_app_client_id_web=$(sh "$WORKING_DIR"/tools/scripts/common/get-cognito-client-id-web.sh "$cognito_user_pool_id")
echo "- Cognito Client ID Web: $cognito_app_client_id_web"

echo ""
echo "CONFIGURING DOCKER-COMPOSE ENVIRONMENT VARIABLES..."
idp_aws_region=$(aws configure get region --profile "$AWS_IDP_PROFILE")
sed -i'.bak' -e "s/IDP_AWS_REGION/$idp_aws_region/g; s/COGNITO_USER_POOL_ID/$cognito_user_pool_id/g" \
  "$WORKING_DIR"/apis/city-tasks-api/local.env
rm -f "$WORKING_DIR"/apis/city-tasks-api/local.env.bak
echo "DONE!"

echo ""
echo "UPDATING IONIC/ANGULAR CONFIG FILES..."
sed -i'.bak' -e "s/IDP_AWS_REGION/$idp_aws_region/g; s/COGNITO_USER_POOL_ID/$cognito_user_pool_id/g; s/COGNITO_APP_CLIENT_ID_WEB/$cognito_app_client_id_web/g;" \
  "$WORKING_DIR"/apps/city-tasks-app/src/environments/environment.ts
rm -f "$WORKING_DIR"/apps/city-tasks-app/src/environments/environment.ts.bak
echo "DONE!"

echo ""
echo "STARING DOCKER COMPOSE..."
echo ""
docker compose up --build -d
echo ""
echo "DONE!"

echo ""
echo "CHECKING IF THE IONIC/ANGULAR APP IS ACTIVE..."
sh "$WORKING_DIR"/tools/scripts/helper/b_check-tasks-app.sh

echo ""
echo "NOTE: Consider to run the following command in a new terminal to see the service's logs:"
echo "docker compose logs -f"
echo ""
echo "DONE!"
