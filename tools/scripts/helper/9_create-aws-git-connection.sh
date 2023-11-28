#!/bin/bash
set -e

cd "$WORKING_DIR"/ || {
  echo "Error moving to the Tasks Service 'root' directory."
  exit 1
}

echo ""
read -r -p "Enter the <AWS Profile> for Git repository connection: [$AWS_WORKLOADS_PROFILE] " aws_profile
if [ -z "$aws_profile" ]; then
  AWS_PROFILE=$AWS_WORKLOADS_PROFILE
else
  AWS_PROFILE=$aws_profile
fi

echo "
Select the <Git Provider> which hosts your source code:
  ---------------------------------
  1) Bitbucket.
  2) GitHub.
  3) GitHub Enterprise Server.
  4) GitLab.
  ---------------------------------
  r) Return.
"
read -r -p 'Choose an option: ' option
case $option in
  1)
    git_provider='Bitbucket'
    ;;
  2)
    git_provider='GitHub'
    ;;
  3)
    git_provider='GitHubEnterpriseServer'
    ;;
  4)
    git_provider='GitLab'
    ;;
  [Rr])
    clear
    exit 0
    ;;
  *)
    clear
    echo 'Wrong option.'
    exit 0
    ;;
esac
GIT_PROVIDER=$(echo "$git_provider" | tr '[:lower:]' '[:upper:]')

echo ""
echo "CREATING CONNECTION TO ACCESS '$GIT_PROVIDER' FROM AWS..."
aws codestar-connections create-connection    \
  --provider-type "$git_provider"             \
  --connection-name city-tasks-connection     \
  --profile "$AWS_PROFILE"
echo "DONE!"

echo "
NOTE: The following procedure is manual.
      Please, go to 'Developer Tools' in your AWS Console, and select the 'Connections' option to complete the process.
      Then, return to this terminal and press any key to continue..."
read -n 1 -s -r -p ""
clear
echo ""
echo "DONE!"
