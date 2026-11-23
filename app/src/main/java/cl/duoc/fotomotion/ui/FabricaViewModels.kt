package cl.duoc.fotomotion.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.duoc.fotomotion.data.repository.RepositorioCuentas
import cl.duoc.fotomotion.data.repository.RepositorioHuerto
import cl.duoc.fotomotion.ui.acceso.AccesoViewModel
import cl.duoc.fotomotion.ui.alertas.AlertasViewModel
import cl.duoc.fotomotion.ui.tareas.TareasViewModel
import cl.duoc.fotomotion.ui.lecturas.LecturasViewModel
import cl.duoc.fotomotion.ui.sectores.SectoresViewModel
import cl.duoc.fotomotion.ui.problemas.ProblemasViewModel
import cl.duoc.fotomotion.ui.cultivos.CultivosViewModel

// Único lugar donde se crean los ViewModels con el repositorio real (Room).
// Así los ViewModels no dependen de Android y se pueden probar con JUnit.
object FabricaViewModels {
    val fabrica: ViewModelProvider.Factory = viewModelFactory {
        initializer { AccesoViewModel(repositorioCuentas()) }
        initializer { SectoresViewModel(repositorio()) }
        initializer { CultivosViewModel(repositorio()) }
        initializer { TareasViewModel(repositorio(), repositorioCuentas()) }
        initializer { ProblemasViewModel(repositorio()) }
        initializer { AlertasViewModel(repositorio()) }
        initializer { LecturasViewModel() }
    }

    private fun CreationExtras.repositorio() = RepositorioHuerto.desdeContexto(aplicacion())
    private fun CreationExtras.repositorioCuentas() = RepositorioCuentas.desdeContexto(aplicacion())
    private fun CreationExtras.aplicacion() = checkNotNull(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
}
