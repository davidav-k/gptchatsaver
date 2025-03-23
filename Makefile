# Makefile — кросс-платформенные команды для GPTChatSaver (Unix + PowerShell)

include .env
export $(shell sed 's/=.*//' .env)

BACKUP_DIR=backup
DATA_DIR=docker/pgdata
SWAGGER_URL=http://localhost:$(PORT)/swagger-ui/index.html
IS_WINDOWS := $(filter Windows_NT, $(OS))

.PHONY: prepare backup restore docker-up docker-down db-reset swagger menu chrome port-check kill psmenu

## Подготовка (chmod только в Unix)
prepare:
ifeq ($(IS_WINDOWS),)
	chmod +x ./mvnw
	chmod +x $(BACKUP_DIR)/backup.sh
	chmod +x $(BACKUP_DIR)/restore.sh
	chmod +x scripts/menu.sh
endif

## Бэкап
backup: prepare
ifeq ($(IS_WINDOWS),)
	bash $(BACKUP_DIR)/backup.sh
else
	powershell -ExecutionPolicy Bypass -File $(BACKUP_DIR)/backup.ps1
endif

## Восстановление
restore: prepare
ifeq ($(IS_WINDOWS),)
	bash $(BACKUP_DIR)/restore.sh
else
	powershell -ExecutionPolicy Bypass -File $(BACKUP_DIR)/restore.ps1
endif

## Docker: запуск
docker-up:
	docker-compose --env-file .env -f docker/compose.yml up -d

## Docker: остановка
docker-down:
	docker-compose --env-file .env -f docker/compose.yml down

## Docker: сброс volume
db-reset:
	docker-compose --env-file .env -f docker/compose.yml down -v

## Swagger UI
swagger:
ifeq ($(IS_WINDOWS),)
	xdg-open $(SWAGGER_URL) 2>/dev/null || open $(SWAGGER_URL) || echo "Открой вручную: $(SWAGGER_URL)"
else
	start $(SWAGGER_URL)
endif

## Chrome с remote debugging
chrome:
ifeq ($(IS_WINDOWS),)
	open -na "Google Chrome" --args --remote-debugging-port=9222
else
	start "" "C:\Program Files\Google\Chrome\Application\chrome.exe" --remote-debugging-port=9222
endif

## Меню (недоступно в чистом PowerShell)
menu:
ifeq ($(IS_WINDOWS),)
	bash scripts/menu.sh
else
	@echo "'make menu' доступен только в Git Bash или WSL"
endif

## Проверка порта 8080
port-check:
ifeq ($(IS_WINDOWS),)
	lsof -i :8080 || echo "Порт свободен"
else
	netstat -aon | findstr :8080 || echo "Порт свободен"
endif

## Завершение процесса на 8080
kill:
ifeq ($(IS_WINDOWS),)
	kill -9 $$(lsof -t -i :8080) 2>/dev/null || echo "Порт свободен"
else
	for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8080') do taskkill /PID %%a /F
endif

psmenu:
	powershell -ExecutionPolicy Bypass -File scripts/menu.ps1