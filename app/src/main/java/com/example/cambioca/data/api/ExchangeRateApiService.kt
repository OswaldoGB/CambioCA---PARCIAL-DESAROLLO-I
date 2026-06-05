package com.example.cambioca.data.api

import com.example.cambioca.data.model.ExchangeRateResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Interfaz de Retrofit que define los endpoints de la API ExchangeRate-API.
 *
 * API utilizada: ExchangeRate-API (https://www.exchangerate-api.com)
 * - Versión gratuita: hasta 1,500 peticiones/mes
 * - Endpoint base: https://v6.exchangerate-api.com/v6/
 * - No requiere tarjeta de crédito para el plan free
 *
 * Ejemplo de llamada:
 * GET https://v6.exchangerate-api.com/v6/{API_KEY}/latest/USD
 */
interface ExchangeRateApiService {

    /**
     * Obtiene los tipos de cambio más recientes para una moneda base.
     *
     * @param apiKey Clave de API obtenida en exchangerate-api.com
     * @param baseCurrency Moneda base (en este proyecto siempre "USD")
     * @return Response con el objeto ExchangeRateResponse parseado
     */
    @GET("{apiKey}/latest/{baseCurrency}")
    suspend fun getLatestRates(
        @Path("apiKey") apiKey: String,
        @Path("baseCurrency") baseCurrency: String = "USD"
    ): Response<ExchangeRateResponse>
}
