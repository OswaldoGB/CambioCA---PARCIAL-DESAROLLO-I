package com.example.cambioca

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.cambioca.ui.screens.CurrencyConverterScreen
import com.example.cambioca.ui.theme.CambioCATheme

/**
 * Actividad principal y único punto de entrada de la aplicación CambioCA.
 *
 * Configura:
 * - Edge-to-edge display para aprovechar toda la pantalla
 * - El tema Material3 personalizado (CambioCATheme)
 * - La pantalla principal (CurrencyConverterScreen)
 *
 * La arquitectura utilizada es MVVM:
 * - Model: ExchangeRateResponse, CurrencyInfo (data/model)
 * - View: Composables en ui/screens y ui/components
 * - ViewModel: CurrencyViewModel (gestiona estado y lógica de negocio)
 * - Repository: ExchangeRateRepository (acceso a datos de la API)
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Habilitar diseño edge-to-edge (pantalla completa debajo del status bar)
        enableEdgeToEdge()

        // Configurar la UI con Jetpack Compose
        setContent {
            CambioCATheme {
                // Surface provee el color de fondo del tema
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding()
                ) {
                    // Pantalla principal del conversor
                    CurrencyConverterScreen()
                }
            }
        }
    }
}
