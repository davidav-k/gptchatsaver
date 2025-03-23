#!/bin/bash
# backup/restore.sh
# скрипт восстановления данных из бэкапа

set -e

BACKUP_DIR="./backup"
DATA_DIR="./docker/pgdata"

echo "⚠️  Восстановление приведёт к потере текущих данных!"
read -p "Продолжить? (yes/no): " confirm
if [ "$confirm" != "yes" ]; then
  echo "❌ Отменено."
  exit 1
fi

echo "Остановка контейнера PostgreSQL (если запущен)..."
docker stop postgresdb 2>/dev/null || true

echo "Очистка текущей директории данных..."
rm -rf "$DATA_DIR"
mkdir -p "$DATA_DIR"

echo "Список доступных бэкапов:"
ls "$BACKUP_DIR"/pgdata_backup_*.tar.gz || true
echo ""
read -p "Введите имя файла бэкапа (например: pgdata_backup_2025-03-21_14-30-00.tar.gz): " BACKUP_FILE

if [ ! -f "$BACKUP_DIR/$BACKUP_FILE" ]; then
  echo "❌ Файл $BACKUP_FILE не найден в $BACKUP_DIR"
  exit 1
fi

echo "Распаковка бэкапа..."
tar -xzf "$BACKUP_DIR/$BACKUP_FILE" -C "$DATA_DIR"

echo "✅ Восстановление завершено. Запусти контейнер:"
echo "   docker-compose up -d"
