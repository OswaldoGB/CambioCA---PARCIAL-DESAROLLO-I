package com.example.cambioca.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cambioca.data.model.ConversionState
import com.example.cambioca.ui.components.AmountInput
import com.example.cambioca.ui.components.ConversionResult
import com.example.cambioca.ui.components.CurrencySelector
import com.example.cambioca.viewmodel.CurrencyViewModel

/**
 * Pantalla principal de la aplicación CambioCA.
 *
 * Estructura de la pantalla:
 * 1. Header — título y botón de refrescar
 * 2. Card principal — campo de monto USD
 * 3. Card de selección — moneda destino
 * 4. Botón de conversión
 * 5. Resultado de conversión (animado)
 * 6. Footer con info de la API
 *
 * @param viewModel ViewModel provisto por Compose (viewModel())
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyConverterScreen(
    viewModel: CurrencyViewModel = viewModel()
) {
    // ─── Recolectar estados del ViewModel ────────────────────────────────────
    val amountInput by viewModel.amountInput.collectAsState()
    val amountError by viewModel.amountError.collectAsState()
    val selectedCurrency by viewModel.selectedCurrency.collectAsState()
    val conversionState by viewModel.conversionState.collectAsState()

    val keyboardController = LocalSoftwareKeyboardController.current
    val scrollState = rememberScrollState()

    // ─── Layout principal ────────────────────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // ─── HEADER ───────────────────────────────────────────────────
            AppHeader(
                isLoading = conversionState is ConversionState.Loading,
                onRefresh = {
                    keyboardController?.hide()
                    viewModel.refreshRates()
                }
            )

            // ─── CARD: Monto en USD ───────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Indicador de moneda origen (siempre USD)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "🇺🇸", fontSize = 20.sp)
                        Text(
                            text = "Dólar Estadounidense • USD",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Campo de entrada del monto
                    AmountInput(
                        value = amountInput,
                        onValueChange = viewModel::onAmountChanged,
                        errorMessage = amountError,
                        onDone = {
                            keyboardController?.hide()
                            viewModel.convertCurrency()
                        }
                    )
                }
            }

            // ─── Flecha de conversión ─────────────────────────────────────
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.primary,
                    tonalElevation = 4.dp,
                    shadowElevation = 4.dp
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Convertir",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(10.dp).size(24.dp)
                    )
                }
            }

            // ─── CARD: Selección de moneda destino ────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    CurrencySelector(
                        currencies = viewModel.availableCurrencies,
                        selectedCurrency = selectedCurrency,
                        onCurrencySelected = viewModel::onCurrencySelected
                    )
                }
            }

            // ─── BOTÓN DE CONVERSIÓN ──────────────────────────────────────
            Button(
                onClick = {
                    keyboardController?.hide()
                    viewModel.convertCurrency()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                enabled = conversionState !is ConversionState.Loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 8.dp
                )
            ) {
                if (conversionState is ConversionState.Loading) {
                    // Mostrar spinner cuando está cargando
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Convirtiendo...",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Convertir",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            // ─── RESULTADO DE LA CONVERSIÓN ───────────────────────────────
            ConversionResult(
                state = conversionState,
                modifier = Modifier.fillMaxWidth()
            )

            // ─── FOOTER con info de la API ────────────────────────────────
            FooterInfo()

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Encabezado de la aplicación con título, subtítulo y botón de refrescar.
 *
 * @param isLoading Si hay una operación en progreso (deshabilita el botón)
 * @param onRefresh Callback al presionar el botón de actualizar
 */
@Composable
private fun AppHeader(
    isLoading: Boolean,
    onRefresh: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "CambioCA",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Conversor de divisas regional",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }

        // Botón de actualizar tipos de cambio
        IconButton(
            onClick = onRefresh,
            enabled = !isLoading
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Actualizar tipos de cambio",
                tint = if (isLoading)
                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                else
                    MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

/**
 * Pie de página con información sobre la API utilizada.
 */
@Composable
private fun FooterInfo() {
    Text(
        text = "Tipos de cambio provistos por ExchangeRate-API\nexchangerate-api.com • Datos en tiempo real",
        style = MaterialTheme.typography.labelMedium.copy(fontSize = 10.sp),
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}
