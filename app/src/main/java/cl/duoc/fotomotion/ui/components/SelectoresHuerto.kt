package cl.duoc.fotomotion.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import cl.duoc.fotomotion.domain.model.Sector
import cl.duoc.fotomotion.domain.model.Cultivo
import cl.duoc.fotomotion.domain.model.admiteAsignaciones

// Fila de chips para elegir una opción de una lista (estados, tipos, cultivos, estudiantes).
@Composable
fun <T> ElegirOpcion(titulo: String, opciones: List<T>, seleccionada: T?, etiqueta: (T) -> String,
    alElegir: (T) -> Unit, habilitado: Boolean = true) {
    Text(titulo, style = MaterialTheme.typography.labelLarge)
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(opciones) { opcion ->
            FilterChip(selected = seleccionada == opcion, onClick = { alElegir(opcion) }, enabled = habilitado,
                label = { Text(etiqueta(opcion)) })
        }
    }
}

@Composable
fun ElegirSector(listaOpciones: List<Sector>, codigoSeleccionado: Int?,
    mensajeSeleccion: String, alSeleccionar: (Int) -> Unit, habilitado: Boolean = true) {
    val sectoresAsignables = listaOpciones.filter { sector -> sector.admiteAsignaciones }
    Text("Sector asociado", style = MaterialTheme.typography.labelLarge)
    if (listaOpciones.isEmpty()) Text("Primero crea un sector en la pestaña Sectores.")
    else if (sectoresAsignables.isEmpty()) Text("No hay sectores disponibles: todos están fuera de uso.")
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(sectoresAsignables, key = { sector -> sector.id }) { sector ->
            FilterChip(selected = codigoSeleccionado == sector.id,
                onClick = { alSeleccionar(sector.id) }, enabled = habilitado,
                label = { Text(sector.nombre) })
        }
    }
    ErrorHuerto(mensajeSeleccion, "Error de selección")
}

// Cultivo opcional dentro del sector elegido; "Sin cultivo" deja la relación vacía.
@Composable
fun ElegirCultivo(cultivosDelSector: List<Cultivo>, codigoSeleccionado: Int?, mensajeError: String,
    alSeleccionar: (Int?) -> Unit, habilitado: Boolean = true) {
    ElegirOpcion("Cultivo relacionado (opcional)", listOf<Cultivo?>(null) + cultivosDelSector,
        cultivosDelSector.find { cultivo -> cultivo.id == codigoSeleccionado },
        { cultivo -> cultivo?.nombre ?: "Sin cultivo" }, { cultivo -> alSeleccionar(cultivo?.id) }, habilitado)
    ErrorHuerto(mensajeError, "Error de cultivo")
}
