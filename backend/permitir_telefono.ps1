$ErrorActionPreference = 'Stop'
. (Join-Path $PSScriptRoot 'red_local.ps1')
$lanAddress = Get-RiesgosLanAddress
$name = 'RiesgosRA pruebas PHP 8000 red local'
$rule = Get-NetFirewallRule -DisplayName $name -ErrorAction SilentlyContinue
if ($rule) {
    $rule | Get-NetFirewallAddressFilter | Set-NetFirewallAddressFilter -LocalAddress $lanAddress -RemoteAddress LocalSubnet | Out-Null
    $rule | Enable-NetFirewallRule
} else {
    New-NetFirewallRule -DisplayName $name -Direction Inbound -Action Allow -Protocol TCP -LocalPort 8000 -LocalAddress $lanAddress -RemoteAddress LocalSubnet -Program 'C:\xampp\php\php.exe' -Profile Any | Out-Null
}
