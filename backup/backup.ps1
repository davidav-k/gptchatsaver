# backup/backup.ps1 — PostgreSQL backup

$timestamp = Get-Date -Format "yyyy-MM-dd_HH-mm-ss"
$backupDir = "backup"
$backupFile = "$backupDir/gptchat_backup_$timestamp.sql"

$dbUser = "user"
$dbName = "gptchatsaver_db"
$container = "gptchatsaver"

Write-Host "Creating PostgreSQL dump..."

if (-Not (Test-Path $backupDir)) {
    New-Item -ItemType Directory -Path $backupDir | Out-Null
}

$cmd = "docker exec $container pg_dump -U $dbUser --clean $dbName > `"$backupFile`""
cmd.exe /c $cmd

Write-Host "Backup saved to:" $backupFile