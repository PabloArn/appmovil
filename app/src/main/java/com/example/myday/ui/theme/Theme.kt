package com.example.myday.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext


private val MyDayLightColorScheme = lightColorScheme(
    primary = MyDayRojo,      // color café oscuro para el botón flotante (+)
    onPrimary = Color.White,       // Texto blanco sobre el color principal
    background = MyDayCremaClaro,  // color crema muy claro para el fondo de la app
    onBackground = MyDayOlivaOscuro,// color verde oliva oscuro para el texto principal
    surface = MyDayBeigeClaro,     // color beige claro para las tarjetas
    onSurface = MyDayOlivaOscuro,  // Texto verde oliva oscuro sobre las tarjetas
    primaryContainer = MyDayOlivaOscuro, // Usaremos el verde oliva oscuro para la barra superior
    onPrimaryContainer = Color.White // Texto blanco sobre la barra superior
)

@Composable
fun MyDayTheme(
    content: @Composable () -> Unit
) {
    // Usamos el color scheme que acabamos de definir
    MaterialTheme(
        colorScheme = MyDayLightColorScheme,
        content = content
    )
}