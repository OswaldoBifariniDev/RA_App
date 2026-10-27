-- DATOS DEMOSTRATIVOS MINIMOS para probar login y ruta_crear.php.
-- NO es una exportacion fiel de los datos locales ni debe ejecutarse en la base oficial.
-- Credenciales SOLO para pruebas: jostin_prueba@riesgosra.local / Prueba123!
-- Ejecutar despues de 01_ESTRUCTURA_POSTGRESQL.sql y solo en una base de pruebas vacia.
-- Si ya existe el usuario, este script no cambia su contrasena ni sus datos.

BEGIN;

INSERT INTO public.roles (nombre_rol)
SELECT 'administrador'
WHERE NOT EXISTS (
    SELECT 1 FROM public.roles WHERE nombre_rol = 'administrador'
);

INSERT INTO public.permisos (nombre_permiso)
SELECT 'crear_ruta'
WHERE NOT EXISTS (
    SELECT 1 FROM public.permisos WHERE nombre_permiso = 'crear_ruta'
);

INSERT INTO public.roles_permisos (id_rol, id_permiso)
SELECT r.id_rol, p.id_permiso
FROM public.roles r
CROSS JOIN public.permisos p
WHERE r.nombre_rol = 'administrador'
  AND p.nombre_permiso = 'crear_ruta'
  AND NOT EXISTS (
      SELECT 1 FROM public.roles_permisos rp
      WHERE rp.id_rol = r.id_rol AND rp.id_permiso = p.id_permiso
  );

INSERT INTO public.usuarios (identificador_usuario, contrasena_hash, id_rol)
SELECT
    'jostin_prueba@riesgosra.local',
    '$2y$12$Y4uYjTFZPWhm8mHuF/7/fOJCCLQZIkayJLQISxWBpwrWVNayU.aJ.',
    r.id_rol
FROM public.roles r
WHERE r.nombre_rol = 'administrador'
  AND NOT EXISTS (
      SELECT 1 FROM public.usuarios u
      WHERE u.identificador_usuario = 'jostin_prueba@riesgosra.local'
  );

COMMIT;

-- La tabla sesiones queda vacia inicialmente; login.php creara nuevas sesiones.
