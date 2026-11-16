package cl.duoc.fotomotion.repository

import cl.duoc.fotomotion.model.*

class RepositorioHuerto(private val consultasLocales: ConsultasHuerto) {
    fun leerParcelas() = consultasLocales.observarParcelas()
    suspend fun registrarParcela(datoParcela: ParcelaHuerto) = consultasLocales.agregarParcela(datoParcela)
    suspend fun corregirParcela(datoParcela: ParcelaHuerto) = consultasLocales.modificarParcela(datoParcela)
    suspend fun borrarParcela(datoParcela: ParcelaHuerto): Boolean {
        if (consultasLocales.contarSiembrasDeParcela(datoParcela.codigoParcela) > 0 ||
            consultasLocales.contarLaboresDeParcela(datoParcela.codigoParcela) > 0) return false
        consultasLocales.quitarParcela(datoParcela)
        return true
    }

    fun leerSiembras() = consultasLocales.observarSiembras()
    suspend fun registrarSiembra(datoSiembra: SiembraHuerto) = consultasLocales.agregarSiembra(datoSiembra)
    suspend fun corregirSiembra(datoSiembra: SiembraHuerto) = consultasLocales.modificarSiembra(datoSiembra)
    suspend fun borrarSiembra(datoSiembra: SiembraHuerto): Boolean {
        if (consultasLocales.contarLaboresDeSiembra(datoSiembra.codigoSiembra) > 0) return false
        consultasLocales.quitarSiembra(datoSiembra)
        return true
    }

    fun leerLabores() = consultasLocales.observarLabores()
    suspend fun registrarLabor(datoLabor: LaborHuerto) = consultasLocales.agregarLabor(datoLabor)
    suspend fun corregirLabor(datoLabor: LaborHuerto) = consultasLocales.modificarLabor(datoLabor)
    suspend fun borrarLabor(datoLabor: LaborHuerto) = consultasLocales.quitarLabor(datoLabor)
}
