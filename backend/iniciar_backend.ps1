$ErrorActionPreference = 'Stop'
$backend = $PSScriptRoot
. (Join-Path $backend 'red_local.ps1')
$lanAddress = Get-RiesgosLanAddress
$pgBin = 'C:\Program Files\PostgreSQL\18\bin'
& "$pgBin\pg_ctl.exe" -D "$backend\.local\postgres" status | Out-Null
if ($LASTEXITCODE -ne 0) {
    & "$pgBin\pg_ctl.exe" -D "$backend\.local\postgres" -l "$backend\.local\postgres.log" -o '-p 5433 -h 127.0.0.1' start -w
    if ($LASTEXITCODE -ne 0) { throw 'No se pudo iniciar PostgreSQL de pruebas.' }
}
$listener = Get-NetTCPConnection -LocalAddress $lanAddress -LocalPort 8000 -State Listen -ErrorAction SilentlyContinue
if (!$listener) {
    $stamp = Get-Date -Format 'yyyyMMdd-HHmmss'
    Start-Process -FilePath 'C:\xampp\php\php.exe' -ArgumentList @('-d','extension=pdo_pgsql','-S',"${lanAddress}:8000",'-t',"$backend\api") -WorkingDirectory "$backend\api" -WindowStyle Hidden -RedirectStandardOutput "$backend\.local\php-$stamp.stdout.log" -RedirectStandardError "$backend\.local\php-$stamp.stderr.log"
    Start-Sleep -Seconds 1
}
$response = Invoke-WebRequest "http://${lanAddress}:8000/prueba_conexion.php" -TimeoutSec 8
Write-Output $response.Content
