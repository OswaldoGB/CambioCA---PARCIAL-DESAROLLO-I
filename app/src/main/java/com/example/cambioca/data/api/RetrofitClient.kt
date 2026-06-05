package com.example.cambioca.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Objeto singleton que proporciona la instancia configurada de Retrofit.
 *
 * Configuración incluida:
 * - URL base de ExchangeRate-API v6
 * - Interceptor de logging para depuración (solo en debug)
 * - Timeouts configurados para conexiones lentas
 * - Convertidor Gson para parsear JSON automáticamente
 */
object RetrofitClient {

    /** URL base de la API de tipos de cambio */
    private const val BASE_URL = "https://v6.exchangerate-api.com/v6/"

    /**
     * Configuración del interceptor de logging.
     * En producción se podría desactivar cambiando a NONE.
     */
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    /**
     * Cliente OkHttp con timeouts y logging configurados.
     * - connectTimeout: tiempo máximo para establecer conexión
     * - readTimeout: tiempo máximo para leer respuesta del servidor
     * - writeTimeout: tiempo máximo para enviar datos al servidor
     */
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    /**
     * Instancia de Retrofit lista para usar.
     * Usa lazy para inicialización diferida (solo cuando se necesite).
     */
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Servicio de la API listo para realizar llamadas HTTP.
     * Se accede a través de: RetrofitClient.apiService.getLatestRates(...)
     */
    val apiService: ExchangeRateApiService by lazy {
        retrofit.create(ExchangeRateApiService::class.java)
    }
}
