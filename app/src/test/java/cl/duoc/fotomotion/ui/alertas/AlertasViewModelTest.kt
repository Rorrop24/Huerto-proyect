package cl.duoc.fotomotion.ui.alertas

import cl.duoc.fotomotion.domain.model.Tarea
import cl.duoc.fotomotion.domain.model.TipoAlerta
import cl.duoc.fotomotion.prueba.HuertoDaoEnMemoria
import cl.duoc.fotomotion.prueba.Datos
import cl.duoc.fotomotion.prueba.ReglaCorrutinas
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AlertasViewModelTest {
    @get:Rule val reglaCorrutinas = ReglaCorrutinas()
    private val dao = HuertoDaoEnMemoria().apply {
        tareas.value = listOf(Tarea(id = 1, descripcion = "Regar", sectorId = 1,
            fechaProgramada = "2020-01-01", responsableId = "USR-002"))
    }
    private val viewModel = AlertasViewModel(Datos.repositorio(dao))

    @Test
    fun cadaCuentaRecibeSoloSusRecordatorios() = runTest {
        assertTrue(viewModel.uiState.first().alertas.isEmpty())

        viewModel.mostrarPara(Datos.mateo)
        val deMateo = viewModel.uiState.first { estado -> estado.cantidad > 0 }
        assertEquals(TipoAlerta.TAREA_ATRASADA, deMateo.alertas.single().tipo)

        viewModel.mostrarPara(Datos.docente)
        assertEquals(1, viewModel.uiState.first { estado -> estado.cantidad > 0 }.cantidad)

        viewModel.mostrarPara(Datos.sofia)
        assertEquals(0, viewModel.uiState.first { estado -> estado.cantidad == 0 }.cantidad)
    }
}
