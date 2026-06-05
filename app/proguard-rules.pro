# Reglas de ProGuard para CambioCA
# Mantener modelos de datos para Gson
-keep class com.example.cambioca.data.model.** { *; }
# Mantener clases de Retrofit
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
