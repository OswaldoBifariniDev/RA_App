$ErrorActionPreference = 'Stop'
Import-Module (Join-Path $PSHOME 'Modules\Microsoft.PowerShell.Security\Microsoft.PowerShell.Security.psd1') -ErrorAction Stop
$carpetaPrivada = Join-Path $PSScriptRoot '.local'
$salud = $null
try { $salud = Invoke-RestMethod 'http://127.0.0.1:3031/health' -TimeoutSec 2 } catch {}
if ($salud -and $salud.servicio -ne 'riesgora-analisis') { throw 'El puerto 3031 está ocupado por otro servicio.' }
if (!$salud) {
    $archivoClave = Join-Path $carpetaPrivada 'clave_protegida.txt'
    if (!(Test-Path -LiteralPath $archivoClave)) {
        Write-Warning 'El análisis local no está configurado. Ejecuta backend_analisis/configurar_backend.ps1 en la computadora.'
        exit 0
    }
    $segura = (Get-Content -LiteralPath $archivoClave -Raw).Trim() | ConvertTo-SecureString
    $puntero = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($segura)
    try {
        $env:GEMINI_API_KEY = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($puntero)
        $env:GEMINI_MODEL = 'gemini-3.1-flash-lite'
        $node = (Get-Command node.exe -ErrorAction SilentlyContinue).Source
        if (!$node) { $node = Join-Path $env:ProgramFiles 'nodejs\node.exe' }
        Start-Process -FilePath $node -ArgumentList 'server.js' -WorkingDirectory $PSScriptRoot -WindowStyle Hidden `
            -RedirectStandardOutput (Join-Path $carpetaPrivada 'servicio.log') `
            -RedirectStandardError (Join-Path $carpetaPrivada 'errores.log') | Out-Null
    } finally {
        [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($puntero)
        $env:GEMINI_API_KEY = $null
    }
    for ($intento = 0; $intento -lt 20; $intento++) {
        try { $salud = Invoke-RestMethod 'http://127.0.0.1:3031/health' -TimeoutSec 1; break } catch { Start-Sleep -Milliseconds 250 }
    }
}
if (!$salud -or !$salud.configurado) { throw 'El servicio de análisis no pudo iniciarse correctamente.' }
$sdk = if ($env:ANDROID_HOME) { $env:ANDROID_HOME } else { Join-Path $env:LOCALAPPDATA 'Android\Sdk' }
$adb = Join-Path $sdk 'platform-tools\adb.exe'
if (Test-Path -LiteralPath $adb) {
    $dispositivos = & $adb devices
    foreach ($linea in $dispositivos) {
        if ($linea -match '^(\S+)\s+device\b') {
            & $adb -s $Matches[1] reverse tcp:3031 tcp:3031 | Out-Null
        }
    }
}
Write-Output 'Servicio de análisis listo. Conexión preparada para los dispositivos conectados.'
