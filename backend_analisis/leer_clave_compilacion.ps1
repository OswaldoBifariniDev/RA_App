$ErrorActionPreference = 'Stop'
Import-Module (Join-Path $PSHOME 'Modules\Microsoft.PowerShell.Security\Microsoft.PowerShell.Security.psd1') -ErrorAction Stop
$archivo = Join-Path $PSScriptRoot '.local\clave_protegida.txt'
if (!(Test-Path -LiteralPath $archivo)) { throw 'No se encontro la configuracion privada de Gemini.' }
$segura = (Get-Content -LiteralPath $archivo -Raw).Trim() | ConvertTo-SecureString
$puntero = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($segura)
try {
    # Gradle captura esta salida en memoria; no se debe ejecutar como comando de diagnostico.
    [Console]::Out.Write([Runtime.InteropServices.Marshal]::PtrToStringBSTR($puntero))
} finally {
    [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($puntero)
}
