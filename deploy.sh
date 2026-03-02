#!/bin/bash

echo " Starting deployment..."

cd /var/www/dms_by_mauli || exit

echo " Pulling latest code..."
git pull origin main

echo " Stopping old container..."
docker stop dms-container || true
docker rm dms-container || true

echo " Removing old image..."
docker rmi dms-app || true

echo " Building Docker image..."
docker build -t dms-app .

echo " Running container..."
docker run -d -p 8082:8080 --name dms-container dms-app

echo " Running containers:"
docker ps

echo " Logs:"
docker logs dms-container

echo " Deployment finished!"
