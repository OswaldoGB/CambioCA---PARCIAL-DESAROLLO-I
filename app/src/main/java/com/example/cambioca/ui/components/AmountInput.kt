package com.example.cambioca.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Componente de entrada del monto en dólares USD.
 *
 * Características:
 * - Teclado numérico con punto decimal
 * - Botón para limpiar el campo
 * - Muestra mensajes de error de validación
 * - Acción "Done" en el teclado para iniciar conversión
 *
 * @param value Valor actual del campo
 * @param onValueChange Callback al cambiar el texto
 * @param errorMessage Mensaje de error a mostrar (null = sin error)
 * @param onDone Callback al presionar "Done" en el teclado
 * @param modifier Modificador de Compose para personalizar layout
 */
@Composable
fun AmountInput(
    value: String,
    onValueChange: (String) -> Unit,
    errorMessage: String?,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Controlador del teclado virtual para ocultarlo manualmente
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(modifier = modifier) {
        // Etiqueta del campo
        Text(
            text = "Monto en dólares (USD)",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),

            // Placeholder cuando el campo está vacío
            placeholder = {
                Text(
                    text = "0.00",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    fontSize = 20.sp
                )
            },

            // Ícono del símbolo dólar al inicio
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.AttachMoney,
                    contentDescription = "Dólares USD",
                    tint = if (errorMessage != null)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.primary
                )
            },

            // Botón para limpiar el campo (solo si hay texto)
            trailingIcon = {
                if (value.isNotEmpty()) {
                    IconButton(onClick = { onValueChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Limpiar campo",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            },

            // Mensaje de error de validación
            isError = errorMessage != null,
            supportingText = {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            },

            // Configuración del teclado: numérico con punto decimal
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            ),

            // Al presionar "Done", ocultar teclado e iniciar conversión
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    onDone()
                }
            ),

            // Estilo del texto ingresado
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            ),

            // Una sola línea para input de monto
            singleLine = true,

            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                errorBorderColor = MaterialTheme.colorScheme.error,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )
    }
}
