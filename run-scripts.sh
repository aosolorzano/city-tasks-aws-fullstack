#!/bin/bash
set -e

WORKING_DIR=$(cd -P -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd -P)
export WORKING_DIR

function setEnvironmentVariables() {
  echo ""
  if [ -z "$AWS_WORKLOADS_ENV" ]; then
    read -r -p 'Enter the <Environment Name> of your Service:          [dev] ' env_name
    env_name=$(echo "$env_name" | tr '[:upper:]' '[:lower:]')
    if [ -z "$env_name" ]; then
      AWS_WORKLOADS_ENV='dev'
    else
      AWS_WORKLOADS_ENV=$env_name
    fi
    export AWS_WORKLOADS_ENV
  fi

  read -r -p 'Enter the <AWS Profile> for the Service Workloads:     [default] ' aws_workloads_profile
  aws_workloads_profile=$(echo "$aws_workloads_profile" | tr '[:upper:]' '[:lower:]')
  if [ -z "$aws_workloads_profile" ]; then
    AWS_WORKLOADS_PROFILE='default'
  else
    AWS_WORKLOADS_PROFILE=$aws_workloads_profile
  fi
  sh "$WORKING_DIR"/tools/scripts/common/verify-aws-profile-existence.sh "$AWS_WORKLOADS_PROFILE"
  export AWS_WORKLOADS_PROFILE

  if [ -z "$AWS_DEPLOYMENT_TOOLS_PROFILE" ]; then
    read -r -p "Enter the <AWS Profile> for the Deployment Tools:      [$AWS_WORKLOADS_PROFILE] " aws_dep_tools_profile
    aws_dep_tools_profile=$(echo "$aws_dep_tools_profile" | tr '[:upper:]' '[:lower:]')
    if [ -z "$aws_dep_tools_profile" ]; then
      AWS_DEPLOYMENT_TOOLS_PROFILE=$AWS_WORKLOADS_PROFILE
    else
      AWS_DEPLOYMENT_TOOLS_PROFILE=$aws_dep_tools_profile
    fi
    sh "$WORKING_DIR"/tools/scripts/common/verify-aws-profile-existence.sh "$AWS_DEPLOYMENT_TOOLS_PROFILE"
    export AWS_DEPLOYMENT_TOOLS_PROFILE
  fi

  if [ -z "$AWS_IDP_PROFILE" ]; then
    read -r -p 'Enter the <AWS Profile> where Cognito IdP is deployed: [default] ' idp_profile_name
    idp_profile_name=$(echo "$idp_profile_name" | tr '[:upper:]' '[:lower:]')
    if [ -z "$idp_profile_name" ]; then
      AWS_IDP_PROFILE='default'
    else
      AWS_IDP_PROFILE=$idp_profile_name
    fi
    sh "$WORKING_DIR"/tools/scripts/common/verify-aws-profile-existence.sh "$AWS_IDP_PROFILE"
    export AWS_IDP_PROFILE
  fi
}

function verifyEnvironmentVariables() {
  if [ -z "$AWS_WORKLOADS_PROFILE" ] || [ -z "$AWS_DEPLOYMENT_TOOLS_PROFILE" ] ||  [ -z "$AWS_WORKLOADS_ENV" ] || [ -z "$AWS_IDP_PROFILE" ]; then
    clear
    setEnvironmentVariables
    echo ""
  fi
}

helperMenu() {
  echo "
    *********************************************
    **************** Helper Menu ****************
    *********************************************
    1) Revert Automated Files.
    2) Prune Docker System.
    3) Create Self-Signed Certificate.
    4) Import Self-Signed Certificate into ACM.
    5) Delete Self-Signed Certificate from ACM.
    6) Create Record-Set in Route53.
    7) Update Record-Set in Route53.
    8) Delete Record-Set from Route53.
    9) Create Git Connection from AWS.
    ---------------------------------------------
    r) Return.
    q) Quit.
  "
  read -r -p 'Choose an option: ' option
  case $option in
  1)
    sh "$WORKING_DIR"/tools/scripts/helper/1_revert-automated-scripts.sh
    clear
    echo ""
    echo "DONE!"
    echo ""
    helperMenu
    ;;
  2)
    clear
    sh "$WORKING_DIR"/tools/scripts/helper/2_docker-system-prune.sh
    clear
    echo ""
    echo "DONE!"
    echo ""
    helperMenu
    ;;
  3)
    clear
    sh "$WORKING_DIR"/tools/scripts/helper/3_create-tls-certificate.sh
    clear
    echo ""
    echo "DONE!"
    echo ""
    helperMenu
    ;;
  4)
    clear
    sh "$WORKING_DIR"/tools/scripts/helper/4_import-tls-certificate-to-acm.sh
    clear
    echo ""
    echo "DONE!"
    echo ""
    helperMenu
    ;;
  5)
    clear
    sh "$WORKING_DIR"/tools/scripts/helper/5_delete-tls-certificate-from-acm.sh
    clear
    echo ""
    echo "DONE!"
    echo ""
    helperMenu
    ;;
  6)
    clear
    export DNS_OPERATION="CREATE"
    sh "$WORKING_DIR"/tools/scripts/helper/6_7_register-alb-domain-in-route53.sh
    clear
    echo ""
    echo "DONE!"
    echo ""
    helperMenu
    ;;
  7)
    clear
    export DNS_OPERATION="UPSERT"
    sh "$WORKING_DIR"/tools/scripts/helper/6_7_register-alb-domain-in-route53.sh
    clear
    echo ""
    echo "DONE!"
    echo ""
    helperMenu
    ;;
  8)
    clear
    sh "$WORKING_DIR"/tools/scripts/helper/8_delete-alb-domain-from-route53.sh
    clear
    echo ""
    echo "DONE!"
    echo ""
    helperMenu
    ;;
  9)
    clear
    sh "$WORKING_DIR"/tools/scripts/helper/9_create-aws-git-connection.sh
    clear
    echo ""
    echo "DONE!"
    echo ""
    helperMenu
    ;;
  [Rr])
    clear
    menu
    ;;
  [Qq])
    clear
    echo ""
    echo "DONE!"
    echo ""
    exit 0
    ;;
  *)
    clear
    echo 'Wrong option.'
    helperMenu
    ;;
  esac
}

function printRemainingSessionTime() {
  sh "$WORKING_DIR"/tools/scripts/common/verify-remaining-session-time.sh "true"
}

menu() {
  echo "
    *********************************
    *********** Main Menu ***********
    *********************************
    c) Create ALL.
    d) Delete ALL.
    h) Helper Menu.
    ---------------------------------
    1) Run with Docker Compose.
    2) Create Backend.
    3) Delete Backend.
    4) Create Frontend.
    5) Delete Frontend.
    ---------------------------------
    q) Quit.
  "
  read -r -p 'Choose an option: ' option
  case $option in
  [Cc])
    printRemainingSessionTime
    sh "$WORKING_DIR"/tools/scripts/2_create-backend.sh
    sh "$WORKING_DIR"/tools/scripts/4_create-frontend.sh
    menu
    ;;
  [Dd])
    printRemainingSessionTime
    sh "$WORKING_DIR"/tools/scripts/3_delete-backend.sh
    sh "$WORKING_DIR"/tools/scripts/4_delete-frontend.sh
    menu
    ;;
  [Hh])
    clear
    helperMenu
    ;;
  1)
    clear
    printRemainingSessionTime
    sh "$WORKING_DIR"/tools/scripts/1_deploy-docker-cluster.sh
    menu
    ;;
  2)
    clear
    printRemainingSessionTime
    sh "$WORKING_DIR"/tools/scripts/2_create-backend.sh
    menu
    ;;
  3)
    clear
    printRemainingSessionTime
    sh "$WORKING_DIR"/tools/scripts/3_delete-backend.sh
    menu
    ;;
  4)
    clear
    printRemainingSessionTime
    sh "$WORKING_DIR"/tools/scripts/4_create-frontend.sh
    menu
    ;;
  5)
    clear
    printRemainingSessionTime
    sh "$WORKING_DIR"/tools/scripts/5_delete-frontend.sh
    menu
    ;;
  [Qq])
    clear
    echo ""
    echo "DONE!"
    echo ""
    exit 0
    ;;
  *)
    clear
    echo 'Wrong option.'
    menu
    ;;
  esac
}

#### Main function ####
verifyEnvironmentVariables
clear
menu
