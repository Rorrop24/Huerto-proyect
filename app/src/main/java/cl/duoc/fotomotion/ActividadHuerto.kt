package cl.duoc.fotomotion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cl.duoc.fotomotion.ui.navigation.RecorridoHuerto
import cl.duoc.fotomotion.ui.theme.TemaHuerto

class ActividadHuerto : ComponentActivity() {
    // onCreate mantiene su nombre porque Android lo exige.
    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun onCreate(estadoGuardado: Bundle?) {
        super.onCreate(estadoGuardado)
        enableEdgeToEdge()
        setContent {
            TemaHuerto {
                RecorridoHuerto()
            }
        }
    }
}
