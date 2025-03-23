# scripts/menu.ps1 — интерактивное меню управления GPTChatSaver

function Show-Menu {
    Clear-Host
    Write-Host "===== GPTChatSaver Меню управления (PowerShell) ====="
    Write-Host "1. Запустить базу данных (docker-up)"
    Write-Host "2. Остановить базу данных (docker-down)"
    Write-Host "3. Сбросить БД и инициализировать заново (db-reset)"
    Write-Host "4. Создать бэкап базы данных"
    Write-Host "5. Восстановить базу данных из последнего архива"
    Write-Host "6. Проверить, кто использует порт 8080"
    Write-Host "7. Завершить процесс на порту 8080"
    Write-Host "8. Открыть Swagger UI"
    Write-Host "9. Запустить Chrome с remote-debugging"
    Write-Host "0. Выход"
    Write-Host "======================================================"
}

do {
    Show-Menu
    $choice = Read-Host "Выберите действие"

    switch ($choice) {
        '1' { make docker-up; pause }
        '2' { make docker-down; pause }
        '3' { make db-reset; pause }
        '4' { make backup; pause }
        '5' { make restore; pause }
        '6' { make port-check; pause }
        '7' { make kill; pause }
        '8' { make swagger; pause }
        '9' { make chrome; pause }
        '0' {
            Write-Host "👋 До встречи!"
            break
        }
        default {
            Write-Host "❌ Неверный выбор!" -ForegroundColor Red
            pause
        }
    }
} while ($true)
