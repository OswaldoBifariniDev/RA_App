<?php

declare(strict_types=1);

header('Content-Type: application/json; charset=utf-8');

require_once __DIR__ . '/config/database.php';
require_once __DIR__ . '/middleware/autenticacion.php';
require_once __DIR__ . '/middleware/autorizacion.php';

$usuarioAutenticado = autenticarSolicitud(
    $connection
);

autorizarPermiso(
    $connection,
    $usuarioAutenticado['idUsuario'],
    'crear_ruta'
);

echo json_encode([
    'autorizado' => true,
    'mensaje' => 'Permiso crear_ruta concedido.',
    'idUsuario' => $usuarioAutenticado['idUsuario'],
    'idSesion' => $usuarioAutenticado['idSesion']
]);