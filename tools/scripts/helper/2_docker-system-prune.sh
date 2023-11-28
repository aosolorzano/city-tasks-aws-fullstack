#!/usr/bin/env bash
set -e

echo ""
read -r -p "Do you want to prune your Docker system? [y/N] " prune_docker_system
prune_docker_system=$(echo "$prune_docker_system" | tr '[:upper:]' '[:lower:]')

if [[ "$prune_docker_system" =~ ^(y|yes)$ ]]; then
  echo ""
  echo "PRUNING DOCKER SYSTEM..."
  echo ""
  docker system prune --all --force --volumes
  echo "DONE!"

  ### REMOVING ALL LOCAL CACHED VOLUMES
  actual_volumes=$(docker volume ls -q)
  if [ -n "$actual_volumes" ]; then
    echo ""
    echo "REMOVING ALL LOCAL CACHED VOLUMES..."
    echo ""
    docker volume rm "$actual_volumes"
    echo "DONE!"
  fi
else
  echo ">> No problem at all. You can prune your Docker system later."
fi
