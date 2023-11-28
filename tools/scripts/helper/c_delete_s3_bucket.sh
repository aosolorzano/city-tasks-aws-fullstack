#!/bin/bash
set -e

cd "$WORKING_DIR" || {
  echo "Error moving to the application's root directory."
  exit 1
}

BUCKET=$1
if [ -z "$BUCKET" ]; then
  echo "ERROR: No bucket name provided."
  exit 1
fi

### FUNCTION THAT DELETES ALL VERSIONS AND DELETE MARKERS
delete_versions() {
    local bucket=$1
    local jq_filter=$2
    local json
    json=$(aws s3api list-object-versions --bucket "$bucket" --profile "$AWS_WORKLOADS_PROFILE")
    echo "$json" | jq -r "$jq_filter" | while read -r key versionId; do
        aws s3api delete-object --bucket "$bucket" --key "$key" --version-id "$versionId" --profile "$AWS_WORKLOADS_PROFILE" > /dev/null
    done
}

### REMOVE ALL OBJECTS AND DELETE MARKERS
delete_versions "$BUCKET" '.Versions[] | "\(.Key) \(.VersionId)"'
delete_versions "$BUCKET" '.DeleteMarkers[] | "\(.Key) \(.VersionId)"'

### EMPTY BUCKET
echo "- Emptying bucket: $BUCKET"
aws s3 rm "s3://$BUCKET" --recursive --profile "$AWS_WORKLOADS_PROFILE"

### DELETE BUCKET
echo "- Deleting bucket: $BUCKET"
aws s3 rb "s3://$BUCKET" --force --profile "$AWS_WORKLOADS_PROFILE"
