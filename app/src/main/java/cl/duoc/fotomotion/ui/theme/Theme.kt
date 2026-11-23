package cl.duoc.fotomotion.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val EsquemaClaro = lightColorScheme(
    primary = VerdeHuerto, onPrimary = Color.White,
    primaryContainer = VerdeHuertoClaro, onPrimaryContainer = VerdeHuertoOscuro,
    secondary = TierraHuerto, onSecondary = Color.White,
    secondaryContainer = TierraHuertoClaro, onSecondaryContainer = TierraHuertoOscuro,
    background = FondoClaro, onBackground = TextoPrincipalClaro,
    surface = SuperficieClara, onSurface = TextoPrincipalClaro,
    surfaceVariant = SuperficieVariableClara, onSurfaceVariant = TextoSecundarioClaro,
    outline = BordeClaro,
    error = ErrorClaro, onError = Color.White,
    errorContainer = ErrorContenedorClaro, onErrorContainer = EnErrorContenedorClaro
)

private val EsquemaOscuro = darkColorScheme(
    primary = VerdeHuertoNoche, onPrimary = VerdeHuertoOscuro,
    primaryContainer = VerdeHuertoNocheContenedor, onPrimaryContainer = VerdeHuertoClaro,
    secondary = TierraHuertoNoche, onSecondary = TierraHuertoOscuro,
    secondaryContainer = TierraHuertoNocheContenedor, onSecondaryContainer = TierraHuertoClaro,
    background = FondoOscuro, onBackground = TextoPrincipalOscuro,
    surface = SuperficieOscura, onSurface = TextoPrincipalOscuro,
    surfaceVariant = SuperficieVariableOscura, onSurfaceVariant = TextoSecundarioOscuro,
    outline = BordeOscuro,
    error = ErrorOscuro, onError = EnErrorContenedorClaro,
    errorContainer = ErrorContenedorOscuro, onErrorContainer = EnErrorContenedorOscuro
)

// Material 3 no trae colores de éxito ni de advertencia, así que se agregan aparte.
@Immutable
data class ColoresEstado(
    val exito: Color,
    val exitoContenedor: Color,
    val advertencia: Color,
    val advertenciaContenedor: Color
)

private val EstadosClaros = ColoresEstado(ExitoClaro, ExitoContenedorClaro, AdvertenciaClaro, AdvertenciaContenedorClaro)
private val EstadosOscuros = ColoresEstado(ExitoOscuro, ExitoContenedorOscuro, AdvertenciaOscuro, AdvertenciaContenedorOscuro)

private val LocalColoresEstado = staticCompositionLocalOf { EstadosClaros }

// Acceso a los colores extra desde cualquier pantalla: HuertoTema.coloresEstado.exito
object HuertoTema {
    val coloresEstado: ColoresEstado
        @Composable @ReadOnlyComposable get() = LocalColoresEstado.current
}

// Tema de la app. Sigue el modo claro u oscuro del teléfono.
@Composable
fun TemaHuerto(modoOscuro: Boolean = isSystemInDarkTheme(), contenido: @Composable () -> Unit) {
    CompositionLocalProvider(LocalColoresEstado provides if (modoOscuro) EstadosOscuros else EstadosClaros) {
        MaterialTheme(
            colorScheme = if (modoOscuro) EsquemaOscuro else EsquemaClaro,
            typography = TipografiaHuerto,
            content = contenido
        )
    }
}
