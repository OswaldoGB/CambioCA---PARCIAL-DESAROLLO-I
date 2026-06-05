package com.example.cambioca.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cambioca.data.model.AVAILABLE_CURRENCIES
import com.example.cambioca.data.model.ConversionState
import com.example.cambioca.data.model.CurrencyInfo
import com.example.cambioca.data.model.ExchangeRateResponse
import com.example.cambioca.data.repository.ExchangeRateRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * ViewModel para la pantalla principal del conversor de divisas.
 *
 * Responsabilidades:
 * - Mantener el estado de la UI (monto, moneda seleccionada, resultado)
 * - Coordinar la llamada al repositorio
 * - Realizar el cálculo de conversión con los datos obtenidos
 * - Exponer estados observables mediante StateFlow
 *
 * El ViewModel sobrevive a los cambios de configuración (rotación de pantalla).
 */
class CurrencyViewModel : ViewModel() {

    /** Repositorio para obtener los tipos de cambio */
    private val repository = ExchangeRateRepository()

    // ─────────────────────────────────────────────────────────────────────────
    // Estado del monto ingresado por el usuario
    // ─────────────────────────────────────────────────────────────────────────

    /** Texto del campo de entrada (monto en USD) */
    private val _amountInput = MutableStateFlow("")
    val amountInput: StateFlow<String> = _amountInput.asStateFlow()

    /** Mensaje de error del campo de monto (null = sin error) */
    private val _amountError = MutableStateFlow<String?>(null)
    val amountError: StateFlow<String?> = _amountError.asStateFlow()

    // ─────────────────────────────────────────────────────────────────────────
    // Estado de la moneda seleccionada
    // ─────────────────────────────────────────────────────────────────────────

    /** Lista de monedas disponibles (inmutable, definida en el modelo) */
    val availableCurrencies = AVAILABLE_CURRENCIES

    /** Moneda destino actualmente seleccionada (por defecto: Quetzal GTQ) */
    private val _selectedCurrency = MutableStateFlow(AVAILABLE_CURRENCIES[0])
    val selectedCurrency: StateFlow<CurrencyInfo> = _selectedCurrency.asStateFlow()

    // ─────────────────────────────────────────────────────────────────────────
    // Estado de la conversión (Idle / Loading / Success / Error)
    // ─────────────────────────────────────────────────────────────────────────

    private val _conversionState = MutableStateFlow<ConversionState>(ConversionState.Idle)
    val conversionState: StateFlow<ConversionState> = _conversionState.asStateFlow()

    // ─────────────────────────────────────────────────────────────────────────
    // Caché de los tipos de cambio para evitar peticiones repetidas
    // ─────────────────────────────────────────────────────────────────────────

    /** Última respuesta exitosa de la API (null si aún no se ha cargado) */
    private var cachedRates: ExchangeRateResponse? = null

    // ─────────────────────────────────────────────────────────────────────────
    // Métodos públicos llamados desde la UI
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Actualiza el monto ingresado por el usuario.
     * También limpia el error del campo y resetea el estado de conversión.
     *
     * @param input Texto ingresado en el campo de monto
     */
    fun onAmountChanged(input: String) {
        // Solo permitir números y un punto decimal
        val filtered = input.filter { it.isDigit() || it == '.' }
        _amountInput.value = filtered
        _amountError.value = null  // Limpiar error al escribir

        // Si se borra el campo, volver a estado inicial
        if (filtered.isEmpty()) {
            _conversionState.value = ConversionState.Idle
        }
    }

    /**
     * Actualiza la moneda destino seleccionada.
     * Si ya hay una conversión exitosa, recalcula con la nueva moneda.
     *
     * @param currency La moneda seleccionada por el usuario
     */
    fun onCurrencySelected(currency: CurrencyInfo) {
        _selectedCurrency.value = currency

        // Si ya tenemos datos en caché, recalcular sin nueva petición a la API
        cachedRates?.let { rates ->
            performConversion(rates)
        }
    }

    /**
     * Inicia el proceso de conversión de moneda.
     * 1. Valida el monto ingresado
     * 2. Hace la llamada a la API (o usa caché)
     * 3. Realiza el cálculo de conversión
     * 4. Actualiza el estado de la UI
     */
    fun convertCurrency() {
        // Paso 1: Validar el monto
        if (!validateAmount()) return

        // Paso 2: Lanzar coroutine en el scope del ViewModel
        viewModelScope.launch {
            _conversionState.value = ConversionState.Loading

            // Paso 3: Obtener tipos de cambio (API o caché)
            val result = repository.getExchangeRates()

            result.fold(
                onSuccess = { response ->
                    cachedRates = response  // Guardar en caché
                    performConversion(response)
                },
                onFailure = { error ->
                    _conversionState.value = ConversionState.Error(
                        message = error.message ?: "Error al obtener tipos de cambio"
                    )
                }
            )
        }
    }

    /**
     * Fuerza una nueva petición a la API (descarta el caché).
     * Útil para el botón de "actualizar".
     */
    fun refreshRates() {
        cachedRates = null
        if (_amountInput.value.isNotEmpty()) {
            convertCurrency()
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Métodos privados auxiliares
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Valida que el campo de monto sea válido.
     *
     * @return true si el monto es válido, false si hay errores
     */
    private fun validateAmount(): Boolean {
        val input = _amountInput.value.trim()

        return when {
            // Campo vacío
            input.isEmpty() -> {
                _amountError.value = "Ingresa un monto en dólares"
                false
            }
            // No es un número válido
            input.toDoubleOrNull() == null -> {
                _amountError.value = "Ingresa un número válido"
                false
            }
            // Monto negativo
            input.toDouble() < 0 -> {
                _amountError.value = "El monto debe ser mayor o igual a cero"
                false
            }
            // Monto igual a cero (advertencia, pero se permite convertir)
            input.toDouble() == 0.0 -> {
                _amountError.value = null
                true
            }
            // Válido
            else -> {
                _amountError.value = null
                true
            }
        }
    }

    /**
     * Realiza el cálculo de conversión con los datos de la API.
     *
     * @param response Respuesta de la API con los tipos de cambio
     */
    private fun performConversion(response: ExchangeRateResponse) {
        val amount = _amountInput.value.toDoubleOrNull() ?: 0.0
        val targetCode = _selectedCurrency.value.code

        // Obtener el tipo de cambio para la moneda seleccionada
        val rate = response.conversionRates[targetCode]

        if (rate != null) {
            val convertedAmount = amount * rate

            // Formatear la fecha de última actualización
            val lastUpdate = response.lastUpdateUtc?.let {
                formatLastUpdate(it)
            } ?: "Fecha no disponible"

            _conversionState.value = ConversionState.Success(
                result = convertedAmount,
                rate = rate,
                targetCurrency = targetCode,
                lastUpdate = lastUpdate
            )
        } else {
            _conversionState.value = ConversionState.Error(
                message = "Tipo de cambio no disponible para $targetCode"
            )
        }
    }

    /**
     * Formatea la cadena de fecha UTC de la API a un formato legible en español.
     *
     * @param utcString Cadena de fecha UTC de la API, ej: "Thu, 05 Jun 2025 00:00:01 +0000"
     * @return Cadena formateada, ej: "05 jun 2025"
     */
    private fun formatLastUpdate(utcString: String): String {
        return try {
            val inputFormat = SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss Z",
                Locale.ENGLISH
            )
            val outputFormat = SimpleDateFormat(
                "dd MMM yyyy, HH:mm",
                Locale("es", "ES")
            )
            val date = inputFormat.parse(utcString)
            date?.let { "Actualizado: ${outputFormat.format(it)}" } ?: utcString
        } catch (e: Exception) {
            "Actualizado recientemente"
        }
    }
}
