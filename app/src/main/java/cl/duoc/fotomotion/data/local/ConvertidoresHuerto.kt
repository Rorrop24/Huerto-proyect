package cl.duoc.fotomotion.data.local

import androidx.room.TypeConverter
import cl.duoc.fotomotion.domain.model.*

// Room guarda cada enum como texto (por ejemplo "PENDIENTE").
// Al leer usa "desde", que tolera valores antiguos o vacíos en vez de cerrar la app.
class ConvertidoresHuerto {
    @TypeConverter fun tipoCuentaATexto(valor: TipoCuenta): String = valor.name
    @TypeConverter fun textoATipoCuenta(valor: String): TipoCuenta = TipoCuenta.desde(valor)

    @TypeConverter fun estadoSectorATexto(valor: EstadoSector): String = valor.name
    @TypeConverter fun textoAEstadoSector(valor: String): EstadoSector = EstadoSector.desde(valor)

    @TypeConverter fun estadoCultivoATexto(valor: EstadoCultivo): String = valor.name
    @TypeConverter fun textoAEstadoCultivo(valor: String): EstadoCultivo = EstadoCultivo.desde(valor)

    @TypeConverter fun tipoTareaATexto(valor: TipoTarea): String = valor.name
    @TypeConverter fun textoATipoTarea(valor: String): TipoTarea = TipoTarea.desde(valor)

    @TypeConverter fun estadoTareaATexto(valor: EstadoTarea): String = valor.name
    @TypeConverter fun textoAEstadoTarea(valor: String): EstadoTarea = EstadoTarea.desde(valor)

    @TypeConverter fun tipoProblemaATexto(valor: TipoProblema): String = valor.name
    @TypeConverter fun textoATipoProblema(valor: String): TipoProblema = TipoProblema.desde(valor)

    @TypeConverter fun estadoProblemaATexto(valor: EstadoProblema): String = valor.name
    @TypeConverter fun textoAEstadoProblema(valor: String): EstadoProblema = EstadoProblema.desde(valor)
}
