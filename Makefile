# Makefile — GPTChatSaver (Unix/macOS only)

include .env
export $(shell sed 's/=.*//' .env)

BACKUP_DIR=backup
DATA_DIR=docker/pgdata
SWAGGER_URL=http://localhost:$(PORT)/swagger-ui/index.html

.PHONY: prepare backup restore docker-up docker-down db-reset swagger menu chrome port-check kill run test build clean

prepare:
	chmod +x ./mvnw
	chmod +x $(BACKUP_DIR)/backup.sh
	chmod +x $(BACKUP_DIR)/restore.sh
	chmod +x scripts/menu.sh

backup: prepare
	docker exec postgresdb pg_dump -U user gptchatsaver_db > backup/gptchat_backup_$(shell date +%Y-%m-%d_%H-%M-%S).sql

restore: prepare
	@echo "Restoring from latest SQL dump..."
	@last=$(shell ls -1t backup/gptchat_backup_*.sql | head -n 1); \
	echo "Using backup file: $$last"; \
	docker exec -i postgresdb psql -U user -d gptchatsaver_db < $$last

docker-up:
	docker-compose --env-file .env -f docker/compose.yml up -d

docker-down:
	docker-compose --env-file .env -f docker/compose.yml down

db-reset:
	docker-compose --env-file .env -f docker/compose.yml down -v

swagger:
	xdg-open $(SWAGGER_URL) 2>/dev/null || open $(SWAGGER_URL)

chrome:
	open -na "Google Chrome" --args --remote-debugging-port=9222

menu: prepare
	bash scripts/menu.sh

port-check:
	lsof -i :8080 || echo "Port is free"

kill:
	kill -9 $$(lsof -t -i :8080) 2>/dev/null || echo "Port is free"

run:
	@echo "Starting GPTChatSaver on http://localhost:8080"
	./mvnw spring-boot:run

test:
	@echo "Running unit tests..."
	./mvnw test

build:
	@echo "Building project..."
	./mvnw clean package -DskipTests

clean:
	@echo "Cleaning target and temporary files..."
	./mvnw clean