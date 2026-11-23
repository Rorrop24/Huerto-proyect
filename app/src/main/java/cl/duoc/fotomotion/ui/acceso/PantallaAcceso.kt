package cl.duoc.fotomotion.ui.acceso

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cl.duoc.fotomotion.ui.components.AvisoHuerto
import cl.duoc.fotomotion.ui.components.CampoHuerto

@Composable
fun PantallaAcceso(viewModel: AccesoViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val habilitado = !uiState.procesando

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Text("Cuidamos nuestro huerto", style = MaterialTheme.typography.headlineSmall)
            Text("Organiza sectores, cultivos y tareas del taller.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = uiState.modo == ModoAcceso.INGRESAR, enabled = habilitado,
                    onClick = { viewModel.cambiarModo(ModoAcceso.INGRESAR) }, label = { Text("Iniciar sesión") })
                FilterChip(selected = uiState.modo == ModoAcceso.REGISTRARSE, enabled = habilitado,
                    onClick = { viewModel.cambiarModo(ModoAcceso.REGISTRARSE) }, label = { Text("Crear cuenta") })
            }
        }
        item { AvisoHuerto(uiState.aviso) }
        item {
            Card(Modifier.fillMaxWidth().animateContentSize()) {
                AnimatedContent(targetState = uiState.modo, transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "modoAcceso") { modo ->
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        if (modo == ModoAcceso.REGISTRARSE) {
                            Text("Las cuentas nuevas son de estudiante.", style = MaterialTheme.typography.bodySmall)
                            CampoHuerto(uiState.textoNombre, "Nombre completo", uiState.errorNombre,
                                viewModel::escribirNombre, habilitado)
                        }
                        CampoHuerto(uiState.textoCorreo, "Correo", uiState.errorCorreo,
                            viewModel::escribirCorreo, habilitado, tipoTeclado = KeyboardType.Email)
                        CampoHuerto(uiState.textoContrasena, "Contraseña", uiState.errorContrasena,
                            viewModel::escribirContrasena, habilitado, esContrasena = true)
                        if (modo == ModoAcceso.REGISTRARSE) {
                            CampoHuerto(uiState.textoConfirmacion, "Repetir contraseña", uiState.errorConfirmacion,
                                viewModel::escribirConfirmacion, habilitado, esContrasena = true)
                        }
                        AnimatedVisibility(visible = uiState.procesando) { LinearProgressIndicator(Modifier.fillMaxWidth()) }
                        Button(onClick = { if (modo == ModoAcceso.INGRESAR) viewModel.ingresar() else viewModel.registrarse() },
                            enabled = habilitado, modifier = Modifier.fillMaxWidth()) {
                            Text(if (modo == ModoAcceso.INGRESAR) "Ingresar" else "Crear cuenta de estudiante")
                        }
                    }
                }
            }
        }
        if (uiState.modo == ModoAcceso.INGRESAR) item {
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Cuentas de demostración", style = MaterialTheme.typography.titleMedium)
                    Text("Datos ficticios para probar la app.", style = MaterialTheme.typography.bodySmall)
                    viewModel.cuentasDemo.forEach { cuenta ->
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(cuenta.nombre, style = MaterialTheme.typography.bodyMedium)
                                Text("${cuenta.correo} · ${cuenta.contrasena}", style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            TextButton(onClick = { viewModel.usarCuentaDemo(cuenta) }, enabled = habilitado) { Text("Usar") }
                        }
                    }
                }
            }
        }
    }
}
