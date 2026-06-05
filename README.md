# CambioCA 💱

Aplicación Android para convertir dólares estadounidenses (USD) a monedas de la región Centroamérica y México.

## Monedas soportadas

| Moneda | Código | País |
|--------|--------|------|
| 🇬🇹 Quetzal guatemalteco | GTQ | Guatemala |
| 🇭🇳 Lempira hondureño | HNL | Honduras |
| 🇳🇮 Córdoba nicaragüense | NIO | Nicaragua |
| 🇲🇽 Peso mexicano | MXN | México |

## Tecnologías utilizadas

- **Lenguaje**: Kotlin
- **UI**: Jetpack Compose + Material3
- **Arquitectura**: MVVM (Model-View-ViewModel)
- **HTTP Client**: Retrofit 2 + OkHttp 4
- **JSON**: Gson (GsonConverterFactory)
- **Async**: Kotlin Coroutines + StateFlow
- **State Management**: ViewModel + StateFlow

## API utilizada

**ExchangeRate-API** — [exchangerate-api.com](https://www.exchangerate-api.com)

- Versión: v6
- Endpoint: `GET https://v6.exchangerate-api.com/v6/{API_KEY}/latest/USD`
- Plan gratuito: 1,500 peticiones/mes (sin tarjeta de crédito)
- Actualización: diaria

## Cómo configurar

1. Crea una cuenta gratuita en [exchangerate-api.com](https://www.exchangerate-api.com)
2. Copia tu API Key
3. Abre `app/src/main/java/com/example/cambioca/data/repository/ExchangeRateRepository.kt`
4. Reemplaza `"YOUR_API_KEY_HERE"` con tu clave

```kotlin
private val apiKey = "TU_API_KEY_AQUI"
```

## Estructura del proyecto

```
CambioCA/
├── app/src/main/java/com/example/cambioca/
│   ├── MainActivity.kt                    # Punto de entrada
│   ├── data/
│   │   ├── api/
│   │   │   ├── ExchangeRateApiService.kt  # Interfaz Retrofit
│   │   │   └── RetrofitClient.kt          # Cliente HTTP configurado
│   │   ├── model/
│   │   │   └── ExchangeRateModel.kt       # Modelos de datos + ConversionState
│   │   └── repository/
│   │       └── ExchangeRateRepository.kt  # Capa de acceso a datos
│   ├── viewmodel/
│   │   └── CurrencyViewModel.kt           # Lógica de negocio y estado UI
│   └── ui/
│       ├── theme/
│       │   ├── Theme.kt                   # Material3 con colores CA
│       │   └── Type.kt                    # Tipografía
│       ├── components/
│       │   ├── AmountInput.kt             # Campo de monto USD
│       │   ├── CurrencySelector.kt        # Selector de moneda destino
│       │   └── ConversionResult.kt        # Tarjeta de resultado
│       └── screens/
│           └── CurrencyConverterScreen.kt # Pantalla principal
└── app/src/main/res/
    └── values/
        ├── strings.xml
        ├── colors.xml
        └── themes.xml
```

## Funcionamiento de la App

1. El usuario ingresa un monto en dólares en el campo de entrada
2. Selecciona la moneda destino entre GTQ, HNL, NIO o MXN
3. Presiona el botón **Convertir** (o la tecla Done del teclado)
4. La app realiza una petición GET a ExchangeRate-API con Retrofit
5. El repositorio parsea la respuesta JSON con Gson
6. El ViewModel calcula: `resultado = monto × tipoDeCambio`
7. El resultado se muestra con animación junto al tipo de cambio y fecha de actualización

## Validaciones implementadas

- ✅ Campo vacío → mensaje "Ingresa un monto en dólares"
- ✅ Texto no numérico → mensaje "Ingresa un número válido"
- ✅ Monto negativo → mensaje "El monto debe ser mayor o igual a cero"
- ✅ Sin conexión a internet → `IOException` capturado → mensaje de error
- ✅ API Key inválida (HTTP 401/403) → mensaje de error específico
- ✅ Límite de peticiones (HTTP 429) → mensaje descriptivo
- ✅ Errores del servidor (HTTP 500/503) → mensaje al usuario

## Criterios de evaluación

| Criterio | Implementación |
|----------|---------------|
| Interfaz gráfica funcional (20%) | Jetpack Compose + Material3, diseño limpio |
| Consumo correcto de la API (25%) | Retrofit + Gson + OkHttp + Repository pattern |
| Conversión correcta (20%) | `monto × rate` con 4 decimales de precisión |
| Validaciones y errores (15%) | 7 validaciones + manejo completo de excepciones |
| Organización del código (10%) | Arquitectura MVVM con KDoc en cada clase |
| Presentación (10%) | README completo + commits descriptivos |

## Autor

Estudiante — Facultad de Informática, UTEC El Salvador  
Fecha: Junio 2026
