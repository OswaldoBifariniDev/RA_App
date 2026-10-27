-- Ejecutar DESPUES de importar filas con identificadores explicitos.
-- No elimina ni modifica filas; ajusta la proxima identidad al maximo existente.
SELECT setval(pg_get_serial_sequence('public.roles','id_rol'), COALESCE(MAX(id_rol),1), MAX(id_rol) IS NOT NULL) FROM public.roles;
SELECT setval(pg_get_serial_sequence('public.permisos','id_permiso'), COALESCE(MAX(id_permiso),1), MAX(id_permiso) IS NOT NULL) FROM public.permisos;
SELECT setval(pg_get_serial_sequence('public.usuarios','id_usuario'), COALESCE(MAX(id_usuario),1), MAX(id_usuario) IS NOT NULL) FROM public.usuarios;
SELECT setval(pg_get_serial_sequence('public.sesiones','id_sesion'), COALESCE(MAX(id_sesion),1), MAX(id_sesion) IS NOT NULL) FROM public.sesiones;
