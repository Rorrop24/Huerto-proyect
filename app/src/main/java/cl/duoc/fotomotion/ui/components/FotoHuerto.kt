package cl.duoc.fotomotion.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import cl.duoc.fotomotion.R

@Composable
fun FotoHuerto(direccionImagen: String) {
    AsyncImage(model = direccionImagen, contentDescription = "Fotografía del trabajo en el huerto",
        error = painterResource(R.drawable.ic_huerto), contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(16.dp)))
}
