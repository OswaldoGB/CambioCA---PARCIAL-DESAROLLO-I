package com.example.cambioca.data.model

import com.google.gson.annotations.SerializedName

/**
 * Modelo principal que mapea la respuesta JSON de la API ExchangeRate-API.
 * Endpoint: https://v6.exchangerate-api.com/v6/{API_KEY}/latest/USD
 *
 * Ejemplo de respuesta:
 * {
 *   "result": "success",
 *   "base_code": "USD",
 *   "conversion_rates": { "GTQ": 7.78, "HNL": 24.87, ... }
 * }
 */
data class ExchangeRateResponse(
    @SerializedName("result")
    val result: String,              // "success" o "error"

    @SerializedName("base_code")
    val baseCode: String,            // Moneda base, ej: "USD"

    @SerializedName("conversion_rates")
    val conversionRates: Map<String, Double>,  // Mapa con todos los tipos de cambio

    @SerializedName("error-type")
    val errorType: String? = null,   // Tipo de error si aplica

    @SerializedName("time_last_update_utc")
    val lastUpdateUtc: String? = null  // Fecha/hora de última actualización
)

/**
 * Clase sellada que representa el estado de la conversión en la UI.
 * Permite manejar los tres estados posibles: cargando, éxito y error.
 */
sealed class ConversionState {
    /** Estado inicial o mientras se carga */
    object Idle : ConversionState()

    /** Indica que hay una operación en progreso */
    object Loading : ConversionState()

    /**
     * Conversión exitosa.
     * @param result Monto convertido
     * @param rate Tipo de cambio utilizado
     * @param targetCurrency Moneda destino (ej: "GTQ")
     * @param lastUpdate Texto de última actualización
     */
    data class Success(
        val result: Double,
        val rate: Double,
        val targetCurrency: String,
        val lastUpdate: String
    ) : ConversionState()

    /**
     * Error durante la conversión.
     * @param message Mensaje descriptivo del error
     */
    data class Error(val message: String) : ConversionState()
}

/**
 * Datos de cada moneda de la región Centroamérica / México.
 * @param code Código ISO 4217 de la moneda
 * @param name Nombre completo de la moneda
 * @param country País de origen
 * @param flag Emoji de la bandera del país
 * @param symbol Símbolo de la moneda
 */
data class CurrencyInfo(
    val code: String,
    val name: String,
    val country: String,
    val flag: String,
    val symbol: String
)

/**
 * Lista de monedas disponibles para convertir desde USD.
 * Solo monedas de la región Centroamérica y México como se solicita.
 */
val AVAILABLE_CURRENCIES = listOf(
    CurrencyInfo(
        code = "GTQ",
        name = "Quetzal guatemalteco",
        country = "Guatemala",
        flag = "🇬🇹",
        symbol = "Q"
    ),
    CurrencyInfo(
        code = "HNL",
        name = "Lempira hondureño",
        country = "Honduras",
        flag = "🇭🇳",
        symbol = "L"
    ),
    CurrencyInfo(
        code = "NIO",
        name = "Córdoba nicaragüense",
        country = "Nicaragua",
        flag = "🇳🇮",
        symbol = "C\$"
    ),
    CurrencyInfo(
        code = "MXN",
        name = "Peso mexicano",
        country = "México",
        flag = "🇲🇽",
        symbol = "\$"
    )
)
