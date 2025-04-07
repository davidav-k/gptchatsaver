function Show-Menu {
    Clear-Host
    Write-Host "===== GPTChatSaver Project Menu (PowerShell) ====="
    Write-Host "1. Start PostgreSQL database"
    Write-Host "2. Stop PostgreSQL database"
    Write-Host "3. Reset DB volume"
    Write-Host "4. Backup the database"
    Write-Host "5. Restore the database from last backup"
    Write-Host "6. Check which process uses port 8080"
    Write-Host "7. Kill the process on port 8080"
    Write-Host "8. Open Swagger UI"
    Write-Host "9. Launch Chrome with remote debugging"
    Write-Host "0. Exit"
    Write-Host "==================================================="
}

$exit = $false

while (-not $exit) {
    Show-Menu
    $choice = Read-Host "Enter your choice"

    switch ($choice) {
        '1' { .\Makefile.bat docker-up; pause }
        '2' { .\Makefile.bat docker-down; pause }
        '3' { .\Makefile.bat db-reset; pause }
        '4' { .\Makefile.bat backup; pause }
        '5' { .\Makefile.bat restore; pause }
        '6' { .\Makefile.bat port-check; pause }
        '7' { .\Makefile.bat kill; pause }
        '8' { .\Makefile.bat swagger; pause }
        '9' { .\Makefile.bat chrome; pause }
        '0' {
            Write-Host "Goodbye!"
            $exit = $true
        }
        default {
            Write-Host "Invalid selection!" -ForegroundColor Red
            pause
        }
    }
}