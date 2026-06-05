package com.example.cambioca.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ─────────────────────────────────────────────────────────────────────────────
// Paleta de colores personalizada para CambioCA
// Inspirada en los colores de Centroamérica: verdes, azules y dorados
// ─────────────────────────────────────────────────────────────────────────────

// Colores primarios — Azul centroamericano
val Primary = Color(0xFF1565C0)
val PrimaryVariant = Color(0xFF003C8F)
val OnPrimary = Color(0xFFFFFFFF)

// Colores secundarios — Verde esmeralda (selva centroamericana)
val Secondary = Color(0xFF2E7D32)
val SecondaryVariant = Color(0xFF005005)
val OnSecondary = Color(0xFFFFFFFF)

// Colores terciarios — Dorado (referencia al quetzal guatemalteco)
val Tertiary = Color(0xFFF57F17)
val OnTertiary = Color(0xFF000000)

// Fondos y superficies
val Background = Color(0xFFF8F9FA)
val Surface = Color(0xFFFFFFFF)
val SurfaceVariant = Color(0xFFE3F2FD)
val OnBackground = Color(0xFF1A1A2E)
val OnSurface = Color(0xFF1A1A2E)

// Estados
val Error = Color(0xFFB71C1C)
val ErrorContainer = Color(0xFFFFEBEE)
val OnError = Color(0xFFFFFFFF)
val Success = Color(0xFF1B5E20)
val SuccessContainer = Color(0xFFE8F5E9)

// Esquema de colores claro
private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = Color(0xFFBBDEFB),
    onPrimaryContainer = Color(0xFF003C8F),
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = Color(0xFFC8E6C9),
    onSecondaryContainer = Color(0xFF1B5E20),
    tertiary = Tertiary,
    onTertiary = OnTertiary,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    error = Error,
    onError = OnError,
    errorContainer = ErrorContainer,
    onErrorContainer = Color(0xFF7F0000)
)

/**
 * Tema principal de la aplicación CambioCA.
 * Usa Material3 con colores personalizados inspirados en Centroamérica.
 *
 * @param content El contenido composable hijo que hereda este tema
 */
@Composable
fun CambioCATheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
