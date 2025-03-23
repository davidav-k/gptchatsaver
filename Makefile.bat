@echo off
set CMD=%1

if "%CMD%"=="docker-up" (
    docker-compose --env-file .env -f docker/compose.yml up -d
    goto end
)

if "%CMD%"=="docker-down" (
    docker-compose --env-file .env -f docker/compose.yml down
    goto end
)

if "%CMD%"=="db-reset" (
    docker-compose --env-file .env -f docker/compose.yml down -v
    rmdir /S /Q docker\pgdata
    echo Database reset completed
    goto end
)

if "%CMD%"=="backup" (
    powershell -ExecutionPolicy Bypass -File backup\backup.ps1
    goto end
)

if "%CMD%"=="restore" (
    powershell -ExecutionPolicy Bypass -File backup\restore.ps1
    goto end
)

if "%CMD%"=="swagger" (
    start http://localhost:8080/swagger-ui/index.html
    goto end
)

if "%CMD%"=="chrome" (
    start "" "C:\Program Files\Google\Chrome\Application\chrome.exe" --remote-debugging-port=9222
    goto end
)

if "%CMD%"=="psmenu" (
    powershell -ExecutionPolicy Bypass -File scripts\menu.ps1
    goto end
)

if "%CMD%"=="port-check" (
    netstat -aon | findstr :8080
    goto end
)

if "%CMD%"=="kill" (
    for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8080') do taskkill /PID %%a /F
    goto end
)


echo Unknown command: %CMD%
echo Supported commands:
echo docker-up, docker-down, db-reset, backup, restore, swagger, chrome, psmenu, port-check, kill

:end