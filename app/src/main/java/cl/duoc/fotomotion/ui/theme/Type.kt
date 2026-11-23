package cl.duoc.fotomotion.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Tipografía: se parte de la de Material 3 y se refuerzan los títulos.
private val base = Typography()

val TipografiaHuerto = Typography(
    headlineSmall = base.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
    titleLarge = base.titleLarge.copy(fontWeight = FontWeight.SemiBold),
    titleMedium = base.titleMedium.copy(fontWeight = FontWeight.SemiBold),
    labelLarge = base.labelLarge.copy(fontWeight = FontWeight.Medium),
    bodyMedium = base.bodyMedium.copy(lineHeight = 20.sp)
)
