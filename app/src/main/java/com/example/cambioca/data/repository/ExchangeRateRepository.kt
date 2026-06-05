package com.example.cambioca.data.repository

import com.example.cambioca.data.api.RetrofitClient
import com.example.cambioca.data.model.ExchangeRateResponse
import java.io.IOException

/**
 * Repositorio que actúa como fuente única de verdad para los datos de tipo de cambio.
 *
 * Patrón Repository: abstrae el origen de los datos (API, caché, BD local)
 * del resto de la aplicación. El ViewModel solo interactúa con este repositorio.
 *
 * IMPORTANTE: Reemplaza "YOUR_API_KEY_HERE" con tu clave de ExchangeRate-API.
 * Obtén tu clave gratuita en: https://www.exchangerate-api.com
 */
class ExchangeRateRepository {

    /**
     * Clave de API de ExchangeRate-API.
     *
     * ⚠️ IMPORTANTE PARA EL ESTUDIANTE:
     * 1. Ve a https://www.exchangerate-api.com
     * 2. Crea una cuenta gratuita (no requiere tarjeta)
     * 3. Copia tu API Key y reemplázala aquí
     * 4. Plan gratuito: 1,500 peticiones/mes
     *
     * En producción, esta clave debería almacenarse en local.properties
     * o en BuildConfig para no exponerla en el código fuente.
     */
    private val apiKey = "YOUR_API_KEY_HERE"

    /**
     * Obtiene los tipos de cambio actualizados desde la API.
     *
     * Utiliza coroutines para ejecutarse de forma asíncrona.
     * Retorna un Result<ExchangeRateResponse> que puede ser:
     * - Result.success(data): cuando la llamada fue exitosa
     * - Result.failure(exception): cuando ocurrió algún error
     *
     * @return Result con los datos de tipo de cambio o el error ocurrido
     */
    suspend fun getExchangeRates(): Result<ExchangeRateResponse> {
        return try {
            // Realizar la llamada a la API de forma suspendida (no bloquea el hilo)
            val response = RetrofitClient.apiService.getLatestRates(
                apiKey = apiKey,
                baseCurrency = "USD"
            )

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.result == "success") {
                    // Respuesta exitosa con datos válidos
                    Result.success(body)
                } else {
                    // La API retornó un resultado de error en el cuerpo
                    val errorMsg = body?.errorType ?: "Error desconocido de la API"
                    Result.failure(Exception("Error de API: $errorMsg"))
                }
            } else {
                // Error HTTP (4xx, 5xx)
                val errorMsg = when (response.code()) {
                    401 -> "API Key inválida o no autorizada"
                    403 -> "Acceso denegado. Verifica tu API Key"
                    404 -> "Endpoint no encontrado"
                    429 -> "Límite de peticiones alcanzado. Intenta más tarde"
                    500, 503 -> "Error del servidor. Intenta más tarde"
                    else -> "Error HTTP ${response.code()}"
                }
                Result.failure(Exception(errorMsg))
            }

        } catch (e: IOException) {
            // Error de red: sin conexión, timeout, DNS, etc.
            Result.failure(Exception("Sin conexión a internet. Verifica tu red."))
        } catch (e: Exception) {
            // Cualquier otro error inesperado
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }
}
