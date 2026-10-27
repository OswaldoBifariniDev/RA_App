<?php

declare(strict_types=1);

header('Content-Type: application/json; charset=utf-8');

require_once __DIR__ . '/config/database.php';
require_once __DIR__ . '/middleware/autenticacion.php';

$usuarioAutenticado = autenticarSolicitud(
    $connection
);

echo json_encode([
    'autorizado' => true,
    'mensaje' => 'Acceso permitido a la ruta protegida.',
    'idUsuario' => $usuarioAutenticado['idUsuario'],
    'idSesion' => $usuarioAutenticado['idSesion']
]);