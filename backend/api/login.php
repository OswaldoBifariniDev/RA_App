<?php

declare(strict_types=1);

header('Content-Type: application/json; charset=utf-8');

require_once __DIR__ . '/config/database.php';
require_once __DIR__ . '/controllers/AutenticacionController.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);

    echo json_encode([
        'autenticado' => false,
        'mensaje' => 'Método no permitido.',
        'token' => null,
        'usuario' => null
    ]);

    exit;
}

$contenido = file_get_contents('php://input');

$datos = json_decode(
    $contenido,
    true
);

if (!is_array($datos)) {
    http_response_code(400);

    echo json_encode([
        'autenticado' => false,
        'mensaje' => 'Solicitud inválida.',
        'token' => null,
        'usuario' => null
    ]);

    exit;
}

$identificadorUsuario = trim(
    (string) ($datos['identificadorUsuario'] ?? '')
);

$contrasena = (string) ($datos['contrasena'] ?? '');

if (
    $identificadorUsuario === '' ||
    $contrasena === ''
) {
    http_response_code(400);

    echo json_encode([
        'autenticado' => false,
        'mensaje' => 'Debe ingresar las credenciales.',
        'token' => null,
        'usuario' => null
    ]);

    exit;
}

try {
    $resultado = AutenticacionController::iniciarSesion(
        $connection,
        $identificadorUsuario,
        $contrasena
    );

    http_response_code(
        $resultado['autenticado'] ? 200 : 401
    );

    echo json_encode(
        $resultado,
        JSON_UNESCAPED_UNICODE
    );
} catch (Throwable $error) {
    error_log('Error en login.php: ' . $error->getMessage());

    http_response_code(500);

    echo json_encode([
        'autenticado' => false,
        'mensaje' => 'Error interno del servidor.',
        'token' => null,
        'usuario' => null
    ]);
}