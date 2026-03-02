#!/bin/bash
# Recovery SSH access
echo "Configuring firewall to allow SSH access..."

# AlmaLinux uses firewalld, not ufw
firewall-cmd --permanent --add-port=22/tcp
firewall-cmd --permanent --add-port=2222/tcp
firewall-cmd --reload
systemctl restart sshd

echo "SSH access configured"
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
