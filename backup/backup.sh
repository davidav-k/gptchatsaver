#!/bin/bash
# backup/backup.sh — PostgreSQL backup using pg_dump

timestamp=$(date +%Y-%m-%d_%H-%M-%S)
backup_dir="backup"
backup_file="$backup_dir/gptchat_backup_$timestamp.sql"
db_user="user"
db_name="gptchatsaver_db"
container="gptchatsaver"

echo "Creating PostgreSQL dump..."

mkdir -p "$backup_dir"
docker exec "$container" pg_dump -U "$db_user" --clean "$db_name" > "$backup_file"

echo "Backup saved to: $backup_file"