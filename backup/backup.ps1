# backup/backup.ps1
$timestamp = Get-Date -Format "yyyy-MM-dd_HH-mm-ss"
$backupDir = "backup"
$dataDir = "docker/pgdata"
$backupFile = "$backupDir/pgdata_backup_$timestamp.zip"

Write-Host "📦 Создание резервной копии..."

if (-Not (Test-Path $dataDir)) {
    Write-Error "Папка данных $dataDir не найдена"
    exit 1
}

if (-Not (Test-Path $backupDir)) {
    New-Item -ItemType Directory -Path $backupDir | Out-Null
}

Compress-Archive -Path "$dataDir\*" -DestinationPath $backupFile -Force

Write-Host "Бэкап сохранён: $backupFile"
