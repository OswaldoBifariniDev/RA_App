<?php

declare(strict_types=1);

class TokenService
{
    private const DURACION_TOKEN_SEGUNDOS = 3600;

    public static function crearToken(
        PDO $connection,
        int $idUsuario
    ): string {
        // El token original se entrega a la app; en la base solo se guarda su hash.
        $token = bin2hex(random_bytes(32));
        $tokenHash = hash('sha256', $token);

        /*
         * Las columnas de fecha en PostgreSQL son timestamp without time zone.
         * LOCALTIMESTAMP mantiene ambas fechas en el mismo marco horario.
         * estado se migró como bit(1), por eso se utiliza el literal B'1'.
         */
        $sql = "
            INSERT INTO public.sesiones (
                id_usuario,
                token_hash,
                fecha_creacion,
                fecha_expiracion,
                estado
            )
            VALUES (
                :id_usuario,
                :token_hash,
                LOCALTIMESTAMP,
                LOCALTIMESTAMP + INTERVAL '1 hour',
                B'1'
            )
        ";

        $statement = $connection->prepare($sql);
        $statement->execute([
            'id_usuario' => $idUsuario,
            'token_hash' => $tokenHash,
        ]);

        return $token;
    }

    public static function obtenerSesionPorToken(
        PDO $connection,
        string $token
    ): ?array {
        $tokenHash = hash('sha256', $token);

        $sql = "
            SELECT
                s.id_sesion,
                s.id_usuario,
                s.fecha_expiracion,
                s.estado
            FROM public.sesiones AS s
            WHERE s.token_hash = :token_hash
              AND s.estado = B'1'
              AND s.fecha_expiracion > LOCALTIMESTAMP
            LIMIT 1
        ";

        $statement = $connection->prepare($sql);
        $statement->execute(['token_hash' => $tokenHash]);

        $sesion = $statement->fetch(PDO::FETCH_ASSOC);
        return is_array($sesion) ? $sesion : null;
    }

    public static function extraerToken(
        string $cabeceraAutorizacion
    ): ?string {
        if (!preg_match('/Bearer\s+(.+)/i', $cabeceraAutorizacion, $coincidencias)) {
            return null;
        }

        return trim($coincidencias[1]);
    }
}
