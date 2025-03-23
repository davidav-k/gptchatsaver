#!/bin/bash

show_menu() {
  echo ""
  echo "===== GPTChatSaver Меню управления ====="
  echo "1. Запустить базу данных"
  echo "2. Остановить базу данных"
  echo "3. Сбросить базу данных"
  echo "4. Сделать бэкап базы данных"
  echo "5. Восстановить базу данных из архива"
  echo "6. Проверить, кто использует порт 8080"
  echo "7. Открыть Swagger UI"
  echo "8. Запустить Chrome с --remote-debugging"
  echo "9. Освободить порт 8080"
  echo "0. Выйти"
  echo "========================================"
}

while true; do
  show_menu
  read -p "Выберите действие: " choice
  case $choice in
    1) make docker-up ;;
    2) make docker-down ;;
    3) make db-reset ;;
    4) make backup ;;
    5) make restore ;;
    6) make port-check ;;
    7) make swagger ;;
    8) make chrome ;;
    9) make kill ;;
    0) echo "До встречи!"; exit 0 ;;
    *) echo "Неверный выбор!" ;;
  esac
done
