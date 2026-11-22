package cl.duoc.fotomotion.ui.acceso

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.fotomotion.domain.model.CuentaHuerto
import cl.duoc.fotomotion.domain.model.TipoCuenta

@Composable
fun PantallaAcceso(controlAcceso: AccesoViewModel, alIngresar: (CuentaHuerto) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Text("Cuidamos nuestro huerto", style = MaterialTheme.typography.headlineSmall)
            Text("Organiza sectores, cultivos y tareas del taller.")
            Text("Elige un perfil de demostración. No se usan contraseñas ni cuentas reales.")
        }
        items(controlAcceso.cuentasEjemplo) { cuenta ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(cuenta.nombreCuenta, style = MaterialTheme.typography.titleMedium)
                    Text(if (cuenta.tipoCuenta == TipoCuenta.DOCENTE) "Administra y revisa el trabajo." else "Consulta y entrega evidencia con una foto.")
                    Button(onClick = { alIngresar(cuenta) }) {
                        Text(if (cuenta.tipoCuenta == TipoCuenta.DOCENTE) "Ingresar como docente" else "Ingresar como estudiante")
                    }
                }
            }
        }
    }
}
