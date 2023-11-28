#!/bin/bash
set -e

cd "$WORKING_DIR" || {
  echo "Error moving to the Tasks Service directory."
  exit 1
}

echo ""
echo "GETTING INFORMATION FROM AWS. PLEASE WAIT..."

### GETTING ALB DOMAIN BEFORE DELETING COPILOT APPLICATION
alb_domain_name=$(sh "$WORKING_DIR"/tools/scripts/common/get-alb-domain-name.sh)
echo "- ALB Domain: $alb_domain_name"

echo ""
echo "DELETING COPILOT APPLICATION FROM AWS..."
export AWS_PROFILE="$AWS_WORKLOADS_PROFILE"
copilot app delete    \
  --name city-tasks   \
  --yes
rm -f "$WORKING_DIR"/copilot/.workspace
echo "DONE!"

echo ""
echo "DELETING SAM APPLICATION FROM AWS..."
sam delete                                                      \
  --stack-name city-events-function-"$AWS_WORKLOADS_ENV"        \
  --config-env "$AWS_WORKLOADS_ENV"                             \
  --no-prompts                                                  \
  --profile "$AWS_WORKLOADS_PROFILE"
rm -rf "$WORKING_DIR"/.aws-sam

echo ""
echo "DELETING PENDING BUCKETS FROM S3..."
aws s3api list-buckets --query "Buckets[?contains(Name, 'city-tasks-') || contains(Name, 'aws-sam-')].[Name]" \
  --output text \
  --profile "$AWS_WORKLOADS_PROFILE" | while read -r bucket_name; do
  sh "$WORKING_DIR"/tools/scripts/helper/c_delete_s3_bucket.sh "$bucket_name"
done
echo "DONE!"

echo ""
echo "DELETING PENDING STACKS FROM CLOUDFORMATION..."
aws cloudformation delete-stack                 \
  --stack-name 'aws-sam-cli-managed-default'    \
  --profile "$AWS_WORKLOADS_PROFILE"
echo "DONE!"

echo ""
echo "DELETING PENDING LOG-GROUPS FROM CLOUDWATCH..."
aws logs describe-log-groups --output text \
  --query "logGroups[?contains(logGroupName, 'city-tasks-') || contains(Name, 'city-events-')].[logGroupName]" \
  --profile "$AWS_WORKLOADS_PROFILE" | while read -r log_group_name; do
  echo "- Deleting log-group: $log_group_name"
  aws logs delete-log-group               \
    --log-group-name "$log_group_name"    \
    --profile "$AWS_WORKLOADS_PROFILE"
done
echo "DONE!"

### DELETE ALB DOMAIN FROM ROUTE53
if [ "$alb_domain_name" ]; then
  read -r -p "Do you want to <delete> the ALB domain name from Route53? [Y/n] " delete_alb_domain_name
  delete_alb_domain_name=$(echo "$delete_alb_domain_name" | tr '[:upper:]' '[:lower:]')

  if [[ "$delete_alb_domain_name" =~ ^(no|n)$ ]]; then
    echo ""
    echo ">> No problem at all. You can delete it later using 'Helper Menu', option 8."
  else
    export ALB_DOMAIN_NAME="$alb_domain_name"
    sh "$WORKING_DIR"/tools/scripts/helper/8-delete-alb-domain-from-route53.sh
  fi
fi

### REVERTING CONFIGURATION FILES
sh "$WORKING_DIR"/tools/scripts/helper/1_revert-automated-scripts.sh
