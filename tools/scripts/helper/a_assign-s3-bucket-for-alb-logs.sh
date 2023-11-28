#!/bin/bash
set -e

cd "$WORKING_DIR" || {
  echo "Error moving to the application's root directory."
  exit 1
}

read -r -p "To create <S3 Bucket> to store ALB logs enter 'c' otherwise enter 'u' to use existing one: [c/U] " s3_operation
s3_operation=$(echo "$s3_operation" | tr '[:upper:]' '[:lower:]')

if [[ "$s3_operation" =~ ^(c|create)$ ]]; then
  read -r -p ">> Please, enter the new <Bucket> name: [city-tasks-alb-logs-$AWS_WORKLOADS_ENV]" s3_bucket_name
  if [ -z "$s3_bucket_name" ]; then
    s3_bucket_name="city-tasks-alb-logs-$AWS_WORKLOADS_ENV"
  fi
  ### CREATE THE S3 BUCKET
  echo ""
  echo "CREATING S3 BUCKET..."
  aws s3api create-bucket           \
      --bucket "$s3_bucket_name"    \
      --profile "$AWS_WORKLOADS_PROFILE" > /dev/null
  echo "DONE!"
else
  read -r -p ">> Please, enter the existing <S3 Bucket> name: [city-tasks-alb-logs-$AWS_WORKLOADS_ENV]" s3_bucket_name
  if [ -z "$s3_bucket_name" ]; then
    s3_bucket_name="city-tasks-alb-logs-$AWS_WORKLOADS_ENV"
  fi
fi

### UPDATE S3 BUCKET POLICY
workloads_account_id=$(aws configure get sso_account_id --profile "$AWS_WORKLOADS_PROFILE")
sed -i'.bak' -e "s/AWS_ACCOUNT_ID/$workloads_account_id/g; s/BUCKET_NAME/$s3_bucket_name/g" \
      "$WORKING_DIR"/tools/aws/iam/s3-alb-access-logs-policy.json
rm -f "$WORKING_DIR"/tools/aws/iam/s3-alb-access-logs-policy.json.bak

echo ""
echo "ASSIGNING S3 BUCKET POLICY..."
echo "- S3 Bucket to use: $s3_bucket_name"
aws s3api put-bucket-policy                                                       \
    --bucket "$s3_bucket_name"                                                    \
    --policy file://"$WORKING_DIR"/tools/aws/iam/s3-alb-access-logs-policy.json   \
    --profile "$AWS_WORKLOADS_PROFILE" > /dev/null
echo "DONE!"

echo ""
echo "UPDATING COPILOT TO USE THE S3 BUCKET..."
sed -i'.bak' -e "s/BUCKET_NAME/$s3_bucket_name/g"   \
      "$WORKING_DIR"/copilot/environments/"$AWS_WORKLOADS_ENV"/manifest.yml
rm -f "$WORKING_DIR"/copilot/environments/"$AWS_WORKLOADS_ENV"/manifest.yml.bak
echo "DONE!"

### CLEANING ALB ACCESS LOGS PERMISSIONS FILE
cat "$WORKING_DIR"/tools/scripts/templates/iam/s3-alb-access-logs-policy.json > \
    "$WORKING_DIR"/tools/aws/iam/s3-alb-access-logs-policy.json
