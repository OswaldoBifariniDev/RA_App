function Get-RiesgosLanAddress {
    $config = Get-NetIPConfiguration | Where-Object {
        $_.IPv4DefaultGateway -and $_.NetAdapter.Status -eq 'Up'
    } | Select-Object -First 1
    if (!$config) { throw 'Conecta el computador a la misma red Wi-Fi que el telefono.' }
    $address = @($config.IPv4Address.IPAddress)[0]
    if (!$address) { throw 'No se encontro la direccion IPv4 de la red local.' }
    return $address
}
