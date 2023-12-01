#!/bin/bash
set -e

cd "$WORKING_DIR" || {
  echo "Error moving to the project's root directory."
  exit 1
}

### VERIFYING CA AND CSR CERTIFICATES FILES
CA_CERTS_DIR="$WORKING_DIR"/tools/certs
TASKS_CERTS_DIR=tools/certs/"$AWS_WORKLOADS_ENV"
if [ ! -f "$CA_CERTS_DIR"/ca-cert.pem ] || [ ! -f "$TASKS_CERTS_DIR"/server-key.pem ] || [ ! -f "$TASKS_CERTS_DIR"/server-cert-"$AWS_WORKLOADS_ENV".pem ]; then
  echo ""
  echo "ERROR: Not TLS certificates was found for the '$AWS_WORKLOADS_ENV' environment."
  echo "You can create <TLS Certificates> using the 'Helper Menu', option 3."
  exit 1
fi

### REVERTING CONFIGURATION FILES
sh "$WORKING_DIR"/tools/scripts/helper/1_revert-automated-scripts.sh

echo ""
read -r -p 'Enter the <Domain Name> used in your <CSR> certificate: ' server_domain_name
if [ -z "$server_domain_name" ]; then
  echo "ERROR: The <Domain Name> is required."
  exit 1
fi
server_fqdn="$AWS_WORKLOADS_ENV.$server_domain_name"

### UPDATE COPILOT MANIFEST FILE WITH THE SERVER-FQDN
sed -i'.bak' -e "s/SERVER_DOMAIN_NAME/$server_domain_name/g; s/SERVER_FQDN/$server_fqdn/g;"  \
      "$WORKING_DIR"/copilot/city-tasks-api/manifest.yml
rm -f "$WORKING_DIR"/copilot/city-tasks-api/manifest.yml.bak

### ASKING TO STORE ALB ACCESS-LOGS IN S3 BUCKET
sh "$WORKING_DIR"/tools/scripts/helper/a_assign-s3-bucket-for-alb-logs.sh

### ASKING TO PRUNE DOCKER SYSTEM
sh "$WORKING_DIR"/tools/scripts/helper/2_docker-system-prune.sh

echo ""
echo "GETTING ADDITIONAL INFO FROM AWS. PLEASE WAIT..."

### GETTING CSR CERTIFICATE ARN
acm_arn=$(aws acm list-certificates       \
  --includes  keyTypes=EC_prime256v1      \
  --profile "$AWS_WORKLOADS_PROFILE"      \
  --output text                           \
  --query "CertificateSummaryList[?contains(DomainName, '$server_domain_name')].[CertificateArn]")
if [ -z "$acm_arn" ]; then
  echo ""
  echo "ERROR: Not ACM Certificate was found for domain: '$server_domain_name'."
  echo "You can import your <CSR> certificates using the 'Helper Menu', option 4."
  sh "$WORKING_DIR"/tools/scripts/helper/1_revert-automated-scripts.sh
  exit 1
fi
acm_certificate_number=$(echo "$acm_arn" | cut -d'/' -f2)
echo "- TLS certificate: $acm_certificate_number"

### GETTING COGNITO USER POOL ID
cognito_user_pool_id=$(sh "$WORKING_DIR"/tools/scripts/common/get-cognito-user-pool-id.sh)
echo "- Cognito User Pool ID: $cognito_user_pool_id"

### UPDATING API MANIFEST FILE WITH COGNITO USER POOL ID
idp_aws_region=$(aws configure get region --profile "$AWS_IDP_PROFILE")
sed -i'.bak' -e "s/AWS_REGION/$idp_aws_region/g; s/COGNITO_USER_POOL_ID/$cognito_user_pool_id/g"  \
      "$WORKING_DIR"/copilot/city-tasks-api/manifest.yml
rm -f "$WORKING_DIR"/copilot/city-tasks-api/manifest.yml.bak
echo "- Cognito IdP Region: $idp_aws_region"

### UPDATING ENV MANIFEST FILE WITH ACM ARN
workloads_aws_region=$(aws configure get region --profile "$AWS_WORKLOADS_PROFILE")
workloads_aws_account_id=$(aws configure get sso_account_id --profile "$AWS_WORKLOADS_PROFILE")
sed -i'.bak' -e "s/AWS_REGION/$workloads_aws_region/g; s/AWS_ACCOUNT_ID/$workloads_aws_account_id/g; s/ACM_CERTIFICATE_NUMBER/$acm_certificate_number/g" \
      "$WORKING_DIR"/copilot/environments/"$AWS_WORKLOADS_ENV"/manifest.yml
rm -f "$WORKING_DIR"/copilot/environments/"$AWS_WORKLOADS_ENV"/manifest.yml.bak
echo "- Workloads Region: $workloads_aws_region"

echo ""
echo "UPDATING ENVOY CONFIGURATION FILE FOR AWS..."
cat "$WORKING_DIR"/tools/scripts/templates/envoy/envoy-aws.yaml > "$WORKING_DIR"/apis/city-tasks-api-proxy/envoy.yaml
echo "DONE!"

echo ""
echo "GENERATING HIPERIUM CITY BUILDER IMAGE..."
echo ""
docker build -t hiperium/city-image-builder:1.8.0 -f tools/docker/city-builder/Dockerfile .
echo ""
echo "DONE!"

echo ""
echo "VALIDATING SAM TEMPLATE..."
sam validate --lint
echo "DONE!"

echo ""
echo "BUILDING SAM PROJECT LOCALLY..."
rm -rf node_modules
rm -rf apps/city-tasks-app/node_modules
sam build --config-env "$AWS_WORKLOADS_ENV"

echo ""
echo "DEPLOYING SAM PROJECT INTO AWS..."
sam deploy                                      \
  --config-env "$AWS_WORKLOADS_ENV"             \
  --disable-rollback                            \
  --profile "$AWS_WORKLOADS_PROFILE"

echo ""
echo "INITIALIZING COPILOT PROJECT ON AWS..."
export AWS_PROFILE="$AWS_WORKLOADS_PROFILE"
copilot init                                    \
  --app city-tasks                              \
  --name city-tasks-api                         \
  --type 'Load Balanced Web Service'            \
  --port 8080                                   \
  --tag '1.8.0'                                 \
  --dockerfile './apis/city-tasks-api/Dockerfile-native'
echo ""
echo "DONE!"

echo ""
echo "INITIALIZING COPILOT ENVIRONMENT ON AWS..."
copilot env init                        \
  --app city-tasks                      \
  --name "$AWS_WORKLOADS_ENV"           \
  --profile "$AWS_WORKLOADS_PROFILE"    \
  --default-config
echo ""
echo "DONE!"

echo ""
echo "DEPLOYING COPILOT ECS CLUSTER ON AWS..."
copilot env deploy                      \
  --app city-tasks                      \
  --name "$AWS_WORKLOADS_ENV"
echo ""
echo "DONE!"

echo ""
echo "DEPLOYING COPILOT API SERVICE ON ECS..."
copilot deploy                          \
  --app city-tasks                      \
  --name city-tasks-api                 \
  --env "$AWS_WORKLOADS_ENV"            \
  --tag '1.8.0'                         \
  --no-rollback                         \
  --resource-tags project=Hiperium,copilot-application-type=city-tasks-api,copilot-application-version=1.8.0
echo ""
echo "DONE!"

### UPDATING IAM POLICY TO ALLOW ECS-TASK TO ACCESS EVENTBRIDGE
sed -i'.bak' -e "s/AWS_REGION/$workloads_aws_region/g; s/AWS_ACCOUNT_ID/$workloads_aws_account_id/g" \
      "$WORKING_DIR"/tools/aws/iam/ecs-task-eventbridge-put-policy.json
rm -f "$WORKING_DIR"/tools/aws/iam/ecs-task-eventbridge-put-policy.json.bak

echo ""
echo "ALLOWING EVENTBRIDGE TO ACCESS ECS TASK..."
ecs_task_role_name=$(aws iam list-roles --output text \
  --query "Roles[?contains(RoleName, 'city-tasks-api-TaskRole')].[RoleName]" \
  --profile "$AWS_WORKLOADS_PROFILE")
if [ -z "$ecs_task_role_name" ]; then
  echo ""
  echo "WARNING: ECS Task Role was not found, which contains the name: 'city-tasks-api-TaskRole'."
  echo "         Please, assign the following IAM-Policy to the ECS-Task role:"
  echo ""
  cat "$WORKING_DIR"/tools/aws/iam/ecs-task-eventbridge-put-policy.json
  echo ""
else
  aws iam put-role-policy --role-name "$ecs_task_role_name"                     \
    --policy-name city-tasks-"$AWS_WORKLOADS_ENV"-api-EventBridge-PutPolicy     \
    --policy-document file://"$WORKING_DIR"/tools/aws/iam/ecs-task-eventbridge-put-policy.json \
    --profile "$AWS_WORKLOADS_PROFILE"
fi
echo "DONE!"

### LOADING DEVICE TEST DATA INTO DYNAMODB
if [ "$AWS_WORKLOADS_ENV" == "dev" ] || [ "$AWS_WORKLOADS_ENV" == "test" ]; then
  echo ""
  echo "WRITING DEVICE TESTING DATA INTO DYNAMODB..."
  aws dynamodb batch-write-item \
    --request-items file://"$WORKING_DIR"/apis/city-tasks-api/src/test/resources/data-setup.json \
    --profile "$AWS_WORKLOADS_PROFILE" > /dev/null
  echo "DONE!"
fi

echo ""
echo "GETTING ALB DNS NAME..."
alb_domain_name=$(sh "$WORKING_DIR"/tools/scripts/common/get-alb-domain-name.sh)
if [ -z "$alb_domain_name" ]; then
  echo ""
  echo "Not <ALB DNS Name> was found in '$AWS_WORKLOADS_ENV' environment."
  echo "You can register your <ALB DNS Name> later using 'Helper Menu', option 6."
  exit 0
fi
echo "- Domain: $alb_domain_name"

echo ""
read -r -p "Do you want to <register> your ALB domain on Route53? [Y/n] " register_alb_domain_name
register_alb_domain_name=$(echo "$register_alb_domain_name" | tr '[:upper:]' '[:lower:]')

if [[ "$register_alb_domain_name" =~ ^(no|n)$ ]]; then
  echo ""
  echo ">> No problem at all. You can manage later using 'Helper Menu', option 6 and 7."
else
  export SERVER_DOMAIN_NAME="$server_domain_name"
  export ALB_DOMAIN_NAME="$alb_domain_name"
  sh "$WORKING_DIR"/tools/scripts/helper/6_7_register-alb-domain-in-route53.sh
fi

### REVERTING CONFIGURATION FILES
sh "$WORKING_DIR"/tools/scripts/helper/1_revert-automated-scripts.sh
