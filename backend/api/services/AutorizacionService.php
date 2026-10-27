<?php

declare(strict_types=1);

class AutorizacionService
{
    public static function tienePermiso(
        PDO $connection,
        int $idUsuario,
        string $nombrePermiso
    ): bool {
        $sql = '
            SELECT 1
            FROM public.usuarios AS u
            INNER JOIN public.roles AS r
                ON r.id_rol = u.id_rol
            INNER JOIN public.roles_permisos AS rp
                ON rp.id_rol = r.id_rol
            INNER JOIN public.permisos AS p
                ON p.id_permiso = rp.id_permiso
            WHERE u.id_usuario = :id_usuario
              AND p.nombre_permiso = :nombre_permiso
            LIMIT 1
        ';

        $statement = $connection->prepare($sql);
        $statement->execute([
            'id_usuario' => $idUsuario,
            'nombre_permiso' => $nombrePermiso,
        ]);

        return $statement->fetchColumn() !== false;
    }
}
