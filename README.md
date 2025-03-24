# GPTChatSaver

**GPTChatSaver** — это локальное приложение, 
которое автоматически извлекает содержимое беседы с ChatGPT 
из вкладки браузера (Chrome) и сохраняет в локальную базу данных PostgreSQL
с дальнейшим доступом к ней через REST API.


**Примечание: приложение находится в стадии разработки и тестирования, но основной функционал работает.**

---

## Функциональность

- Извлечение сообщений с помощью Selenium WebDriver из вкладки ChatGPT
- Сохранение вопросов и ответов в локальную базу PostgreSQL
- REST API для поиска и фильтрации сообщений в базе данных 
- Документация через Swagger UI
- Резервное копирование и восстановление базы данных
- CLI меню для управления приложением и базой данных (для Windows и Unix)

---

## Стек технологий

- **Java 21**, **Spring Boot 3**, **Spring Data JPA**
- **PostgreSQL** (через Docker)
- **Selenium WebDriver**
- **Swagger UI**
- **Docker + Docker Compose**
- **PowerShell (Windows)** и **Makefile (Unix/macOS/WSL)**

---

## Установка и настройка

### Требования:
>Windows 
>- Docker Desktop установлен и запущен
>- Google Chrome установлен (`C:\Program Files\Google\Chrome\Application\chrome.exe`)
>- PowerShell 5+ или 7+

>MacOS или Unix
>- Docker и Docker Compose
>- Google Chrome
>- GNU Make

PostgreSQL будет работать в Docker-контейнере

#### 1. Клонирование проекта

```bash
git clone https://github.com/davidav-k/gptchatsaver.git
```

---

#### 2. Запуск меню управления
#### Windows:
```powershell
.\Makefile.bat psmenu
```
#### MacOS:
```bash
make menu
``` 

#### 3.  В меню - `Start the database`

#### 4.  В меню - `Start Chrome with --remote-debugging`

#### 5.  В chrome - открыть интересующую вкладку ChatGPT

#### 6.  Запустить приложение

---

###  Доступ к API

- **API**: `http://localhost:8080/api/v1/chat`
- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`

---

#### API Эндпоинты

| Метод | Эндпоинт                       | Описание                                |
| ----- | ------------------------------ |-----------------------------------------|
| POST  | `/search/question` | Поиск в истории чатов среди вопросов                |
| POST  | `/search/answer`   | Поиск в истории чатов среди ответов                 |
| POST  | `/save`            | Сохранение в базу чата из активной вкладки Chrome   |

---

### Автоматизация

В корне находится `Makefile`(macOS) и `Makefile.bat`(Windows) содержащие команды упрощающие работу.

#### Поддерживаемые команды (macOS)

| Команда            | Описание                                      |
|--------------------|-----------------------------------------------|
| `make docker-up`   | Запуск контейнера PostgreSQL (Docker Compose) |
| `make docker-down` | Остановка контейнера PostgreSQL               |
| `make db-reset`    | !!! Сброс volume !!!                          |
| `make backup`      | Создание резервной копии БД                   |
| `make restore`     | Восстановление данных БД из последнего архива |
| `make swagger`     | Открытие Swagger UI в браузере                |
| `make chrome`      | Запуск Chrome с remote debugging (порт 9222)  |
| `make port-check`  | Проверка, кто использует порт 8080            |
| `make kill`        | Завершение процесса, занятого портом 8080     |
| `make menu`        | CLI меню                                      |
| `make prepare`     | Назначение прав на выполнение скриптов        |
| `make run`         | Запуск приложения                             |
| `make stop`        | Остановка приложения *                        |
| `make clean`       | Очистка проекта (удаление target и log)       |
| `make build`       | Сборка проекта                                |
| `make test`        | Запуск тестов *                               |
| `make help`        | Справка по командам *                         |

* - в разработке

---

### Переменные окружения (`.env`)

В проекте используется файл `.env` в корне для настройки окружения:

#### Пример `.env`

```dotenv
POSTGRES_USER=user
POSTGRES_PASSWORD=Password123
POSTGRES_DB=gptchatsaver_db
POSTGRES_PORT=5432
POSTGRES_CONTAINER=postgresdb
```

---

## Git hook: pre-commit

В проекте используется `pre-commit` hook для автоустановки прав исполнения на скрипты:

```bash
.git/hooks/pre-commit
```

После клонирования репозитория выполнить:

```bash
chmod +x .git/hooks/pre-commit
```


---
## Структура проекта

```
gptchatsaver/
├── .git/
│   └── hooks/
│       └── pre-commit      ← git hook
├── backup/
│   ├── backup.ps1          ← бэкап Windows
│   ├── restore.ps1         ← восстановление Windows
│   ├── backup.sh           ← бекап Unix/macOS
│   └── restore.sh          ← восстановление Unix/macOS
├── docker/
│   ├── compose.yml         ← контейнер PostgreSQL
│   ├── db-init/init.sql    ← скрипт инициализации БД
    └── pgdata/             ← база данных PostgreSQL
├── scripts/
│   └── menu.ps1            ← CLI меню Windows
│   └── menu.sh             ← CLI меню Unix/macOS
├── Makefile                ← автоматизация Unix/macOS
├── Makefile.bat            ← автоматизация Windows
├── .env                    ← переменные окружения 
```

---


### Важно

- Бэкап/восстановление работают **только если контейнер PostgreSQL запущен в docker**


---

**© 2025 GPTChatSaver. Все права защищены.**