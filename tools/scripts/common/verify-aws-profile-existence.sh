#!/bin/bash
set -e

AWS_PROFILE=$1

if [ -z "$AWS_PROFILE" ]; then
  echo "ERROR: AWS Profile is required."
  exit 1
fi

### VERIFYING AWS PROFILE CONFIGURATION
if [ ! -f ~/.aws/config ] || ! grep -q "$AWS_PROFILE" ~/.aws/config; then
  echo ""
  echo "ERROR: The profile '$AWS_PROFILE' is not configured."
  echo ""
  exit 1
fi
