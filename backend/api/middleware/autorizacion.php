<?php

declare(strict_types=1);

require_once __DIR__ . '/../services/AutorizacionService.php';

function autorizarPermiso(
    $connection,
    int $idUsuario,
    string $nombrePermiso
): void {
    $tienePermiso = AutorizacionService::tienePermiso(
        $connection,
        $idUsuario,
        $nombrePermiso
    );

    if (!$tienePermiso) {
        http_response_code(403);

        echo json_encode([
            'autorizado' => false,
            'mensaje' => 'No tiene permisos para realizar esta operación.'
        ]);

        exit;
    }
}