#!/bin/bash
set -e

### READ CSR DOMAIN NAME AND CSR SERVER FQDN
echo ""
read -r -p 'Enter the <Domain Name> used in your <CSR> certificate: ' server_domain_name
if [ -z "$server_domain_name" ]; then
  echo "ERROR: The <Domain Name> is required."
  exit 1
fi

echo ""
echo "DELETING ACM CERTIFICATE FROM AWS..."
acm_arn=$(aws acm list-certificates   \
  --includes keyTypes=EC_prime256v1   \
  --profile "$AWS_WORKLOADS_PROFILE"  \
  --output text \
  --query "CertificateSummaryList[?contains(DomainName, '$server_domain_name')].[CertificateArn]")
if [ -z "$acm_arn" ]; then
  echo ""
  echo "WARNING: Not ACM Certificate found to delete. Maybe you must delete it manually."
else
  aws acm delete-certificate      \
    --certificate-arn "$acm_arn"  \
    --profile "$AWS_WORKLOADS_PROFILE"
  echo "DONE!"
fi
