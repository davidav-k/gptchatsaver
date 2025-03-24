#!/bin/bash
# backup/restore.sh — Restore PostgreSQL from latest SQL dump

backup_dir="backup"
db_user="user"
db_name="gptchatsaver_db"
container="gptchatsaver"

echo "Restoring PostgreSQL data..."

last_backup=$(ls -t "$backup_dir"/gptchat_backup_*.sql 2>/dev/null | head -n 1)

if [ -z "$last_backup" ]; then
  echo "No backup file found."
  exit 1
fi

docker exec -i "$container" psql -U "$db_user" -d "$db_name" < "$last_backup"

echo "Restore completed from: $last_backup"