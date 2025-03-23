# backup/backup.ps1
$timestamp = Get-Date -Format "yyyy-MM-dd_HH-mm-ss"
$backupDir = "backup"
$dataDir = "docker/pgdata"
$backupFile = "$backupDir/gptchat_backup_$timestamp.zip"

Write-Host "Creating a backup copy..."

if (-Not (Test-Path $dataDir)) {
    Write-Error "Folder data $dataDir not found"
    exit 1
}

if (-Not (Test-Path $backupDir)) {
    New-Item -ItemType Directory -Path $backupDir | Out-Null
}

Compress-Archive -Path "$dataDir\*" -DestinationPath $backupFile -Force

Write-Host "Backup saved: $backupFile"
