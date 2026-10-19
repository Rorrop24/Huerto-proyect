# Huerto Escolar

Aplicación Android para organizar el trabajo de un huerto escolar. Permite registrar sectores, llevar un listado de cultivos y crear tareas que los estudiantes pueden entregar con una foto y un comentario.

## Qué se puede hacer

- Crear, editar y eliminar sectores y cultivos.
- Registrar tareas con una fecha y un sector del huerto.
- Adjuntar fotos desde la cámara o la galería como evidencia del trabajo.
- Revisar las entregas y aprobarlas o pedir una corrección.
- Consultar lecturas de ejemplo de humedad y temperatura, y simular un cambio de humedad.

Los datos de sectores, cultivos y tareas se guardan en el dispositivo con Room y se conservan al cerrar la aplicación. Para evitar registros sueltos, no se puede borrar un sector si tiene cultivos o tareas asociadas.

## Perfiles

Al abrir la app se puede elegir entre dos perfiles de demostración:

- **Docente:** administra los sectores, cultivos y tareas, y revisa las entregas.
- **Estudiante:** consulta la información y entrega las tareas con una foto y un comentario.

No hace falta una contraseña. Los perfiles son de prueba y, al reiniciar el proceso de la app, hay que volver a elegir uno. Ambos usan los datos del mismo dispositivo.

## Cómo ejecutar el proyecto

1. Abrir la carpeta del proyecto en Android Studio.
2. Usar JDK 17 o superior y tener instalado el SDK de Android 36.1, que es el configurado para compilar.
3. Esperar a que termine la sincronización de Gradle y la descarga de dependencias.
4. Seleccionar un emulador o conectar un teléfono con Android 8.0 (API 26) o superior.
5. Ejecutar el módulo `app` con el botón Run.

Para probar el flujo completo, primero entra como docente y crea un sector, un cultivo y una tarea. Después cambia al perfil de estudiante, entrega la tarea con una foto y un comentario, y vuelve al perfil docente para revisarla.

## Tecnologías

El proyecto está hecho en Kotlin. Usa Jetpack Compose y Material 3 para las pantallas, Navigation Compose para la navegación, ViewModel y StateFlow para el estado, Room para guardar los datos y Coil para mostrar las fotos.

El código principal está en `app/src/main/java/cl/duoc/fotomotion/`:

- `model/`: entidades y acceso a la base de datos.
- `repository/`: operaciones sobre los registros.
- `viewmodel/`: estado de las pantallas y validaciones.
- `ui/`: navegación, pantallas y componentes.

## Pruebas

En `app/src/androidTest/` hay pruebas de persistencia, relaciones entre registros, validación de formularios y navegación entre perfiles. Para ejecutarlas desde PowerShell, con un emulador o teléfono conectado:

```powershell
.\gradlew.bat connectedDebugAndroidTest
```

La carpeta `evidencias/` contiene archivos de compilación y revisión estática.

## Alcance actual

La app trabaja con almacenamiento local, sin servidor ni sincronización entre dispositivos. Las lecturas de humedad y temperatura son simuladas; no están conectadas a sensores reales.
