# huerto-api

Microservicio en Spring Boot del huerto escolar. Guarda sectores, cultivos, tareas y problemas en MySQL (XAMPP).

## Cómo levantarlo

1. Abre XAMPP y pulsa **Start** en **MySQL**. Puede quedar Apache apagado.
2. Abre esta carpeta (`backend`) en IntelliJ y espera a que Maven descargue las dependencias.
3. Ejecuta `HuertoApiApplication`.

La primera vez, la base `huerto_db` se crea sola (`createDatabaseIfNotExist=true`) y Flyway crea las tablas
con los scripts de `src/main/resources/db/migration`. Puedes verlas en phpMyAdmin (http://localhost/phpmyadmin).

- API: http://localhost:8080/api/v1/...
- Documentación de endpoints: http://localhost:8080/swagger-ui.html
- Desde el emulador de Android la dirección es `http://10.0.2.2:8080/`.

## Endpoints

| Método | Ruta | Para qué |
|---|---|---|
| GET / POST | `/api/v1/sectores` | Listar / crear sectores |
| GET / PUT / DELETE | `/api/v1/sectores/{id}` | Ver / editar / borrar un sector |
| GET / POST | `/api/v1/cultivos` (`?sectorId=1`) | Listar / registrar cultivos |
| GET / PUT / DELETE | `/api/v1/cultivos/{id}` | Ver / editar / borrar un cultivo |
| GET / POST | `/api/v1/tareas` (`?responsableId=USR-002`) | Listar / crear tareas |
| GET / PUT / DELETE | `/api/v1/tareas/{id}` | Ver / editar / borrar una tarea |
| PUT | `/api/v1/tareas/{id}/entrega` | El estudiante entrega foto y comentario |
| PUT | `/api/v1/tareas/{id}/revision` | El docente aprueba o rechaza |
| GET / POST | `/api/v1/problemas` | Listar / reportar problemas |
| GET / DELETE | `/api/v1/problemas/{id}` | Ver / borrar un reporte |
| PUT | `/api/v1/problemas/{id}/seguimiento` | El docente cambia el estado y comenta |

Los errores llegan siempre como `{"status": 409, "mensaje": "..."}`.

## Reglas de negocio

- No se borra un sector con cultivos, tareas o problemas, ni un cultivo con tareas o problemas.
- Un sector **Fuera de uso** no recibe cultivos ni tareas nuevas.
- El trasplante y la cosecha no pueden ser antes de la siembra; el riego va de 1 a 30 días.
- Solo el estudiante asignado puede entregar su tarea, y nadie puede validar su propia tarea.
- Para rechazar una tarea el docente debe explicar qué corregir.
- La fecha de cierre de un problema se guarda la primera vez que queda Atendido o Cerrado.

## Pruebas

`mvn test` (o clic derecho en `src/test/java` → Run tests en IntelliJ). No necesitan XAMPP:

- `service/*ServiceTest`: pruebas unitarias de las reglas con Mockito (el repositorio es simulado).
- `HuertoApiIntegracionTest`: levanta todo con una base H2 en memoria, ejecuta Flyway y llama a los endpoints.

## Cambiar la base de datos

No edites `V1__crear_tablas.sql` después de haberlo ejecutado: Flyway lo detecta y no levanta.
Crea un script nuevo, por ejemplo `V3__agregar_columna.sql`.
