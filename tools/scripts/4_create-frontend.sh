#!/bin/bash
set -e

cd "$WORKING_DIR" || {
  echo "Error moving to the project's root directory."
  exit 1
}

### REVERT CHANGES IN CONFIGURATION FILES
sh "$WORKING_DIR"/tools/scripts/helper/1_revert-automated-scripts.sh

echo ""
echo "INSTALLING NODEJS DEPENDENCIES FOR THE IONIC/ANGULAR APP..."
echo ""
pnpm install

#### MOVING TO THE APP DIRECTORY TO INIT AMPLIFY
cd "$WORKING_DIR"/apps/city-tasks-app || {
  echo "Error moving to the Tasks App 'frontend' directory."
  exit 1
}

clear
echo ""
echo "INITIALIZING AMPLIFY PROJECT ON AWS..."
echo ""
amplify init

clear
echo ""
echo "GETTING ADDITIONAL INFO FROM AWS. PLEASE WAIT..."

### GETTING ALB DOMAIN NAME
alb_domain_name=$(sh "$WORKING_DIR"/tools/scripts/common/get-alb-domain-name.sh)
echo "- ALB Domain: $alb_domain_name"

### GETTING COGNITO USER POOL ID
cognito_user_pool_id=$(sh "$WORKING_DIR"/tools/scripts/common/get-cognito-user-pool-id.sh)
echo "- Cognito User Pool ID: $cognito_user_pool_id"

### GETTING COGNITO CLIENT ID WEB
cognito_app_client_id_web=$(sh "$WORKING_DIR"/tools/scripts/common/get-cognito-client-id-web.sh "$cognito_user_pool_id")
echo "- Cognito Client ID Web: $cognito_app_client_id_web"

### GETTING AMPLIFY PROJECT ID
amplify_app_id=$(aws amplify list-apps --query "apps[?name=='CityTasksApp'].appId" \
  --output text \
  --profile "$AWS_WORKLOADS_PROFILE")
if [ -z "$amplify_app_id" ]; then
  echo "ERROR: No Amplify ID found." >&2
  exit 1
fi
echo "- Amplify Project ID: $amplify_app_id"

echo ""
echo "UPDATING IONIC/ANGULAR CONFIGURATION FILES..."
cat "$WORKING_DIR"/tools/scripts/templates/angular/environment-"$AWS_WORKLOADS_ENV".ts > \
    "$WORKING_DIR"/apps/city-tasks-app/src/environments/environment.ts

workloads_aws_region=$(aws configure get region --profile "$AWS_WORKLOADS_PROFILE")
sed -i'.bak' -e "s/ALB_API_ENDPOINT/$alb_domain_name/g; s/IDP_AWS_REGION/$workloads_aws_region/g; s/COGNITO_USER_POOL_ID/$cognito_user_pool_id/g; s/COGNITO_APP_CLIENT_ID_WEB/$cognito_app_client_id_web/g; s/AWS_WORKLOADS_ENV/$AWS_WORKLOADS_ENV/g; s/AMPLIFY_APP_ID/$amplify_app_id/g;" \
      "$WORKING_DIR"/apps/city-tasks-app/src/environments/environment.ts
rm -f "$WORKING_DIR"/apps/city-tasks-app/src/environments/environment.ts.bak
echo "DONE!"

echo ""
echo "PUSHING CHANGES TO GIT REPOSITORY..."
echo ""
git add .
git commit -m "Adding Amplify configuration for '$AWS_WORKLOADS_ENV' environment."
git push --set-upstream origin main
echo ""

clear
echo "
DONE!

CREATING AMPLIFY HOSTING ON AWS...

NOTE: The following procedure is manual. The script will ask to select options.
      Select 'Hosting with Amplify Console' and 'Continuous deployment'.
      Finally, the script will open a browser window to complete the process on the AWS console.

      Please, press any key to continue..."
read -n 1 -s -r -p ""
echo ""
amplify add hosting

echo "
IMPORTANT!!: Add the previous Amplify Hosting URL to your Cognito IdP for OAuth redirection.

             Press any key to continue...
"
read -n 1 -s -r -p ""
clear
echo ""
echo "DONE!"
