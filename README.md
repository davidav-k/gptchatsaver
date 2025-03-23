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

### 2. Запуск базы данных PostgreSQL через Docker

```sh
make docker-up
```
или
```sh
docker-compose -f docker/compose.yml up -d
```

### 3. Запуск Chrome с включенной отладкой

macOS:

```sh
/Applications/Google\ Chrome.app/Contents/MacOS/Google\ Chrome --remote-debugging-port=9222
```

Windows:

```powershell
"C:\Program Files\Google\Chrome\Application\chrome.exe" --remote-debugging-port=9222
```

Или:

```powershell
make chrome
```

### 4. Запуск Spring Boot приложения



### 5. Доступ к API

- **REST API**: `http://localhost:8080/api/v1/chat`
- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`

## API Эндпоинты

| Метод | Эндпоинт                       | Описание                                          |
| ----- | ------------------------------ | ------------------------------------------------- |
| POST  | `/api/v1/chat/search/question` | Поиск в истории чатов среди вопросов              |
| POST  | `/api/v1/chat/search/answer`   | Поиск в истории чатов среди ответов               |
| POST  | `/api/v1/chat/save`            | Сохранение в базу чата из активной вкладки Chrome |

---

## ⚙️ Makefile и автоматизация

В корне находится `Makefile`, содержащий команды упрощающие работу.

### 📦 Поддерживаемые команды

| Команда             | Описание                                              |
|---------------------|-------------------------------------------------------|
| `make docker-up`    | Запуск контейнера PostgreSQL (Docker Compose)         |
| `make docker-down`  | Остановка контейнера PostgreSQL                       |
| `make db-reset`     | Сброс volume                                          |
| `make backup`       | Создание резервной копии БД                           |
| `make restore`      | Восстановление данных БД из последнего архива         |
| `make swagger`      | Открытие Swagger UI в браузере                        |
| `make chrome`       | Запуск Chrome с remote debugging (порт 9222)          |
| `make port-check`   | Проверка, кто использует порт 8080                    |
| `make kill`         | Завершение процесса, занятого портом 8080             |
| `make menu`         | Текстовое меню (только в WSL/Git Bash)                |
| `make psmenu`       | Меню в PowerShell (только для Windows)                |
| `make prepare`      | Назначение прав на выполнение скриптов (в Unix-среде) |

---

## 🌐 Переменные окружения (`.env`)

В проекте используется файл `.env` в корне для настройки окружения:

```dotenv
POSTGRES_USER=user
POSTGRES_PASSWORD=Password123
POSTGRES_DB=gptchatsaver_db
POSTGRES_PORT=5432
ACTIVE_PROFILE=dev
PORT=8080
```

Этот файл используется в `docker-compose.yml`, `application.yml` и `Makefile`.

---

## 🔐 Git hook: pre-commit

В проекте используется `pre-commit` hook для автоустановки прав исполнения на скрипты:

```bash
.git/hooks/pre-commit
```

После клонирования репозитория выполнить:

```bash
chmod +x .git/hooks/pre-commit
```

---

## 🖥 Меню управления

### macOS:

```bash
bash scripts/menu.sh
```

Или:

```bash
make menu
```


### Windows:

powershell or Idea terminal:

```powershell
-ExecutionPolicy Bypass -File scripts/menu.ps1
```

Меню поддерживает запуск базы, бэкап, восстановление, Swagger, Chrome и др.

---

**© 2025 GPTChatSaver. Все права защищены.**