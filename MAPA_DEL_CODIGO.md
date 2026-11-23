# Mapa del código — Huerto Escolar

Todo el código de la app está en `app/src/main/java/cl/duoc/fotomotion/`.
Las rutas de abajo parten desde ahí.

## Cómo está ordenado

```
data/        → dónde se guardan los datos (Room)
  local/       BaseDatosHuerto (la base), HuertoDao (las consultas SQL), ConvertidoresHuerto (enum ↔ texto)
  repository/  RepositorioHuerto (sectores, cultivos, tareas, problemas) y RepositorioCuentas (login y registro)
domain/      → reglas del negocio, sin nada de pantallas
  model/       Sector, Cultivo, Tarea, Problema, Usuario, Cuenta, LecturaSensor, Alerta + sus enum
  validation/  un Validador por formulario + ValidacionesHuerto (reglas comunes)
  seguridad/   CifradorContrasenas (hash de contraseñas)
  alertas/     GeneradorAlertas (recordatorios)
ui/          → lo que se ve
  navigation/  RecorridoHuerto (arma la app), RutasHuerto (nombres de rutas), BarrasHuerto (barra superior e inferior)
  components/  piezas reutilizables (campos, selectores, mensajes, etiquetas de estado, foto)
  theme/       Color.kt, Type.kt, Theme.kt
  <módulo>/    cada pantalla tiene: Pantalla, ViewModel, UiState, y sus Formulario/Tarjeta
```

Cada módulo de `ui/` sigue el mismo patrón. Por ejemplo, para **tareas**:

| Archivo | Qué hace |
|---|---|
| `ui/tareas/PantallaTareas.kt` | La pantalla: lista de tareas y qué formulario se muestra |
| `ui/tareas/FormularioTarea.kt` | Formulario del docente para crear/editar y asignar |
| `ui/tareas/FormularioEntrega.kt` | Formulario del estudiante para entregar con foto y comentario |
| `ui/tareas/TarjetaTarea.kt` | La tarjeta de cada tarea (aprobar, rechazar, editar, borrar) |
| `ui/tareas/TareasUiState.kt` | Todos los datos que muestra la pantalla |
| `ui/tareas/TareasViewModel.kt` | Las acciones: guardar, entregar, revisar, borrar |
| `domain/validation/ValidadorTareas.kt` | Las reglas que debe cumplir una tarea o una entrega |
| `domain/model/Tarea.kt` | La tabla `tareas`, los enum `TipoTarea` y `EstadoTarea` |

En las pantallas siempre se usan los mismos nombres: `viewModel` es el ViewModel de la pantalla y `uiState` su estado.
En los ViewModel, `_uiState` es el estado que se modifica y `uiState` el que lee la pantalla.

## Si te piden… abre aquí

### Inicio de sesión y cuentas
| Te piden | Archivo | Función / parte |
|---|---|---|
| Dónde se inicia sesión | `ui/acceso/AccesoViewModel.kt` | `ingresar()` |
| Dónde se registra un usuario | `ui/acceso/AccesoViewModel.kt` | `registrarse()` |
| Dónde se cierra sesión | `ui/navigation/RecorridoHuerto.kt` | `alSalir` → `accesoViewModel.salirDeCuenta()` |
| Cómo se comprueba la contraseña | `data/repository/RepositorioCuentas.kt` | `ingresar()` |
| Que no se repita el correo | `data/repository/RepositorioCuentas.kt` | `registrarEstudiante()` |
| Que la contraseña no esté en texto plano | `domain/seguridad/CifradorContrasenas.kt` | `cifrar()`, `verificar()` |
| Las 4 cuentas de prueba | `domain/model/CuentasDemo.kt` | `lista` |
| Dónde se crean en la base | `data/local/BaseDatosHuerto.kt` | `sembrarCuentasDemo()` |
| Validación de correo y contraseña | `domain/validation/ValidadorAcceso.kt` | `validarIngreso()`, `validarRegistro()` |
| Roles (docente / alumno) | `domain/model/Cuenta.kt` | `enum class TipoCuenta` |

### Sectores, cultivos y tareas
| Te piden | Archivo | Función / parte |
|---|---|---|
| Crear o editar un sector | `ui/sectores/SectoresViewModel.kt` | `guardarSector()`, `editarSector()` |
| Borrar un sector | `ui/sectores/SectoresViewModel.kt` | `eliminarSector()` |
| Crear o editar un cultivo | `ui/cultivos/CultivosViewModel.kt` | `guardarCultivo()`, `editarCultivo()` |
| Crear una tarea y asignarla | `ui/tareas/TareasViewModel.kt` | `guardarTarea()`, `elegirEstudiante()` |
| Que el estudiante vea solo sus tareas | `domain/model/Tarea.kt` | `visiblesPara()` |
| Entregar una tarea con foto | `ui/tareas/TareasViewModel.kt` | `entregarTarea()` |
| Aprobar o rechazar la evidencia | `ui/tareas/TareasViewModel.kt` | `revisarEntrega()` |
| Tomar o elegir la foto | `ui/components/AdjuntarFoto.kt` | `AdjuntarFoto()` |
| Reportar un problema | `ui/problemas/ProblemasViewModel.kt` | `reportarProblema()` |
| Lecturas simuladas | `ui/lecturas/LecturasViewModel.kt` | `simularRiego()` |
| Recordatorios (campana) | `domain/alertas/GeneradorAlertas.kt` | `generar()` |

### Formularios
| Te piden | Archivo | Función / parte |
|---|---|---|
| Que el formulario se limpie al guardar o cancelar | cada `*ViewModel.kt` | `limpiarFormulario()` |
| Que un formulario nuevo parta vacío | cada `*ViewModel.kt` | `abrirFormularioNuevo()` |
| Que al editar se carguen los datos | cada `*ViewModel.kt` | `editarSector()`, `editarCultivo()`, `editarTarea()` |
| Que si falla se conserven los datos | cada `*ViewModel.kt` | bloque `catch` de `guardar…()` |
| Que no se guarde dos veces | cada `*ViewModel.kt` | `formulario.guardando` al inicio de `guardar…()` |
| Que no se pierdan datos al girar | cada `*ViewModel.kt` + `*UiState.kt` | el ViewModel sobrevive a la rotación |
| El diálogo de borrar al girar | `ui/sectores/PantallaSectores.kt` | `rememberSaveable { idPorBorrar }` |

### Validaciones (`domain/validation/`)
| Formulario | Archivo |
|---|---|
| Login y registro | `ValidadorAcceso.kt` |
| Sector | `ValidadorSectores.kt` |
| Cultivo | `ValidadorCultivos.kt` |
| Tarea y entrega (foto obligatoria) | `ValidadorTareas.kt` |
| Problema y seguimiento | `ValidadorProblemas.kt` |
| Reglas comunes (texto, correo, fecha, número) | `ValidacionesHuerto.kt` |

### Enum
| Enum | Archivo |
|---|---|
| `TipoCuenta` (DOCENTE, ALUMNO) | `domain/model/Cuenta.kt` |
| `EstadoTarea` (PENDIENTE, PENDIENTE_DE_VALIDACION, VALIDADA, RECHAZADA), `TipoTarea` | `domain/model/Tarea.kt` |
| `EstadoSector` | `domain/model/Sector.kt` |
| `EstadoCultivo` | `domain/model/Cultivo.kt` |
| `EstadoProblema`, `TipoProblema` | `domain/model/Problema.kt` |
| Cómo Room guarda los enum | `data/local/ConvertidoresHuerto.kt` |

### Base de datos (Room)
| Te piden | Archivo | Función / parte |
|---|---|---|
| La base y su versión | `data/local/BaseDatosHuerto.kt` | `@Database` (versión 3) |
| Las migraciones | `data/local/BaseDatosHuerto.kt` | `MIGRACION_1_2`, `MIGRACION_2_3` |
| Las consultas SQL | `data/local/HuertoDao.kt` | `@Query`, `@Insert`, `@Update`, `@Delete` |
| Las tablas | `domain/model/` | `@Entity` en Sector, Cultivo, Tarea, Problema, Usuario |

### Diseño, navegación y animaciones
| Te piden | Archivo | Función / parte |
|---|---|---|
| Colores | `ui/theme/Color.kt` | |
| Tema claro / oscuro | `ui/theme/Theme.kt` | `TemaHuerto()` |
| Tipografía | `ui/theme/Type.kt` | |
| Color de cada estado | `ui/components/TonosEstado.kt` | `tono()` |
| Navegación entre pantallas | `ui/navigation/RecorridoHuerto.kt` | `NavHost` |
| Nombres de las rutas | `ui/navigation/RutasHuerto.kt` | |
| Barra superior e inferior | `ui/navigation/BarrasHuerto.kt` | `BarraSuperiorHuerto()`, `BarraInferiorHuerto()` |
| Menú de bienvenida | `ui/inicio/PantallaInicio.kt` | |
| Animación de formularios | `ui/components/TarjetaFormulario.kt` | `AnimatedVisibility` |
| Animación de mensajes de error | `ui/components/MensajesHuerto.kt` | `ErrorHuerto()` |
| Animación de estado de tarea | `ui/components/EtiquetaEstado.kt` | `animateColorAsState`, `AnimatedContent` |
| Animación de lecturas | `ui/lecturas/TarjetaLectura.kt` | `animateFloatAsState` |
| Cambio entre pantallas | `ui/navigation/RecorridoHuerto.kt` | `fadeIn` / `fadeOut` |
| Dónde se crean los ViewModels | `ui/FabricaViewModels.kt` | |

## Pruebas

| Tipo | Carpeta | Cómo se ejecutan |
|---|---|---|
| Unitarias (JUnit) | `app/src/test/java/cl/duoc/fotomotion/` | clic derecho en la carpeta `test` → *Run Tests* |
| Cobertura (Kover) | — | `.\gradlew.bat :app:koverHtmlReportDebug` y abrir `app/build/reports/kover/htmlDebug/index.html` |
| Instrumentadas (emulador) | `app/src/androidTest/java/cl/duoc/fotomotion/` | con el emulador abierto: clic derecho → *Run Tests* |

Cada ViewModel tiene su prueba en la misma ruta dentro de `test/`
(por ejemplo, `ui/tareas/TareasViewModelTest.kt`). La base falsa que usan las pruebas está en `prueba/HuertoDaoEnMemoria.kt`.
