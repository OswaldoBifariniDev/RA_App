<?php

declare(strict_types=1);

require_once __DIR__ . '/../models/Usuario.php';
require_once __DIR__ . '/../services/TokenService.php';

class AutenticacionController
{
    public static function iniciarSesion(
        $connection,
        string $identificadorUsuario,
        string $contrasena
    ): array {
        $usuario = Usuario::buscarPorIdentificador(
            $connection,
            $identificadorUsuario
        );

        if (
            $usuario === null ||
            !password_verify(
                $contrasena,
                $usuario['contrasena_hash']
            )
        ) {
            return [
                'autenticado' => false,
                'mensaje' => 'Credenciales incorrectas.',
                'token' => null,
                'usuario' => null
            ];
        }

        $token = TokenService::crearToken(
            $connection,
            (int) $usuario['id_usuario']
        );

        return [
            'autenticado' => true,
            'mensaje' => 'Inicio de sesión correcto.',
            'token' => $token,
            'usuario' => [
                'idUsuario' => (int) $usuario['id_usuario'],
                'identificadorUsuario' =>
                    $usuario['identificador_usuario'],
                'rolUsuario' => $usuario['rol_usuario']
            ]
        ];
    }
}