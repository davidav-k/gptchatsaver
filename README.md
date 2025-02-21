# GPTChatSaver

GPTChatSaver - это приложение, которое автоматически извлекает содержимое чата из активной вкладки браузера Chrome, сохраняет его в PostgreSQL и предоставляет REST API для доступа к данным.

## Функциональность
- Извлечение сообщений из чата с помощью Selenium
- Сохранение данных в PostgreSQL 
- REST API для получения и фильтрации сообщений
- Фоновый процесс сбора данных
- Документация API через Swagger UI


## Технологии
- **Spring Boot 3**
- **Spring Data JPA**
- **PostgreSQL**
- **Selenium WebDriver**
- **Flyway**
- **Swagger**
- **Lombok**
- **Docker**

## Установка и настройка
### 1. Клонирование проекта
```sh
 git clone https://github.com/davidav-k/gptchatsaver.git
```

### 2. Запустите базу данных с помощью Docker из папки docker:
```sh
docker-compose up -d
```

### 3. Соберите и запустите приложение:
```sh
mvn spring-boot:run
```

### 4. Доступ к API
После запуска приложение будет доступно по адресу:
- **REST API**: `http://localhost:8080/api/chat`
- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`

## API Эндпоинты
| Метод | Эндпоинт | Описание |
|--------|-------------|------------|
| GET | `/api/chat` | Получить все сообщения |
| POST | `/api/chat?sender=User&message=Привет!` | Сохранить сообщение |
| DELETE | `/api/chat` | Очистить историю чатов |


---
**© 2025 GPTChatSaver. Все права защищены.**

