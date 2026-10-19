package cl.duoc.fotomotion.model

enum class TipoCuenta { DOCENTE, ALUMNO }

data class CuentaHuerto(val codigoCuenta: String, val nombreCuenta: String, val tipoCuenta: TipoCuenta)
