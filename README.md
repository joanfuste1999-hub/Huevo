# Huevo

Aplicación Android (Kotlin + Jetpack Compose) para ayudar a reducir o dejar un hábito de
masturbación demasiado frecuente, con un enfoque positivo y sin culpa: autocontrol, hábitos,
progreso y acompañamiento.

La app gira en torno a un **compañero virtual** (un huevo que evoluciona en ave) que refleja la
racha actual del usuario en 6 etapas: huevo (día 1), huevo agrietado (día 3), pollito recién
nacido (día 7), pollito joven (día 14), ave joven (día 21) y evolución máxima (día 30). El
compañero está dibujado íntegramente con `Canvas` de Compose (`ui/companion/CompanionView.kt`),
sin assets externos, y expresa 7 estados de ánimo distintos.

## Estructura del proyecto

```
app/src/main/java/com/huevo/app/
├── data/                  Repositorio, Room (check-ins e impulsos), DataStore (perfil y racha)
├── model/                 Modelos de dominio (etapas, expresiones, respuestas de onboarding...)
├── ui/
│   ├── companion/         Ilustración del compañero (Canvas)
│   ├── components/        Botones, tarjetas, chips, barras de progreso reutilizables
│   ├── navigation/        Destinos y barra de navegación inferior
│   ├── onboarding/        Flujo de bienvenida de 11 pasos
│   ├── screens/           Hoy, Progreso, Impulso, Objetivo, Patrones, Perfil
│   ├── theme/             Paleta cálida (crema + naranja), tipografía, formas
│   └── tips/              Consejos diarios rotativos
├── HuevoApplication.kt
└── MainActivity.kt
```

## Privacidad

Todos los datos (perfil, racha, historial de check-ins, registros de impulso) se guardan
exclusivamente en el dispositivo mediante Room y DataStore. No hay backend, cuentas en la nube,
perfiles públicos ni funciones sociales. `data_extraction_rules.xml` excluye explícitamente la
base de datos y las preferencias de cualquier copia de seguridad en la nube.

## Cómo abrir el proyecto

1. Abre la carpeta raíz con Android Studio (Koala o superior recomendado).
2. Deja que Gradle sincronice (usa AGP 8.7.2, Kotlin 2.0.21, Compose BOM 2024.12.01).
3. Ejecuta la configuración `app` en un emulador o dispositivo con Android 8.0 (API 26) o superior.

> Nota: este proyecto se generó en un entorno sin SDK de Android ni acceso a los repositorios de
> Google, por lo que no se pudo ejecutar una compilación real con Gradle en esta sesión. El código
> se ha revisado manualmente con cuidado, pero se recomienda compilar una vez en Android Studio
> antes de darlo por definitivo.

## Stack técnico

- Kotlin + Jetpack Compose (Material 3)
- Navigation Compose
- Room (historial de check-ins y registros de impulso)
- DataStore Preferences (perfil, racha, objetivo)
- Arquitectura MVVM con `StateFlow`
