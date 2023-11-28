#!/bin/bash
set -e

alb_domain_name=$(aws cloudformation describe-stacks --stack-name "city-tasks-$AWS_WORKLOADS_ENV" \
  --query "Stacks[0].Outputs[?OutputKey=='PublicLoadBalancerDNSName'].OutputValue"                \
  --output text \
  --profile "$AWS_WORKLOADS_PROFILE")
if [ -z "$alb_domain_name" ]; then
  echo ""
  echo "ERROR: No ALB Domain Name found."
  exit 0
fi

echo "$alb_domain_name"
