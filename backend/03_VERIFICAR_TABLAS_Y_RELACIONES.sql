-- Comprobacion de conteos de la base actual.
SELECT 'roles' AS tabla, COUNT(*) AS registros FROM public.roles
UNION ALL SELECT 'permisos', COUNT(*) FROM public.permisos
UNION ALL SELECT 'roles_permisos', COUNT(*) FROM public.roles_permisos
UNION ALL SELECT 'usuarios', COUNT(*) FROM public.usuarios
UNION ALL SELECT 'sesiones', COUNT(*) FROM public.sesiones;

-- Lista las cuatro relaciones FK conocidas.
SELECT
    tc.table_name AS tabla,
    tc.constraint_name AS relacion,
    kcu.column_name AS columna,
    ccu.table_name AS tabla_referenciada,
    ccu.column_name AS columna_referenciada
FROM information_schema.table_constraints tc
JOIN information_schema.key_column_usage kcu
  ON tc.constraint_name = kcu.constraint_name
 AND tc.constraint_schema = kcu.constraint_schema
 AND tc.table_name = kcu.table_name
JOIN information_schema.constraint_column_usage ccu
  ON tc.constraint_name = ccu.constraint_name
 AND tc.constraint_schema = ccu.constraint_schema
WHERE tc.constraint_type = 'FOREIGN KEY'
  AND tc.table_schema = 'public'
  AND tc.table_name IN ('roles_permisos','usuarios','sesiones')
ORDER BY tc.table_name, tc.constraint_name;
