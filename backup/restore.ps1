# backup/restore.ps1
$backupDir = "backup"
$dataDir = "docker/pgdata"

Write-Host "Восстановление базы данных..."

$lastBackup = Get-ChildItem "$backupDir\pgdata_backup_*.zip" | Sort-Object LastWriteTime -Descending | Select-Object -First 1

if (-Not $lastBackup) {
    Write-Error "Бэкап не найден"
    exit 1
}

if (Test-Path $dataDir) {
    Remove-Item -Recurse -Force $dataDir
}

New-Item -ItemType Directory -Path $dataDir | Out-Null
Expand-Archive -Path $lastBackup.FullName -DestinationPath $dataDir -Force

Write-Host "Восстановление завершено из: $($lastBackup.Name)"
