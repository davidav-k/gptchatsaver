# GPTChatSaver

**GPTChatSaver** is an application that
automatically extracts the contents of a ChatGPT conversation
from a browser tab (Chrome) and saves it to a local PostgreSQL database
with further access to it via WEB-interface.

---

## Functionality

- Extracting messages using Selenium WebDriver from the ChatGPT tab
- Saving questions and answers to a local PostgreSQL database
- WEB interface for searching and show messages
- Backup and restore of the database
- CLI menu for managing the application and database (for Windows and Unix)

---

## Technology stack

- **Java 21**, **Spring Boot 3**, **Spring Data JPA**
- **PostgreSQL** (via Docker)
- **Selenium WebDriver**
- **Docker + Docker Compose**
- **PowerShell (Windows)** and **Makefile (Unix/macOS/WSL)**

---

## Installation and configuration

### Requirements:
>Windows
>- Docker Desktop installed and running
>- Google Chrome installed (`C:\Program Files Files\Google\Chrome\Application\chrome.exe`)
>- PowerShell 5+ or 7+

>MacOS or Unix
>- Docker and Docker Compose
>- Google Chrome
>- GNU Make

PostgreSQL will run in a Docker container

#### 1. Project cloning

```bash
git clone https://github.com/davidav-k/gptchatsaver.git
```

---

#### 2. Launching the control menu
#### Windows:
```powershell
.\Makefile.bat psmenu
```
#### MacOS:
```bash
make menu
```

#### 3. In the menu - `Start the database`

#### 4. In the menu - `Start Chrome with --remote-debugging`

#### 5. In chrome - open the desired ChatGPT tab

#### 6. Launch the application

---

### WEB access

- **web**: `http://localhost:8080/index.html`

---

#### API Endpoints

| Method | Endpoint | Description |
| ----- | ------------------------------ |-----------------------------------------|
| POST | `/search/question` | Search in chat history among questions |
| POST | `/search/answer` | Search in chat history among answers |
| POST | `/save` | Save to the chat database from the active Chrome tab |

---

### Automation

At the root there is `Makefile` (macOS) and `Makefile.bat` (Windows) containing commands that simplify the work.

#### Supported commands (macOS)

| Command | Description |
|--------------------|----------------------------------------------|
| `make docker-up` | Start a PostgreSQL container (Docker Compose) |
| `make docker-down` | Stop a PostgreSQL container |
| `make db-reset` | !!! Reset volume !!! |
| `make backup` | Create a backup copy of the database |
| `make restore` | Restore database data from the latest archive |
| `make swagger` | Open Swagger UI in a browser |
| `make chrome` | Start Chrome with remote debugging (port 9222) |
| `make port-check` | Check who is using port 8080 |
| `make kill` | Kill the process using port 8080 |
| `make menu` | CLI menu |
| `make prepare` | Assigning permissions to execute scripts |
| `make run` | Run the application |
| `make stop` | Stop the application * |
| `make clean` | Clean the project (remove targets and logs) |
| `make build` | Build the project |
| `make test` | Run tests * |
| `make help` | Help on commands * |

* - in development

---

### Environment variables (`.env`)

The project uses the `.env` file in the root to configure the environment:

#### Example `.env`

```dotenv
POSTGRES_USER=user
POSTGRES_PASSWORD=Password123
POSTGRES_DB=gptchatsaver_db
POSTGRES_PORT=5432
POSTGRES_CONTAINER=postgresdb
```

---

## Git hook: pre-commit

The project uses the `pre-commit` hook to automatically set execution rights for scripts:

```bash
.git/hooks/pre-commit
```

After cloning the repository, run:

```bash
chmod +x .git/hooks/pre-commit
```

---
## Project structure

```
gptchatsaver/
├── .git/
│ └── hooks/
│ └── pre-commit ← git hook
├── backup/
│ ├── backup.ps1 ← Windows backup
│ ├── restore.ps1 ← Windows restore
│ ├── backup.sh ← Unix/macOS backup
│ └── restore.sh ← Unix/macOS restore
├── docker/
│ ├── compose.yml ← PostgreSQL container
│ ├── db-init/init.sql ← DB initialization script
└── pgdata/ ← PostgreSQL database
├── scripts/
│ └── menu.ps1 ← Windows CLI menu
│ └── menu.sh ← Unix/macOS CLI menu
├── Makefile ← Unix/macOS automation
├── Makefile.bat
