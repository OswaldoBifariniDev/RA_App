<?php

declare(strict_types=1);

class Usuario
{
    public static function buscarPorIdentificador(
        PDO $connection,
        string $identificadorUsuario
    ): ?array {
        $sql = '
            SELECT
                u.id_usuario,
                u.identificador_usuario,
                u.contrasena_hash,
                u.id_rol,
                r.nombre_rol AS rol_usuario
            FROM public.usuarios AS u
            INNER JOIN public.roles AS r
                ON r.id_rol = u.id_rol
            WHERE u.identificador_usuario = :identificador_usuario
            LIMIT 1
        ';

        $statement = $connection->prepare($sql);
        $statement->execute([
            'identificador_usuario' => $identificadorUsuario,
        ]);

        $usuario = $statement->fetch(PDO::FETCH_ASSOC);
        return is_array($usuario) ? $usuario : null;
    }
}
