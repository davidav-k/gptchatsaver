#!/bin/bash

show_menu() {
  echo ""
  echo "===== GPTChatSaver Control Menu ====="
  echo "1. Start the database"
  echo "2. Stop the database"
  echo "3. Reset the database"
  echo "4. Make a backup of the database"
  echo "5. Restore the database from the archive"
  echo "6. Check who is using port 8080"
  echo "7. Open Swagger UI"
  echo "8. Start Chrome with --remote-debugging"
  echo "9. Free port 8080"
  echo "0. Exit"
  echo "======================================"
}

while true; do
  show_menu
  read -p "Enter your choice: " choice
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
    0) echo "Goodbye!"; exit 0 ;;
    *) echo "Invalid selection!" ;;
  esac
done
