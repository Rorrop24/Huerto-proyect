package cl.duoc.fotomotion.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun CampoHuerto(valorCampo: String, etiquetaCampo: String, mensajeCampo: String,
    alEscribir: (String) -> Unit, habilitado: Boolean = true,
    esContrasena: Boolean = false, tipoTeclado: KeyboardType = KeyboardType.Text) {
    OutlinedTextField(value = valorCampo, onValueChange = alEscribir, enabled = habilitado,
        label = { Text(etiquetaCampo) }, singleLine = true, isError = mensajeCampo.isNotEmpty(),
        supportingText = { if (mensajeCampo.isNotEmpty()) Text(mensajeCampo) },
        trailingIcon = {
            if (mensajeCampo.isNotEmpty()) Icon(Icons.Default.Warning, contentDescription = "Error en $etiquetaCampo")
        },
        visualTransformation = if (esContrasena) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = if (esContrasena) KeyboardType.Password else tipoTeclado),
        modifier = Modifier.fillMaxWidth())
}
