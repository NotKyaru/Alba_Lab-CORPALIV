# **CorpalivAlbaLab**

Aplicación móvil Android (MVP) para la difusión de impacto social y vinculación
empresarial de CORPALIV (Corporación de Padres y Amigos por el Limitado Visual)
y su Red Sociolaboral Alba Lab.

Proyecto de la asignatura DSY1105 · Desarrollo de Aplicaciones Móviles (Duoc UC).

Pantallas: Inicio, Programas, Catálogo, Impacto, Colaborar, Contacto, Registros.

⚠️ Todos los datos son ficticios (el caso lo exige).

Tecnología: Kotlin + Jetpack Compose (Material Design 3), MVVM, Retrofit y un backend
local de 3 microservicios en Spring Boot (Kotlin) con base de datos H2.

---

## Arquitectura

```
┌──────────────── App Android (Compose + MVVM) ────────────────┐
│ Pantalla ← StateFlow ← ViewModel (consulta cada 3 s)          │
│                           ← Repositorio ← Retrofit + Gson     │
└───────────────┬───────────────┬───────────────┬──────────────┘
      127.0.0.1:8081     127.0.0.1:8082     127.0.0.1:8083   (adb reverse → PC)
┌───────────────▼──┐ ┌──────────▼───────┐ ┌─────▼────────────┐
│ ms-catalogo      │ │ ms-institucional │ │ ms-registros     │
│ productos        │ │ programas, casos │ │ registros de     │
│ (Spring Boot)    │ │ de éxito         │ │ interés          │
└───────┬──────────┘ └───────┬──────────┘ └──────┬───────────┘
   H2: data/catalogo   H2: data/institucional  H2: data/registros
```

Cada microservicio es una aplicación Spring Boot independiente (controlador REST +
repositorio Spring Data JPA) con su propia base de datos H2 guardada en archivo.

**Tiempo real:** cada ViewModel consulta su microservicio cada 3 segundos. Si un dato
cambió en el backend, la pantalla se actualiza sola, sin recargar la app.

| Microservicio | Puerto | Endpoints |
|---|---|---|
| `ms-catalogo` | 8081 | `GET /productos` · `GET /productos/{id}` · `POST /productos` · `PUT /productos/{id}` · `DELETE /productos/{id}` |
| `ms-institucional` | 8082 | `GET /programas` · `PUT /programas/{id}` · `GET /casos-exito` · `PUT /casos-exito/{id}` |
| `ms-registros` | 8083 | `GET /registros` · `POST /registros` · `PATCH /registros/{id}/estado` · `DELETE /registros/{id}` |

Cada servicio valida los datos que recibe y responde `400` con un mensaje si algo falta,
o `404` si el id no existe.

---

## Requisitos

- Android Studio con Android SDK (incluye `adb` y el emulador).
- JDK 17 o superior. Si no está instalado, Gradle descarga Java 17 para el backend.
- Un emulador creado en Device Manager, o un teléfono Android conectado por USB.

---

## Cómo ejecutar

**1. Levantar los 3 microservicios** (dejar esta terminal abierta):

```powershell
cd backend
.\gradlew.bat bootRun --parallel
```

La consola muestra `Started ...Application` de un solo servicio, porque Gradle agrupa la
salida de las tareas que siguen corriendo. Para confirmar que están los tres, abre en el navegador
`http://localhost:8081/productos`, `http://localhost:8082/programas` y
`http://localhost:8083/registros`.

**2. Iniciar el emulador** (o conectar el teléfono por USB).

**3. Conectar el dispositivo con el backend** (en otra terminal, desde la raíz del repo):

```powershell
powershell -ExecutionPolicy Bypass -File backend\conectar-dispositivo.ps1
```

Este paso usa `adb reverse` para que `127.0.0.1` dentro del dispositivo llegue al PC.
**Hay que repetirlo cada vez que se reinicia el emulador o se reconecta el teléfono.**

**4. Ejecutar la app** desde Android Studio (Run ▶).

---

## Probar la actualización en tiempo real

Con la app abierta en **Catálogo**, cambia un dato de una de estas dos formas. El cambio
aparece en el dispositivo en 3 segundos o menos.

**Desde la consola de H2** (en el navegador del PC):

1. Abre `http://localhost:8081/h2-console`.
2. En **JDBC URL** escribe `jdbc:h2:file:./data/catalogo`, usuario `sa`, contraseña vacía, y
   presiona **Connect**.
3. Ejecuta, por ejemplo: `UPDATE PRODUCTO SET PRECIO = 7000 WHERE ID = 1`

| Servicio | Consola | JDBC URL | Tablas |
|---|---|---|---|
| ms-catalogo | `http://localhost:8081/h2-console` | `jdbc:h2:file:./data/catalogo` | `PRODUCTO` |
| ms-institucional | `http://localhost:8082/h2-console` | `jdbc:h2:file:./data/institucional` | `PROGRAMA`, `CASO_EXITO` |
| ms-registros | `http://localhost:8083/h2-console` | `jdbc:h2:file:./data/registros` | `REGISTRO_INTERES` |

La consola solo acepta conexiones desde el mismo PC.

**Desde PowerShell**, con la API REST:

```powershell
Invoke-RestMethod -Method Put -Uri http://localhost:8081/productos/1 -ContentType 'application/json; charset=utf-8' -Body '{"nombre":"Velas artesanales","descripcion":"Velas decorativas hechas a mano.","precio":5000}'
```

Otro ejemplo: marcar un registro como contactado (pantalla Registros):

```powershell
Invoke-RestMethod -Method Patch -Uri http://localhost:8083/registros/1/estado -ContentType 'application/json; charset=utf-8' -Body '{"estado":"Contactado"}'
```

---

## Datos

- Cada servicio guarda su base de datos H2 en su carpeta `data/` (no se sube a GitHub).
  Los datos se mantienen aunque se reinicie el backend.
- Si la base está vacía, el servicio carga los datos ficticios de `src/main/resources/seed/`.
- Para volver a los datos iniciales: detén el backend, borra las carpetas `backend/*/data/`
  y vuelve a levantarlo.
- Los ids los asigna H2. Tras un reinicio pueden saltar (por ejemplo, de 2 a 33), porque H2
  reserva ids de a 32. Es normal.

---

## Pruebas

| Qué | Comando | Pruebas |
|---|---|---|
| Backend (JUnit 5 + MockMvc, H2 en memoria) | `cd backend; .\gradlew.bat test` | 15 |
| App, unitarias (Kotest + MockK + coroutines-test) | `.\gradlew.bat :app:testDebugUnitTest` | 13 |
| App, de UI (Compose UI Test, requiere emulador abierto) | `.\gradlew.bat :app:connectedDebugAndroidTest` | 7 |

El reporte de las pruebas de UI queda en
`app/build/reports/androidTests/connected/debug/index.html`.

---

## Solución de problemas

| Síntoma | Causa y solución |
|---|---|
| La app muestra "Sin conexión con el servidor. Reintentando…" | El backend no está levantado o falta el paso 3. Ejecuta `conectar-dispositivo.ps1` de nuevo. En Logcat, el filtro `Sondeo` muestra el error exacto. |
| Las tildes quedan como `�` al usar `Invoke-RestMethod` | PowerShell 5.1 envía el texto en Latin-1 si falta `charset=utf-8` en `-ContentType`. |
| `adb` no responde o el script se queda esperando | Cierra los procesos colgados con `taskkill /F /IM adb.exe` y reinicia el emulador. |
| `Port 8081 was already in use` al levantar el backend | Ya hay otra copia del backend corriendo. Ciérrala o cierra la terminal anterior. |
| La consola de H2 dice que la base está en uso o no muestra las tablas | Revisa que la JDBC URL sea la de la tabla de arriba (con `./data/...`) y que el servicio esté corriendo. |

---

## Estructura

```
app/src/main/java/com/corpaliv/albalab/
├── data/model/      modelos (data class) y UiState
├── data/remote/     Retrofit: ApiService y RetrofitInstance
├── repository/      un repositorio por microservicio
├── viewmodel/       ViewModels y sondeo en tiempo real
├── navigation/      pantallas de la app
└── ui/              pantallas, componentes y tema
backend/                 (cada servicio: *Application, *Controller, entidades + repositorio)
├── ms-catalogo/     puerto 8081
├── ms-institucional/ puerto 8082
├── ms-registros/    puerto 8083
└── conectar-dispositivo.ps1
```
