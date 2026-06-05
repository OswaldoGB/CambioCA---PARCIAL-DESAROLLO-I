package com.example.cambioca.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cambioca.data.model.CurrencyInfo

/**
 * Componente selector de moneda destino.
 *
 * Muestra las monedas disponibles en una fila horizontal scrollable.
 * La moneda seleccionada se resalta con el color primario.
 *
 * @param currencies Lista de monedas disponibles para seleccionar
 * @param selectedCurrency Moneda actualmente seleccionada
 * @param onCurrencySelected Callback invocado cuando el usuario selecciona una moneda
 */
@Composable
fun CurrencySelector(
    currencies: List<CurrencyInfo>,
    selectedCurrency: CurrencyInfo,
    onCurrencySelected: (CurrencyInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Etiqueta de la sección
        Text(
            text = "Moneda destino",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Fila horizontal con las monedas disponibles
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            items(currencies) { currency ->
                CurrencyChip(
                    currency = currency,
                    isSelected = currency.code == selectedCurrency.code,
                    onSelected = { onCurrencySelected(currency) }
                )
            }
        }
    }
}

/**
 * Chip individual para cada moneda.
 *
 * Muestra la bandera, el código y el nombre del país.
 * Se resalta cuando está seleccionada.
 *
 * @param currency Información de la moneda
 * @param isSelected Si esta moneda está actualmente seleccionada
 * @param onSelected Callback al seleccionar este chip
 */
@Composable
private fun CurrencyChip(
    currency: CurrencyInfo,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    val containerColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surface
    }

    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    }

    Surface(
        onClick = onSelected,
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
        contentColor = contentColor,
        border = BorderStroke(1.5.dp, borderColor),
        tonalElevation = if (isSelected) 4.dp else 1.dp,
        shadowElevation = if (isSelected) 3.dp else 1.dp
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .width(72.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Emoji de bandera del país
            Text(
                text = currency.flag,
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )

            // Código ISO de la moneda (ej: GTQ)
            Text(
                text = currency.code,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                color = contentColor,
                textAlign = TextAlign.Center
            )

            // País de origen
            Text(
                text = currency.country,
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 9.sp),
                color = contentColor.copy(alpha = if (isSelected) 0.9f else 0.6f),
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}
