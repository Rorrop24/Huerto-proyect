package cl.duoc.fotomotion.ui.lecturas

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LecturasViewModelTest {
    @Test
    fun simularRiegoAlternaLaHumedadDelInvernadero() {
        val viewModel = LecturasViewModel()
        assertTrue(viewModel.uiState.value.lecturas.first().humedadBaja)

        viewModel.simularRiego()
        assertEquals(60, viewModel.uiState.value.lecturas.first().humedadSuelo)
        assertFalse(viewModel.uiState.value.lecturas.first().humedadBaja)

        viewModel.simularRiego()
        assertEquals(22, viewModel.uiState.value.lecturas.first().humedadSuelo)
        assertEquals(2, viewModel.uiState.value.cantidadSimulaciones)
        // El patio no cambia.
        assertEquals(65, viewModel.uiState.value.lecturas[1].humedadSuelo)
    }
}
