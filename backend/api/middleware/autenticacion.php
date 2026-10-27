<?php

declare(strict_types=1);

require_once __DIR__ . '/../services/TokenService.php';

function autenticarSolicitud($connection): array
{
    $cabeceraAutorizacion = '';

    // Forma principal
    if (!empty($_SERVER['HTTP_AUTHORIZATION'])) {
        $cabeceraAutorizacion = $_SERVER['HTTP_AUTHORIZATION'];
    }

    // Alternativa utilizada por Apache en algunos entornos
    if (
        $cabeceraAutorizacion === '' &&
        !empty($_SERVER['REDIRECT_HTTP_AUTHORIZATION'])
    ) {
        $cabeceraAutorizacion =
            $_SERVER['REDIRECT_HTTP_AUTHORIZATION'];
    }

    // Último recurso: obtener las cabeceras HTTP directamente
    if (
        $cabeceraAutorizacion === '' &&
        function_exists('getallheaders')
    ) {
        $cabeceras = getallheaders();

        foreach ($cabeceras as $nombre => $valor) {
            if (strcasecmp($nombre, 'Authorization') === 0) {
                $cabeceraAutorizacion = $valor;
                break;
            }
        }
    }

    $token = TokenService::extraerToken(
        $cabeceraAutorizacion
    );

    if ($token === null || $token === '') {
        http_response_code(401);

        echo json_encode([
            'autenticado' => false,
            'mensaje' => 'Token de autenticación requerido.'
        ]);

        exit;
    }

    $sesion = TokenService::obtenerSesionPorToken(
        $connection,
        $token
    );

    if ($sesion === null) {
        http_response_code(401);

        echo json_encode([
            'autenticado' => false,
            'mensaje' => 'Token inválido o expirado.'
        ]);

        exit;
    }

    return [
        'idUsuario' => (int) $sesion['id_usuario'],
        'idSesion' => (int) $sesion['id_sesion']
    ];
}