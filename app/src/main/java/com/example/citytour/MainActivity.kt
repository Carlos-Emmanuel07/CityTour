package com.example.citytour

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.citytour.ui.theme.CityTourTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CityTourTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current

    // Lista de imágenes para el carrusel principal
    val imagenesMonumentos = listOf(
        R.drawable.monumento_1,
        R.drawable.monumento_2,
        R.drawable.monumento_3
    )
    var indiceActual by remember { mutableStateOf(0) }

    // Variables de estado para el reproductor de audio
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var estaReproduciendo by remember { mutableStateOf(false) }

    // Liberar memoria al cerrar la actividad
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Título de la aplicación
        Text(
            text = "Guía Turística: City Tour",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        // Carrusel de Imágenes
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = imagenesMonumentos[indiceActual]),
                    contentDescription = "Monumento histórico",
                    modifier = Modifier.fillMaxSize()
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = {
                        if (indiceActual > 0) indiceActual--
                        else indiceActual = imagenesMonumentos.size - 1
                    }) {
                        Text("Anterior")
                    }
                    Button(onClick = {
                        if (indiceActual < imagenesMonumentos.size - 1) indiceActual++
                        else indiceActual = 0
                    }) {
                        Text("Siguiente")
                    }
                }
            }
        }

        // Panel de Controles de Audio
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (estaReproduciendo) "Reproduciendo audio..." else "Audio detenido / pausado",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        if (mediaPlayer == null) {
                            mediaPlayer = MediaPlayer.create(context, R.raw.audio_guia)?.apply {
                                setOnCompletionListener {
                                    estaReproduciendo = false
                                    seekTo(0)
                                }
                            }
                        }
                        mediaPlayer?.start()
                        estaReproduciendo = true
                    }
                ) {
                    Text("Play")
                }

                Button(
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                    onClick = {
                        mediaPlayer?.let {
                            if (it.isPlaying) {
                                it.pause()
                                estaReproduciendo = false
                            }
                        }
                    }
                ) {
                    Text("Pausa")
                }

                Button(
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    onClick = {
                        mediaPlayer?.let {
                            it.stop()
                            it.release()
                            mediaPlayer = null
                            estaReproduciendo = false
                        }
                    }
                ) {
                    Text("Stop")
                }
            }
        }
    }
}