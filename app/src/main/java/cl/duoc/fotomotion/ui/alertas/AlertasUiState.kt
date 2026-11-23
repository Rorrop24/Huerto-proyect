package cl.duoc.fotomotion.ui.alertas

import cl.duoc.fotomotion.domain.model.Alerta

data class AlertasUiState(val alertas: List<Alerta> = emptyList()) {
    val cantidad: Int get() = alertas.size
}
