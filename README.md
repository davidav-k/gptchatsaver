# GPTChatSaver

GPTChatSaver - это приложение, которое автоматически извлекает содержимое чата из активной вкладки браузера Chrome, сохраняет его в PostgreSQL и предоставляет REST API для доступа к данным.

## Функциональность
- Извлечение сообщений из чата с помощью Selenium
- Сохранение данных в PostgreSQL 
- REST API для получения и фильтрации сообщений
- Документация API через Swagger UI


## Технологии
- **Spring Boot 3**
- **Spring Data JPA**
- **PostgreSQL**
- **Selenium WebDriver**
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

### 4. Запустите chrome с включенной отладкой:
macOS - 
```sh
/Applications/Google\ Chrome.app/Contents/MacOS/Google\ Chrome --remote-debugging-port=9222
```
Windows - закрыть все экземпляры Chrome и выполнить в командной строке: 
```sh
"C:\Program Files\Google\Chrome\Application\chrome.exe" --remote-debugging-port=9222
```

### 5. Доступ к API
После запуска приложение будет доступно по адресу:
- **REST API**: `http://localhost:8080/api/v1/chat`
- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`

## API Эндпоинты
| Метод  | Эндпоинт                       | Описание                                          |
|--------|--------------------------------|---------------------------------------------------|
| POST   | `/api/v1/chat/search/question` | Поиск в истории чатов среди вопросов              |
| POST   | `/api/v1/chat/search/answer`   | Поиск в истории чатов среди ответов               |
| POST   | `/api/v1/chat/save`            | Сохранение в базу чата из активной вкладки Chrome |

---
**© 2025 GPTChatSaver. Все права защищены.**

