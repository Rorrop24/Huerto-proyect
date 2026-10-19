package cl.duoc.fotomotion.ui.components

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException

@Composable
fun AdjuntarFoto(alRecibirFoto: (String) -> Unit, alInformar: (String) -> Unit, habilitado: Boolean) {
    val contextoPantalla = LocalContext.current
    var destinoCamara by rememberSaveable { mutableStateOf("") }

    val capturaFoto = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { fotoTomada ->
        if (fotoTomada && destinoCamara.isNotBlank()) alRecibirFoto(destinoCamara)
        else alInformar("Se canceló la foto. Puedes intentarlo otra vez.")
    }

    fun abrirCamaraHuerto() {
        try {
            val carpetaFotos = File(contextoPantalla.filesDir, "fotos")
            carpetaFotos.mkdirs()
            val archivoFoto = File.createTempFile("labor_", ".jpg", carpetaFotos)
            val direccionCamara = FileProvider.getUriForFile(contextoPantalla,
                "${contextoPantalla.packageName}.fileprovider", archivoFoto)
            destinoCamara = direccionCamara.toString()
            capturaFoto.launch(direccionCamara)
        } catch (problema: ActivityNotFoundException) {
            alInformar("No hay una cámara disponible. Puedes usar Galería.")
        } catch (problema: SecurityException) {
            alInformar("No se pudo acceder a la cámara. Revisa el permiso en Ajustes.")
        } catch (problema: IOException) {
            alInformar("No se pudo crear la foto. Revisa el espacio disponible.")
        }
    }

    val permisoCamara = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { autorizado ->
        if (autorizado) abrirCamaraHuerto()
        else alInformar("Permiso de cámara rechazado. Puedes usar Galería o habilitar Cámara en Ajustes.")
    }

    // El selector permite leer solo la imagen elegida, sin permiso a toda la galería.
    val seleccionGaleria = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { imagenElegida: Uri? ->
        if (imagenElegida != null) {
            try {
                contextoPantalla.contentResolver.takePersistableUriPermission(
                    imagenElegida, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                alRecibirFoto(imagenElegida.toString())
            } catch (problema: SecurityException) {
                alInformar("No se pudo conservar acceso a esa imagen. Elige otra foto local.")
            }
        } else alInformar("Selección cancelada. La foto anterior se mantiene.")
    }

    Column {
        Button(onClick = {
            if (ContextCompat.checkSelfPermission(contextoPantalla, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                abrirCamaraHuerto()
            } else permisoCamara.launch(Manifest.permission.CAMERA)
        }, enabled = habilitado, modifier = Modifier.fillMaxWidth()) { Text("Tomar foto con cámara") }
        OutlinedButton(onClick = {
            try { seleccionGaleria.launch(arrayOf("image/*")) }
            catch (problema: ActivityNotFoundException) { alInformar("No hay un selector de imágenes disponible.") }
        }, enabled = habilitado, modifier = Modifier.fillMaxWidth()) { Text("Elegir de galería") }
    }
}
