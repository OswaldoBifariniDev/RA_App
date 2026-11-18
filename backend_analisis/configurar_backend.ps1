param([string]$RutaPropiedades)
$ErrorActionPreference = 'Stop'
if ($RutaPropiedades) {
    $contenido = [IO.File]::ReadAllText($RutaPropiedades)
    $coincidencia = [regex]::Match($contenido, '(?m)^GEMINI_API_KEY=(.+)$')
    if (!$coincidencia.Success) { throw 'No hay una clave configurada en ese archivo.' }
    $claveProtegida = ConvertTo-SecureString $coincidencia.Groups[1].Value.Trim() -AsPlainText -Force
    $contenido = $null
} else {
    $claveProtegida = Read-Host 'Clave del servicio de análisis (entrada oculta)' -AsSecureString
}
$carpetaPrivada = Join-Path $PSScriptRoot '.local'
New-Item -ItemType Directory -Path $carpetaPrivada -Force | Out-Null
$claveProtegida | ConvertFrom-SecureString | Set-Content (Join-Path $carpetaPrivada 'clave_protegida.txt')
Write-Output 'Configuracion privada guardada. La APK de pruebas integra la clave al compilar; no publiques el instalador.'
