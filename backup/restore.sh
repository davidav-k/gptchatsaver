#!/bin/bash
# backup/restore.sh — PostgreSQL restore using pg_restore

backup_dir="backup"
db_user="user"
db_name="gptchatsaver_db"
container="gptchatsaver"

echo "Restoring PostgreSQL data..."

latest_backup=$(ls -t "$backup_dir"/gptchat_backup_*.sql 2>/dev/null | head -n 1)

if [ -z "$latest_backup" ]; then
  echo "No backup file found."
  exit 1
fi

docker exec "$container" bash -c "psql -U $db_user -d postgres -c \"
  SELECT pg_terminate_backend(pid)
  FROM pg_stat_activity
  WHERE datname = '$db_name' AND pid <> pg_backend_pid();
\""

docker exec "$container" bash -c "psql -U $db_user -d postgres -c 'DROP DATABASE IF EXISTS $db_name;'"
docker exec "$container" bash -c "psql -U $db_user -d postgres -c 'CREATE DATABASE $db_name;'"

docker exec -i "$container" psql -U "$db_user" -d "$db_name" < "$latest_backup"

echo "Restore completed from: $latest_backup"
