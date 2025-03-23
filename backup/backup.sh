#!/bin/bash
# backup/backup.sh
#  Скрипт создает архив с данными PostgreSQL и сохраняет его в директории  backup
set -e

BACKUP_DIR="./backup"
DATA_DIR="./docker/pgdata"
TIMESTAMP=$(date +%F_%H-%M-%S)
BACKUP_FILE="$BACKUP_DIR/pgdata_backup_$TIMESTAMP.tar.gz"

echo "Создание бэкапа PostgreSQL данных..."
mkdir -p "$BACKUP_DIR"

if [ ! -d "$DATA_DIR" ]; then
  echo "❌ Директория данных PostgreSQL ($DATA_DIR) не найдена."
  exit 1
fi

tar -czf "$BACKUP_FILE" -C "$DATA_DIR" .
echo "✅ Бэкап сохранён в $BACKUP_FILE"


