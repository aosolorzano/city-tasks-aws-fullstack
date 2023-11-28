#!/bin/bash
set -e

cd "$WORKING_DIR"/apps/city-tasks-app || {
  echo "Error moving to the Tasks App 'frontend' directory."
  exit 1
}

echo ""
echo "DELETING AMPLIFY APP FROM AWS.."
amplify delete --yes --force

echo ""
echo "CLEANING CONFIGURATION FILES..."
sh "$WORKING_DIR"/tools/scripts/helper/1_revert-automated-scripts.sh
echo ""
echo "DONE!"
