<?php
declare(strict_types=1);
header('Content-Type: application/json; charset=utf-8');
header('Cache-Control: no-store');
if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    http_response_code(405);
    header('Allow: GET');
    echo json_encode(['error' => true, 'mensaje' => 'Metodo no permitido.']);
    exit;
}
require_once __DIR__ . '/config/database.php';
try {
    require_once __DIR__ . '/config/catalog_database.php';
    // Mantener IDs, analisis y planes existentes. Los consejos confirmados por el
    // usuario pertenecen a public.riesgos; relacionar por tipo, nunca por sus IDs
    // (las dos tablas numeran incendio y sismo en distinto orden). Solo lectura.
    $statement = $catalogConnection->query('SELECT i.id AS id_riesgo, i.nombre, i.analisis_riesgo, i.plan_accion,
        COALESCE((SELECT r.consejos_estudiante FROM public.riesgos r
            WHERE (i.nombre ILIKE $$%incend%$$ AND r.nombre ILIKE $$%incend%$$)
               OR (i.nombre ILIKE $$%sism%$$ AND r.nombre ILIKE $$%sism%$$)
               OR (i.nombre ILIKE $$%inund%$$ AND r.nombre ILIKE $$%inund%$$)
            ORDER BY r.id LIMIT 1), i.consejos_estudiante) AS consejos_estudiante
        FROM public.informacion_riesgos i ORDER BY i.id');
    $rows = $statement->fetchAll(PDO::FETCH_ASSOC);
    foreach ($rows as &$row) { $row['id_riesgo'] = (int) $row['id_riesgo']; }
    unset($row);
    echo json_encode($rows, JSON_UNESCAPED_UNICODE | JSON_THROW_ON_ERROR);
} catch (Throwable $error) {
    error_log('No se pudo consultar informacion_riesgos: ' . $error->getMessage());
    http_response_code(500);
    echo json_encode(['error' => true, 'mensaje' => 'No se pudo consultar el catalogo de riesgos en PostgreSQL.']);
}
