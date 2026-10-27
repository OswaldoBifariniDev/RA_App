<?php

declare(strict_types=1);

header('Content-Type: text/plain; charset=utf-8');
require_once __DIR__ . '/config/database.php';

$resultado = $connection->query(
    'SELECT current_database() AS nombre_base, current_user AS usuario_base_datos'
)->fetch(PDO::FETCH_ASSOC);

echo "CONEXION CORRECTA\n";
echo 'Base de datos: ' . $resultado['nombre_base'] . "\n";
echo 'Usuario de base de datos: ' . $resultado['usuario_base_datos'] . "\n";
