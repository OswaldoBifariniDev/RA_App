<?php
declare(strict_types=1);
// Mismas credenciales locales; catalogo restaurado del respaldo correcto.
require_once __DIR__ . '/database.php';
$catalogConnection = new PDO(
    "pgsql:host={$host};port={$port};dbname=registro_riesgos",
    $userName,
    $password,
    [PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
     PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
     PDO::ATTR_EMULATE_PREPARES => false]
);
