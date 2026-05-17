🌊 SeaStream
Plataforma Streaming
Trabajo de Fin de Grado · Ciclo Superior DAM

---
📖 Descripción
SeaStream es una aplicación Android desarrollada como Proyecto Final de Ciclo para el Grado Superior en Desarrollo de Aplicaciones Multiplataforma (DAM). 
Consume la API de TMDB para ofrecer un catálogo actualizado de contenido multimedia, y permite a los usuarios gestionar sus propias listas de seguimiento de forma persistente mediante Firebase y Room.
---
📦 Instalación rápida (APK)
> Si solo quieres probar la app sin compilar el proyecto, descarga directamente la APK precompilada.
Ve a la sección Releases de este repositorio.
Descarga el archivo `.apk` de la última versión disponible.
En tu dispositivo Android, ve a Ajustes → Seguridad y activa "Instalar aplicaciones de fuentes desconocidas" (o "Instalar apps desconocidas", según tu versión de Android).
Abre el archivo `.apk` descargado y sigue los pasos del instalador.
¡Listo! Inicia sesión con las credenciales de prueba o crea tu cuenta.
> **Requisito mínimo:** Android 7.0 Nougat (API 24) o superior.
---
🛠️ Requisitos del Entorno
Para compilar el proyecto desde el código fuente, asegúrate de contar con el siguiente entorno:
Herramienta	Versión requerida
Android Studio	Ladybug (2024.2.1) o superior
JDK	21
`compileSdk`	36
`minSdk`	24 (Android 7.0 Nougat)
`targetSdk`	36
Gradle	8.x (gestionado por el Gradle Wrapper)
Además, se requiere conexión a internet activa para el consumo de la API de TMDB y la autenticación con Firebase.
---
🚀 Pasos para Compilar y Ejecutar
1. Clonar el repositorio
```bash
git clone https://github.com/tu-usuario/proyectoFinalGradoSuperior.git
```
2. Abrir el proyecto
Ejecuta Android Studio y selecciona Open, navegando hasta la carpeta raíz del proyecto clonado.
3. Sincronizar dependencias
Haz clic en el icono del elefante "Sync Project with Gradle Files" para descargar todas las librerías necesarias:
Jetpack Compose
Room
Retrofit
Firebase
EncryptedSharedPreferences, entre otras.
4. Configurar Firebase
Verifica que el archivo `google-services.json` esté presente en el directorio `/app`.
> ⚠️ El proyecto incluye un archivo de desarrollo. Para entornos de producción, debe ser reemplazado por uno propio generado desde la consola de Firebase.
5. Ejecutar la aplicación
Conecta un dispositivo físico o inicia un emulador con API 24 o superior y pulsa el botón ▶️ Run.
---
📂 Estructura General del Proyecto
El proyecto sigue los principios de Clean Architecture junto al patrón de diseño MVVM (Model-View-ViewModel), garantizando un código desacoplado, testeable y mantenible.
```
org.dam2.appstreaming/
│
├── data/                          # 🗄️ Capa de Datos
│   ├── remote/                    # Retrofit + DTOs (tmdb / backend)
│   ├── local/                     # Room (SQLite) + EncryptedSharedPreferences
│   ├── repository/                # Mediadores entre fuentes de datos
│   └── mapper/                    # Transformación de modelos red → dominio
│
└── ui/                            # 🎨 Capa de Presentación
    ├── screen/                    # Pantallas en Jetpack Compose + ViewModels
    ├── component/                 # Componentes visuales reutilizables
    └── colors/                    # Sistema de diseño y paleta cromática
```
Capa de datos
`data/remote`	Consumo de APIs REST mediante Retrofit, con DTOs separados por origen
`data/local`	Persistencia con Room y almacenamiento seguro con EncryptedSharedPreferences
`data/repository`	Coordinación entre fuentes remotas y locales
`data/mapper`	Desacoplamiento entre modelos de red y de dominio

Capa de presentación
`ui/screen`	Pantallas declarativas con Jetpack Compose y su ViewModel asociado
`ui/component`	Librería interna de componentes reutilizables
`ui/colors`	Tokens de diseño y definición de la paleta cromática

---
🔑 Credenciales de Prueba
Para facilitar la evaluación sin necesidad de registro previo, se han habilitado las siguientes credenciales sincronizadas con Firebase Auth:
Campo	Valor
📧 Usuario	`test@seastream.com`
🔒 Contraseña	`Test1234#`
---
👩‍💻 Autoría
Autora	Alba Carrobles, Sergio Gárgoles, Emilio Abril
Ciclo	Grado Superior — Desarrollo de Aplicaciones Multiplataforma (DAM)
Centro	IES Villablanca
API utilizada	The Movie Database (TMDB)
