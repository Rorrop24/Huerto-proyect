package cl.duoc.fotomotion.domain.model

enum class TipoCuenta { DOCENTE, ALUMNO }

data class CuentaHuerto(val codigoCuenta: String, val nombreCuenta: String, val tipoCuenta: TipoCuenta)
