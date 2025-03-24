# restore.ps1 — restore PostgreSQL from latest SQL dump using hardcoded values

$backupDir = "backup"
$dbUser = "user"
$dbName = "gptchatsaver_db"
$container = "gptchatsaver"

Write-Host "Restoring PostgreSQL data..."

$lastBackup = Get-ChildItem "$backupDir\gptchat_backup_*.sql" | Sort-Object LastWriteTime -Descending | Select-Object -First 1

if (-Not $lastBackup) {
    Write-Error "No backup file found."
    exit 1
}

$backupFilePath = $lastBackup.FullName
$cmd = "docker exec -i $container psql -U $dbUser -d $dbName < `"$backupFilePath`""
cmd.exe /c $cmd

Write-Host "Restore completed from:" $lastBackup.Name