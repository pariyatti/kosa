#!/usr/bin/env bash
set -euo pipefail

# This script restores data from an AWS S3 backup and restarts the Kosa app.
# Run as root and run with your personal AWS creds that you setup for active shell only.

# Prompt the user for the target restore date.
read -p "Please enter the target restore date (format example: 2023050312): " RESTORE_DATE

# Set up variables.
USER=kosa-user
RESTORE_DIR=/tmp/kosa-restore
BACKUP_BUCKET=kosa-production-data-backup
APP_SERVICE=kosa-app.service

# Clean up the restore directory and create it if it doesn't exist.
rm -rf "$RESTORE_DIR" || true
mkdir -p "$RESTORE_DIR"
cd "$RESTORE_DIR"

# Sync data from the S3 backup.
aws s3 sync "s3://$BACKUP_BUCKET/$RESTORE_DATE/" .

# Change ownership of the data and storage directories to the Kosa user.
chown -R "$USER:$USER" data/
chown -R "$USER:$USER" storage/

# Stop the Kosa app service.
systemctl stop "$APP_SERVICE"

# Print the status of the Kosa app service.
systemctl status "$APP_SERVICE"

# Remove the old data directory and move the restored data directory to its new location.
rm -rf "/srv/$USER/kosa/data/"
mv data/ "/srv/$USER/kosa/"

# Remove the old storage directory and move the restored storage directory to its new location.
rm -rf "/srv/$USER/kosa/resources/storage"
mv storage/ "/srv/$USER/kosa/resources/"

# Start the Kosa app service.
systemctl start "$APP_SERVICE"

# Print the status of the Kosa app service.
systemctl status "$APP_SERVICE"
