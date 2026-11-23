package cl.duoc.fotomotion

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.text.AnnotatedString
import androidx.test.ext.junit.runners.AndroidJUnit4
import cl.duoc.fotomotion.data.local.BaseDatosHuerto
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
        val daoPrueba = BaseDatosHuerto.abrirBase(pantallaPrueba.activity).huertoDao()
        val sectorPrueba = daoPrueba.observarSectores().first().find { sector -> sector.nombre == nombrePrueba }
        if (sectorPrueba != null) {
            daoPrueba.observarTareas().first().filter { tarea -> tarea.sectorId == sectorPrueba.id }
                .forEach { tarea -> daoPrueba.eliminarTarea(tarea) }
            daoPrueba.observarCultivos().first().filter { cultivo -> cultivo.sectorId == sectorPrueba.id }
                .forEach { cultivo -> daoPrueba.eliminarCultivo(cultivo) }
            daoPrueba.eliminarSector(sectorPrueba)
        }
    }

    private fun esperarTexto(texto: String) {
        pantallaPrueba.waitUntil(5000) { pantallaPrueba.onAllNodesWithText(texto).fetchSemanticsNodes().isNotEmpty() }
    }

    private fun ingresarComo(correo: String, contrasena: String, saludo: String) {
        pantallaPrueba.onNodeWithText("Correo").performTextInput(correo)
        pantallaPrueba.onNodeWithText("Contraseña").performTextInput(contrasena)
        pantallaPrueba.onNodeWithText("Ingresar").performClick()
        esperarTexto(saludo)
    }

    // Comprueba que el campo de texto con esa etiqueta esté vacío.
    private fun campoVacio(etiqueta: String) {
        pantallaPrueba.onNode(hasText(etiqueta) and hasSetTextAction())
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.EditableText, AnnotatedString("")))
    }

    @Test
    fun recorreElFlujoCompletoPorRoles() {
        // Acceso: validaciones e inicio de sesión
        pantallaPrueba.onNodeWithText("Ingresar").performClick()
        pantallaPrueba.onNodeWithText("Escribe tu correo.").assertExists()
        pantallaPrueba.onNodeWithText("Correo").performTextInput("docente@huerto.cl")
        pantallaPrueba.onNodeWithText("Contraseña").performTextInput("Incorrecta1")
        pantallaPrueba.onNodeWithText("Ingresar").performClick()
        esperarTexto("Correo o contraseña incorrectos.")
        pantallaPrueba.onNodeWithText("Contraseña").performTextInput("Docente2026")
        pantallaPrueba.onNodeWithText("Ingresar").performClick()
        esperarTexto("Hola, Profesor Carlos Silva")

        // Sectores: el formulario está cerrado, valida, guarda y queda vacío
        pantallaPrueba.onNodeWithText("Sectores").performClick()
        pantallaPrueba.onNodeWithText("Guardar sector").assertDoesNotExist()
        pantallaPrueba.onNodeWithText("Nuevo sector").performClick()
        pantallaPrueba.onNodeWithText("Guardar sector").performScrollTo().performClick()
        pantallaPrueba.onNodeWithText("Escribe el nombre del sector.").assertExists()
        pantallaPrueba.onNodeWithText("Nombre del sector").performTextInput(nombrePrueba)
        pantallaPrueba.onNodeWithText("Ubicación").performTextInput("Patio de prueba")
        pantallaPrueba.onNodeWithText("Guardar sector").performScrollTo().performClick()
        esperarTexto("Sector guardado.")
        pantallaPrueba.onNodeWithText("Guardar sector").assertDoesNotExist()
        pantallaPrueba.onNodeWithText("Nuevo sector").performClick()
        campoVacio("Nombre del sector")
        pantallaPrueba.onNodeWithText("Cancelar").performClick()

        // Cultivos
        pantallaPrueba.onNodeWithText("Cultivos").performClick()
        pantallaPrueba.onNodeWithText("Nuevo cultivo").performClick()
        pantallaPrueba.onNodeWithText("Riego cada (días)").performScrollTo().performTextReplacement("0")
        pantallaPrueba.onNodeWithText("Guardar cultivo").performScrollTo().performClick()
        pantallaPrueba.onNodeWithText("Escribe el nombre del cultivo.").assertExists()
        pantallaPrueba.onNodeWithText("Usa un número entre 1 y 30.").assertExists()
        pantallaPrueba.onNodeWithText("Riego cada (días)").performScrollTo().performTextReplacement("2")
        pantallaPrueba.onNodeWithText("Nombre del cultivo").performScrollTo().performTextInput("Tomate de prueba")
        pantallaPrueba.onNodeWithText(nombrePrueba).performScrollTo().performClick()
        pantallaPrueba.onNodeWithText("Guardar cultivo").performScrollTo().performClick()
        esperarTexto("Cultivo guardado.")

        // Tareas: hay que asignarlas a un estudiante
        pantallaPrueba.onNodeWithText("Tareas").performClick()
        pantallaPrueba.onNodeWithText("Nueva tarea").performClick()
        pantallaPrueba.onNodeWithText("Guardar tarea").performScrollTo().performClick()
        pantallaPrueba.onNodeWithText("Explica qué hay que hacer.").assertExists()
        pantallaPrueba.onNodeWithText("Asigna la tarea a un estudiante.").assertExists()
        pantallaPrueba.onNodeWithText("Descripción de la tarea").performScrollTo().performTextInput("Regar tomates de prueba")
        pantallaPrueba.onNodeWithText("Fecha (AAAA-MM-DD)").performScrollTo().performTextReplacement("2026-02-30")
        pantallaPrueba.onNodeWithText(nombrePrueba).performScrollTo().performClick()
        pantallaPrueba.onNodeWithText("Estudiante Mateo Rojas").performScrollTo().performClick()
        pantallaPrueba.onNodeWithText("Guardar tarea").performScrollTo().performClick()
        pantallaPrueba.onNodeWithText("Escribe una fecha real: AAAA-MM-DD.").assertExists()
        pantallaPrueba.onNodeWithText("Fecha (AAAA-MM-DD)").performScrollTo().performTextReplacement("2026-10-20")
        pantallaPrueba.onNodeWithText("Guardar tarea").performScrollTo().performClick()
        esperarTexto("Tarea guardada.")
        pantallaPrueba.onNodeWithText("Salir").performClick()

        // Otra estudiante no ve la tarea de Mateo
        ingresarComo("sofia@huerto.cl", "Sofia2026", "Hola, Estudiante Sofía Muñoz")
        pantallaPrueba.onNodeWithText("Tareas").performClick()
        pantallaPrueba.onNodeWithText("Regar tomates de prueba").assertDoesNotExist()
        pantallaPrueba.onNodeWithText("Salir").performClick()

        // Mateo ve su tarea, la entrega vacía y revisa problemas, recordatorios y lecturas
        ingresarComo("mateo@huerto.cl", "Mateo2026", "Hola, Estudiante Mateo Rojas")
        pantallaPrueba.onNodeWithText("Tareas").performClick()
        pantallaPrueba.onNodeWithText("Nueva tarea").assertDoesNotExist()
        pantallaPrueba.onNodeWithText("Regar tomates de prueba").performScrollTo().assertExists()
        pantallaPrueba.onAllNodesWithText("Realizar tarea").onFirst().performScrollTo().performClick()
        pantallaPrueba.onNodeWithText("Enviar evidencia").performScrollTo().performClick()
        pantallaPrueba.onNodeWithText("Adjunta una foto con cámara o galería.").assertExists()
        pantallaPrueba.onNodeWithText("Cuenta qué hiciste en el huerto.").assertExists()
        pantallaPrueba.onNodeWithText("Problemas").performClick()
        pantallaPrueba.onNodeWithText("Reportar problema").performClick()
        pantallaPrueba.onNodeWithText("Enviar reporte").performScrollTo().performClick()
        pantallaPrueba.onNodeWithText("Selecciona el sector afectado.").assertExists()
        pantallaPrueba.onNodeWithText("Elige el tipo de problema.").assertExists()
        pantallaPrueba.onNodeWithContentDescription("Recordatorios").performClick()
        pantallaPrueba.onAllNodesWithText("Tarea atrasada").onFirst().assertExists()
        pantallaPrueba.onNodeWithText("Inicio").performClick()
        pantallaPrueba.onNodeWithText("Lecturas simuladas").performClick()
        pantallaPrueba.onNodeWithText("Humedad baja: revisar el riego.").assertIsDisplayed()
        pantallaPrueba.onNodeWithText("Simular cambio de humedad").performClick()
        pantallaPrueba.onNodeWithText("Humedad baja: revisar el riego.").assertDoesNotExist()
    }
}
