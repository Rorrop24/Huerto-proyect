package cl.duoc.fotomotion

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import cl.duoc.fotomotion.model.BaseHuerto
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PruebaFormularios {
    @get:Rule val pantallaPrueba = createAndroidComposeRule<ActividadHuerto>()
    private val nombrePrueba = "Sector ${System.currentTimeMillis()}"

    @After
    fun quitarSoloLosRegistrosDeEstaPrueba() = runBlocking {
        val consultasPrueba = BaseHuerto.abrirBase(pantallaPrueba.activity).consultasHuerto()
        val parcelaPrueba = consultasPrueba.observarParcelas().first().find { parcela -> parcela.tituloParcela == nombrePrueba }
        if (parcelaPrueba != null) {
            consultasPrueba.observarLabores().first().filter { labor -> labor.parcelaTrabajo == parcelaPrueba.codigoParcela }
                .forEach { labor -> consultasPrueba.quitarLabor(labor) }
            consultasPrueba.observarSiembras().first().filter { siembra -> siembra.parcelaAsociada == parcelaPrueba.codigoParcela }
                .forEach { siembra -> consultasPrueba.quitarSiembra(siembra) }
            consultasPrueba.quitarParcela(parcelaPrueba)
        }
    }

    @Test
    fun validaCamposGuardaDatosYNavegaEntrePerfiles() {
        pantallaPrueba.onNodeWithText("Ingresar como docente").performClick()
        pantallaPrueba.onNodeWithText("Guardar sector").performScrollTo().performClick()
        pantallaPrueba.onNodeWithText("Escribe el nombre del sector.").assertIsDisplayed()
        pantallaPrueba.onNodeWithText("Escribe dónde está el sector.").assertIsDisplayed()
        pantallaPrueba.onNodeWithContentDescription("Error en Nombre del sector").assertExists()
        pantallaPrueba.onNodeWithText("Nombre del sector").performTextInput(nombrePrueba)
        pantallaPrueba.onNodeWithText("Ubicación").performTextInput("Patio de prueba")
        pantallaPrueba.onNodeWithText("Guardar sector").performScrollTo().performClick()
        pantallaPrueba.waitUntil(5000) {
            pantallaPrueba.onAllNodesWithText("Sector guardado.").fetchSemanticsNodes().isNotEmpty()
        }
        pantallaPrueba.onNodeWithText("Cultivos").performClick()
        pantallaPrueba.onNodeWithText("Guardar cultivo").performClick()
        pantallaPrueba.onNodeWithText("Escribe el nombre del cultivo.").assertIsDisplayed()
        pantallaPrueba.onNodeWithText("Selecciona un sector.").assertIsDisplayed()
        pantallaPrueba.onNodeWithText("Nombre del cultivo").performTextInput("Tomate de prueba")
        pantallaPrueba.onNodeWithText(nombrePrueba).performClick()
        pantallaPrueba.onNodeWithText("Guardar cultivo").performClick()
        pantallaPrueba.waitUntil(5000) {
            pantallaPrueba.onAllNodesWithText("Cultivo guardado.").fetchSemanticsNodes().isNotEmpty()
        }
        pantallaPrueba.onNodeWithText("Tareas").performClick()
        pantallaPrueba.onNodeWithText("Guardar tarea").performScrollTo().performClick()
        pantallaPrueba.onNodeWithText("Explica qué hay que hacer.").assertExists()
        pantallaPrueba.onNodeWithText("Descripción de la tarea").performTextInput("Regar tomates de prueba")
        pantallaPrueba.onNodeWithText("Fecha (AAAA-MM-DD)").performTextReplacement("2026-02-30")
        pantallaPrueba.onNodeWithText(nombrePrueba).performClick()
        pantallaPrueba.onNodeWithText("Guardar tarea").performScrollTo().performClick()
        pantallaPrueba.onNodeWithText("Escribe una fecha real: AAAA-MM-DD.").assertExists()
        pantallaPrueba.onNodeWithText("Fecha (AAAA-MM-DD)").performTextReplacement("2026-10-20")
        pantallaPrueba.onNodeWithText("Guardar tarea").performScrollTo().performClick()
        pantallaPrueba.waitUntil(5000) {
            pantallaPrueba.onAllNodesWithText("Tarea guardada.").fetchSemanticsNodes().isNotEmpty()
        }
        pantallaPrueba.onNodeWithText("Salir").performClick()
        pantallaPrueba.onNodeWithText("Ingresar como estudiante").performClick()
        pantallaPrueba.onNodeWithText("Guardar sector").assertDoesNotExist()
        pantallaPrueba.onNodeWithText("Tareas").performClick()
        pantallaPrueba.onAllNodesWithText("Realizar tarea").onFirst().performScrollTo().performClick()
        pantallaPrueba.onNodeWithText("Enviar evidencia").performScrollTo().performClick()
        pantallaPrueba.onNodeWithText("Adjunta una foto con cámara o galería.").assertExists()
        pantallaPrueba.onNodeWithText("Cuenta qué hiciste en el huerto.").assertExists()
        pantallaPrueba.onNodeWithText("Lecturas").performClick()
        pantallaPrueba.onNodeWithText("Humedad baja: revisar el riego.").assertIsDisplayed()
        pantallaPrueba.onNodeWithText("Simular cambio de humedad").performClick()
        pantallaPrueba.onNodeWithText("Humedad baja: revisar el riego.").assertDoesNotExist()
    }
}
