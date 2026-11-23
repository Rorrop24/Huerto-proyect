package cl.duoc.fotomotion.prueba

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

// Los ViewModels lanzan corrutinas en el hilo principal de Android, que no existe en una prueba JUnit.
// Esta regla lo reemplaza por un despachador de prueba que ejecuta todo de inmediato.
// Se cambia apenas se crea la regla (init), porque el ViewModel de cada prueba se crea
// antes de que JUnit llame a starting(); si no, el ViewModel trabajaría en otro hilo.
@OptIn(ExperimentalCoroutinesApi::class)
class ReglaCorrutinas : TestWatcher() {
    init { Dispatchers.setMain(UnconfinedTestDispatcher()) }
    override fun finished(description: Description) { Dispatchers.resetMain() }
}
