#!/usr/bin/env bash
set -e

cd "$WORKING_DIR" || {
  echo "Error moving to the Project's root directory."
  exit 1
}

read -r -p '>> Enter the <AWS Profile> for DNS management: [default] ' aws_route53_profile
if [ -z "$aws_route53_profile" ]; then
  aws_route53_profile='default'
fi

### VERIFYING DNS NAME VARIABLE
if [ -z "$SERVER_DOMAIN_NAME" ]; then
  read -r -p '>> Enter the <Domain Name> to operate: ' server_domain_name
  if [ -z "$server_domain_name" ]; then
    echo ""
    echo "ERROR: Domain Name is required."
    exit 0
  fi
else
  server_domain_name=$SERVER_DOMAIN_NAME
fi
server_fqdn="$AWS_WORKLOADS_ENV.$server_domain_name"

### VERIFYING DNS NAME VARIABLE
if [ -z "$ALB_DOMAIN_NAME" ]; then
  read -r -p '>> Enter the ALB <Domain Name> to delete: ' alb_domain_name
  if [ -z "$alb_domain_name" ]; then
    echo ""
    echo "ERROR: ALB <Domain Name> is required."
    exit 0
  fi
fi

echo ""
echo "GETTING HOSTED ZONE-ID FROM AWS..."
hosted_zone_id=$(aws route53 list-hosted-zones-by-name \
  --dns-name "$server_domain_name"  \
  --profile "$aws_route53_profile"  \
  --output text                     \
  --query "HostedZones[?contains(Name, '$server_domain_name')].[Id]")
if [ -z "$hosted_zone_id" ]; then
  echo ""
  echo "ERROR: Hosted Zone not found for domain: '$server_domain_name'.
               Please, create it first and then, use the 'Helper Menu', option 6.
  "
  exit 0
fi
echo "- Hosted Zone: $hosted_zone_id"

### UPDATING RECORD-SET FILE
sed -i'.bak' -e "s/record-set-action/DELETE/g; s/server-name-fqdn/$server_fqdn/g; s/alb-domain-name/$alb_domain_name/g" \
      "$WORKING_DIR"/tools/aws/route53/tasks-api-alb-record-set-operation.json
rm -f "$WORKING_DIR"/tools/aws/route53/tasks-api-alb-record-set-operation.json.bak

### REGISTERING RECORD SET ON ROUTE53
echo ""
echo "SENDING RECORD-SET OPERATION TO ROUTE 53..."
hosted_zone_id=$(echo "$hosted_zone_id" | cut -d'/' -f3)
aws route53 change-resource-record-sets   \
  --hosted-zone-id "$hosted_zone_id"      \
  --change-batch file://"$WORKING_DIR"/tools/aws/route53/tasks-api-alb-record-set-operation.json \
  --profile "$aws_route53_profile" > /dev/null
echo "DONE!"

### CLEANING RECORD-SET FILE
cat "$WORKING_DIR"/tools/scripts/templates/route53/tasks-api-alb-record-set-operation.json > \
    "$WORKING_DIR"/tools/aws/route53/tasks-api-alb-record-set-operation.json
