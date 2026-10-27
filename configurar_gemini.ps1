param([switch]$SelfTest)

$ErrorActionPreference = 'Stop'

$root = $PSScriptRoot

Add-Type -AssemblyName System.Windows.Forms

Add-Type -AssemblyName System.Drawing

[System.Windows.Forms.Application]::EnableVisualStyles()

trap {

    if ($SelfTest) { Write-Output 'Fallo de prueba del asistente.'; exit 1 }

    [System.Windows.Forms.MessageBox]::Show($_.Exception.Message, 'Configuracion de Gemini', 'OK', 'Error') | Out-Null

    exit 1

}

$form = New-Object System.Windows.Forms.Form

$form.Text = 'Configurar Gemini - asistente corregido'

$form.ClientSize = New-Object System.Drawing.Size(570,200)

$form.StartPosition = 'CenterScreen'

$form.FormBorderStyle = 'FixedDialog'

$form.MaximizeBox = $false

$form.MinimizeBox = $false

$form.TopMost = $true

$label = New-Object System.Windows.Forms.Label

$label.Text = "Primero elimina la clave expuesta y crea una nueva en Google AI Studio.`r`nPega la clave NUEVA en el campo de abajo. No la pegues en PowerShell."

$label.SetBounds(18,18,534,48)

$keyTextBox = New-Object System.Windows.Forms.TextBox

$keyTextBox.UseSystemPasswordChar = $true

$keyTextBox.MaxLength = 512

$keyTextBox.SetBounds(18,78,534,28)

$ok = New-Object System.Windows.Forms.Button

$ok.Text = 'Guardar y actualizar app'

$ok.SetBounds(190,135,210,36)

$ok.DialogResult = [System.Windows.Forms.DialogResult]::OK

$cancel = New-Object System.Windows.Forms.Button

$cancel.Text = 'Cancelar'

$cancel.SetBounds(412,135,140,36)

$cancel.DialogResult = [System.Windows.Forms.DialogResult]::Cancel

$form.Controls.AddRange(@($label,$keyTextBox,$ok,$cancel))

$form.AcceptButton = $ok

$form.CancelButton = $cancel

$form.ActiveControl = $keyTextBox

if ($SelfTest) {

    if (!$keyTextBox.UseSystemPasswordChar) { throw 'El campo de clave no esta protegido.' }

    $keyTextBox.Text = 'test-value-not-a-real-credential'

    if (!$keyTextBox.Text) { throw 'El campo no acepta texto.' }

    $keyTextBox.Clear()

    $form.ShowInTaskbar = $false

    $form.Opacity = 0

    $form.Add_Shown({ param($sender, $eventArgs) $sender.DialogResult = [System.Windows.Forms.DialogResult]::Cancel })

    $null = $form.ShowDialog()

    $form.Dispose()

    Write-Output 'Prueba correcta: ventana abierta y cerrada sin errores; campo oculto verificado. No se leyeron claves ni se realizaron solicitudes.'

    exit 0

}

if ($form.ShowDialog() -ne [System.Windows.Forms.DialogResult]::OK) { $form.Dispose(); exit 0 }

$key = $keyTextBox.Text.Trim()

$keyTextBox.Clear()

$form.Dispose()

if ($key -notmatch '^[a-zA-Z0-9_.-]{20,}$') { throw 'Copia la clave completa desde AI Studio y vuelve a abrir el asistente.' }

# Verificar acceso al modelo sin imprimir la clave ni enviar fotografias.

try {

    $probeBody = @{contents=@(@{parts=@(@{text='Responde solamente OK.'})});generationConfig=@{maxOutputTokens=64}} | ConvertTo-Json -Depth 8 -Compress
    $null = Invoke-RestMethod -Uri 'https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent' -Headers @{'x-goog-api-key'=$key} -Method Post -ContentType 'application/json' -Body $probeBody

} catch {

    throw 'Google no permitio validar esta clave/modelo. Revisa el proyecto y los permisos en AI Studio.'

}

$file = Join-Path $root 'local.properties'

$text = [System.IO.File]::ReadAllText($file)

$text = [regex]::Replace($text, '(?m)^GEMINI_API_KEY=.*\r?\n?', '')

[System.IO.File]::WriteAllText($file, $text.TrimEnd() + "`r`nGEMINI_API_KEY=$key`r`n", [System.Text.UTF8Encoding]::new($false))

$key = $null

Write-Host 'Clave validada y guardada localmente. Compilando...'

& powershell.exe -NoProfile -ExecutionPolicy RemoteSigned -File (Join-Path $root 'compilar.ps1')

if ($LASTEXITCODE -ne 0) { throw 'La compilacion no termino correctamente.' }

$adb = Join-Path $env:LOCALAPPDATA 'Android\Sdk\platform-tools\adb.exe'

$devices = @(& $adb devices -l | Select-String '^\S+\s+device\b' | Where-Object { $_.Line -notlike 'emulator-*' })

if ($devices.Count -eq 1) {

    $serial = ($devices[0].Line -split '\s+')[0]

    & $adb -s $serial install -r (Join-Path $root 'app\build\outputs\apk\debug\app-debug.apk')

    if ($LASTEXITCODE -ne 0) { throw 'No se pudo instalar en el telefono.' }

    Write-Host 'Aplicacion actualizada en el telefono. Ya puedes probar una foto.'

} else { Write-Host 'APK compilado. Conecta un unico telefono USB para instalarlo.' }

[System.Windows.Forms.MessageBox]::Show('Proceso completado. Revisa el resultado de la instalacion en la ventana de PowerShell.', 'Configurar Gemini') | Out-Null 

