#!/bin/bash

echo "Starting deployment..."
cd /var/www/dms_by_mauli || exit
echo "Pulling latest code..."
git pull origin main
# Clean old build cache to avoid disk filling again
docker builder prune -af
# Rebuild fresh
docker compose build --no-cache
# Start everything
docker compose up -d
# Show status
docker ps
