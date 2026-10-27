$ErrorActionPreference = 'Stop'
$root = 'C:\Users\Public\RA_AndroidProject'
. (Join-Path $root 'backend\red_local.ps1')
$lanAddress = Get-RiesgosLanAddress
$props = Join-Path $root 'gradle.properties'
$text = [System.IO.File]::ReadAllText($props)
$text = [regex]::Replace($text, '(?m)^riesgosApiUrl=.*$', "riesgosApiUrl=http://${lanAddress}:8000/")
$text = [regex]::Replace($text, '(?m)^riesgosCatalogoUrl=.*$', "riesgosCatalogoUrl=http://${lanAddress}:8000/")
[System.IO.File]::WriteAllText($props, $text, [System.Text.UTF8Encoding]::new($false))
& (Join-Path $root 'backend\iniciar_backend.ps1')
$allowed = Get-NetFirewallRule -DisplayName 'RiesgosRA pruebas PHP 8000 red local' -ErrorAction SilentlyContinue | Get-NetFirewallAddressFilter
if (!$allowed -or $lanAddress -notin $allowed.LocalAddress) {
    Start-Process -FilePath 'powershell.exe' -Verb RunAs -WindowStyle Hidden -Wait -ArgumentList '-NoProfile -ExecutionPolicy RemoteSigned -File "C:\Users\Public\RA_AndroidProject\backend\permitir_telefono.ps1"'
}
& powershell.exe -NoProfile -ExecutionPolicy RemoteSigned -File (Join-Path $root 'compilar.ps1')
if ($LASTEXITCODE -ne 0) { throw 'No se pudo compilar la app.' }
$adb = Join-Path $env:LOCALAPPDATA 'Android\Sdk\platform-tools\adb.exe'
$devices = @(& $adb devices -l | Where-Object {
    $parts = $_.Split([char[]]@(' ', [char]9), [System.StringSplitOptions]::RemoveEmptyEntries)
    $parts.Count -ge 2 -and $parts[1] -eq 'device' -and !$parts[0].StartsWith('emulator-')
})
if ($devices.Count -eq 1) {
    $serial = $devices[0].Split([char[]]@(' ', [char]9), [System.StringSplitOptions]::RemoveEmptyEntries)[0]
    & $adb -s $serial install -r (Join-Path $root 'app\build\outputs\apk\debug\app-debug.apk')
    if ($LASTEXITCODE -ne 0) { throw 'No se pudo instalar en el telefono.' }
    Write-Host 'Servidor activo y aplicacion actualizada en el telefono.'
} else { Write-Host 'Servidor activo. Conecta un unico telefono USB para instalar el APK actualizado.' }
Write-Host "Direccion actual de la API: http://${lanAddress}:8000/"
