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

```sh
"C:\Program Files\Google\Chrome\Application\chrome.exe" --remote-debugging-port=9222
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

Для удобства управления в корне репозитория добавлен `Makefile`, который содержит готовые команды

### 📦 Поддерживаемые команды

| Команда             | Описание                                                        |
|---------------------|-----------------------------------------------------------------|
| `make docker-up`    | Запуск контейнера PostgreSQL (Docker Compose)                   |
| `make docker-down`  | Остановка контейнера PostgreSQL                                 |
| `make backup`       | Создание резервной копии БД в виде архива                       |
| `make restore`      | Восстановление данных БД из архива                              |
| `make swagger`      | Открытие Swagger UI в браузере                                  |
| `make menu`         | Запуск интерактивного bash-меню для всех вышеуказанных действий |
| `make prepare`      | Назначение прав на выполнение скриптов бэкапа                   |
| `make db-reset`     | сброс volume и повторная инициализация                          |
| `make chrome`       | Chrome с remote debugging                                       |
| `make port-check`   | Проверка порта 8080                                             |

## 🌐 Переменные окружения (`.env`)

В проекте используется файл `.env` в корне, чтобы централизованно управлять настройками:

```dotenv
POSTGRES_USER=user
POSTGRES_PASSWORD=Password123
POSTGRES_DB=gptchatsaver_db
POSTGRES_PORT=5432
ACTIVE_PROFILE=dev
PORT=8080
```

Эти значения используются как в `docker-compose.yml`, так и в `Makefile`.

## 🔐 Git hook: pre-commit

В репозитории настроен `pre-commit` hook, который:

- автоматически проверяет наличие прав на исполнение для скриптов `backup.sh`, `restore.sh` и `menu.sh`;
- предотвращает коммиты с отсутствующим `+x`, чтобы не возникало ошибок запуска в других окружениях;

Хук находится по пути:

```bash
.git/hooks/pre-commit
```

При клонировании репозитория — сделать его исполняемым:

```bash
chmod +x .git/hooks/pre-commit
```

## 🖥 Bash-меню

Для управления проектом в интерактивном режиме можно использовать текстовое меню:

```bash
bash scripts/menu.sh
```

> Или просто:\
> `make menu`

Отобразится список действий

---

**© 2025 GPTChatSaver. Все права защищены.**

