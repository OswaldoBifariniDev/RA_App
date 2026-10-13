# Publicación del proyecto RiesgosRA en la rama Jostin

## Estado

Este directorio conserva el historial Git local recibido de `RA_App` y aplica el proyecto integrado con arquitectura `model`, `presenter` y `view`, incluyendo el login conectado a la API PHP. No se ha hecho ningún push ni se ha modificado `main` en GitHub.

## Antes de ejecutar

1. Mantén Apache (XAMPP) y SQL Server activos.
2. En el teléfono, verifica en el navegador `http://192.168.10.105/riesgos_ra_api/prueba_conexion.php`.
3. El archivo `app/src/main/java/com/riesgosra/app/model/network/ApiService.kt` usa la IP local `192.168.10.105`, validada en la red usada para las pruebas. Si cambia la IP del PC, actualiza `BASE_URL`. Para el emulador, utiliza `http://10.0.2.2/riesgos_ra_api/`.
4. Abre en Android Studio la raíz del proyecto (la carpeta que contiene `settings.gradle.kts`), sincroniza Gradle y ejecuta `app`.

## Comprobar historial y cambios

Desde CMD o la terminal de Android Studio, en la raíz de este repositorio:

```powershell
git status --short --branch
git log --oneline --decorate -8
git remote -v
```

La rama debe ser `Jostin` y `origin` debe apuntar a `https://github.com/OswaldoBifariniDev/RA_App.git`.

## Commit y publicación únicamente en Jostin

Después de revisar y confirmar que el proyecto ejecuta correctamente:

```powershell
git add -A
git status
git commit -m "feat: integrar autenticacion API con arquitectura MVP"
git push origin Jostin
```

No ejecutes `git push origin main`, `git push --force` ni borres ramas. El push normal a `Jostin` conserva los commits anteriores y no actualiza la rama `main`.

## Verificar en GitHub

Abre `https://github.com/OswaldoBifariniDev/RA_App/tree/Jostin` y confirma que aparezcan `app/src/main/java/com/riesgosra/app/model`, `presenter` y `view`. Revisa también la pestaña **Commits** para ver los commits previos más el nuevo.
