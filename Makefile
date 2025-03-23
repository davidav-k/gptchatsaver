# Makefile —  (Unix / macOS / Linux)

include .env
export $(shell sed 's/=.*//' .env)

BACKUP_DIR=backup
DATA_DIR=docker/pgdata
SWAGGER_URL=http://localhost:$(PORT)/swagger-ui/index.html

.PHONY: prepare backup restore docker-up docker-down db-reset swagger menu chrome port-check kill

prepare:
	chmod +x ./mvnw
	chmod +x $(BACKUP_DIR)/backup.sh
	chmod +x $(BACKUP_DIR)/restore.sh
	chmod +x scripts/menu.sh

backup: prepare
	bash $(BACKUP_DIR)/backup.sh

restore: prepare
	bash $(BACKUP_DIR)/restore.sh


docker-up:
	docker-compose --env-file .env -f docker/compose.yml up -d


docker-down:
	docker-compose --env-file .env -f docker/compose.yml down


db-reset:
	docker-compose --env-file .env -f docker/compose.yml down -v


swagger:
	xdg-open $(SWAGGER_URL) 2>/dev/null || open $(SWAGGER_URL) || echo "Открой вручную: $(SWAGGER_URL)"


chrome:
	open -na "Google Chrome" --args --remote-debugging-port=9222


menu:
	bash scripts/menu.sh


port-check:
	lsof -i :8080 || echo "Порт свободен"


kill:
	kill -9 $$(lsof -t -i :8080) 2>/dev/null || echo "Порт свободен"