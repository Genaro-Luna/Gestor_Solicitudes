# Etapa 6 — Pruebas y verificación del comportamiento

**Responsable:** Akier Aguirrezabala · **Rama:** `test/contrato` · **Tag:** `etapa-pruebas`

## 1. Qué se prueba y cómo

Las pruebas verifican el **contrato de comportamiento externo** (`docs/contrato-comportamiento.md`): los métodos públicos de `GestorSolicitudes`, el reporte, las notificaciones por consola y el menú de `Main`. Están escritas para correr **sin modificarse** sobre `estado-inicial` y sobre `estado-refactorizado`; por eso usan solo la API pública del contrato (el estado de una solicitud se consulta con `buscarPorEstado(...)`, no leyendo campos que el refactoring pueda encapsular).

| Clase de pruebas | Qué cubre |
|---|---|
| `RegistrarSolicitudTest` | Validaciones, ids, mayúsculas/minúsculas, valores límite, reutilización de estudiante |
| `AsignarResponsableTest` | Asignación por estado, entradas inválidas, solicitudes finalizadas |
| `CambiarEstadoTest` | Las 4 transiciones válidas, los 20 pares inválidos, regla del responsable, ciclo completo |
| `BusquedasTest` | `obtener`, `buscarPor…`, `listarTodas`, orden de alta, datos de prueba |
| `ReporteTest` | Texto exacto del reporte, contadores y plazos de vencimiento en valores límite |
| `NotificacionesTest` | Que cada evento notifica al estudiante y que las operaciones rechazadas no notifican |
| `MenuConsolaTest` | Prueba de humo de extremo a extremo manejando el menú por entrada estándar |
| `MejorasEsperadasTest` | 2 pruebas de **mejora** (defectos de la Etapa 2), omitidas por defecto |
| `InyeccionDependenciasTest` | Lo que habilita el refactoring: `Notificador` y `Clock` inyectables (RI-01 a RI-07). **Solo compila sobre `estado-refactorizado`** |
| `PruebaBase` | Gestor con datos de prueba, captura de consola y utilidades comunes |

Todas siguen la estructura **AAA** (Arrange–Act–Assert), con casos válidos, inválidos y valores límite.

## 2. Cómo ejecutarlas

```bash
mvn test                      # contrato: 108 pruebas (las 2 de mejora quedan omitidas)
mvn test -Dmejoras=true       # suma las 2 pruebas de mejora (MJ-01 y MJ-02)
```

**Antes / después.** Para correr las mismas pruebas sobre cada versión del código se usa una copia de trabajo aparte por tag, así no se mezclan archivos de una versión con otra:

```bash
# ANTES: código de estado-inicial (sin InyeccionDependenciasTest, que usa clases nuevas)
git worktree add ../prueba-antes estado-inicial
cp -r src/test ../prueba-antes/src/
rm ../prueba-antes/src/test/java/solicitudes/InyeccionDependenciasTest.java
(cd ../prueba-antes && mvn test -Dmejoras=true)

# DESPUÉS: código de estado-refactorizado (todas las pruebas)
git worktree add ../prueba-despues etapa-refactoring
cp -r src/test ../prueba-despues/src/
(cd ../prueba-despues && mvn test -Dmejoras=true)
```

La cobertura se obtiene en IntelliJ con *Run > Run 'All Tests' with Coverage* (misma medición que se hizo con JaCoCo, ver evidencias).

## 3. Resultados sobre `estado-inicial` (ANTES)

- **110 pruebas encontradas; 108 pasan, 0 fallan, 2 omitidas** (las de mejora, que se activan con `-Dmejoras=true`).
- Con `-Dmejoras=true`: **MJ-01 y MJ-02 fallan**, como se esperaba: son los dos defectos de la Etapa 2 (id no numérico en el menú; `listarTodas()` expone la lista interna: un `clear()` externo vacía el repositorio).
- **Cobertura (JaCoCo 0.8.11):** 98,1 % de líneas (259/264), 97,0 % de ramas (163/168), `GestorSolicitudes` 97,6 % de líneas.
- **Prueba de mutación manual:** 18 fallas inyectadas, **18 detectadas** (`evidencias/etapa-6/mutantes-estado-inicial.md`).

### Lo que NO se cubre y por qué
| Ubicación | Motivo |
|---|---|
| `GestorSolicitudes` líneas 87, 121, 175 (`catch` vacíos) | El notificador nunca lanza excepciones: no hay forma de provocarlas sin poder inyectar un notificador (smell de acoplamiento, Etapa 3 n.º 5 y DIP). |
| `GestorSolicitudes` líneas 149–153 (`PENDIENTE → EN_REVISION` con responsable) | **Rama inalcanzable desde la API pública:** asignar un responsable ya pasa la solicitud a EN_REVISION, así que nunca hay una PENDIENTE con responsable. Es código muerto por diseño. |
| `GestorSolicitudes` líneas 161, 237, 241 (últimos `else if`) | Condiciones siempre verdaderas por invariante (el estado y la prioridad siempre son válidos). |
| `Main` línea 57 (rama `false` de asignar por menú) | No se probó el mensaje de asignación fallida por menú. |

## 4. Casos de prueba (ID → pruebas)

Columna **Antes**: resultado sobre `estado-inicial`. Columna **Después**: resultado sobre `estado-refactorizado` (tag `etapa-refactoring`, commit `ad421e7`).

| ID | Método | Entrada | Resultado esperado | Antes | Después |
|---|---|---|---|---|---|
| CP-01 | `registrarSolicitud` | Alta válida (legajo 3001, Marta Ruiz, CERTIFICADO, ALTA, descripción ≥ 10) | Devuelve 4; queda PENDIENTE; el total pasa a 4 | ✔ | ✔ |
| CP-02 | `registrarSolicitud` | Gestor vacío, dos altas válidas seguidas | Ids 1 y 2 (autoincremental desde 1) | ✔ | ✔ |
| CP-03 | `registrarSolicitud` | 9 casos inválidos: legajo vacío, legajo en blanco, nombre vacío, apellido vacío, email sin @, tipo inexistente, prioridad inexistente, descripción de 9 caracteres, descripción corta con espacios | Devuelve -1 y no agrega ninguna solicitud | ✔ | ✔ |
| CP-04 | `registrarSolicitud` | Cada parámetro en null, de a uno | Devuelve -1 sin lanzar excepción | ✔ | ✔ |
| CP-05 | `registrarSolicitud` | Los 4 tipos válidos y variantes en minúscula (“certificado”, “Reclamo_Nota”) | Se acepta (id > 0) y aparece en buscarPorTipo | ✔ | ✔ |
| CP-06 | `registrarSolicitud` | Prioridades BAJA, MEDIA, ALTA, “baja”, “Alta” | Se acepta (id > 0) | ✔ | ✔ |
| CP-07 | `registrarSolicitud` | Descripción de exactamente 10 caracteres (valor límite) | Se acepta (devuelve 4) | ✔ | ✔ |
| CP-08 | `registrarSolicitud` | Legajo existente (1001) con otro email | Se suma a las del legajo (3) y la notificación va al email original | ✔ | ✔ |
| CP-09 | `registrarSolicitud` | Legajo null, “”, “ ” y tabulación | Devuelve -1 | ✔ | ✔ |
| CP-10 | `asignarResponsable` | Solicitud #1 (PENDIENTE) | true; pasa a EN_REVISION | ✔ | ✔ |
| CP-11 | `asignarResponsable` | Solicitud #2 (EN_REVISION) | true; sigue EN_REVISION | ✔ | ✔ |
| CP-12 | `asignarResponsable` | Solicitud RESUELTA | true; sigue RESUELTA | ✔ | ✔ |
| CP-13 | `asignarResponsable` | Ids inexistentes 99, 0 y -1 | false | ✔ | ✔ |
| CP-14 | `asignarResponsable` | Responsable null, vacío o solo espacios | false; el estado no cambia | ✔ | ✔ |
| CP-15 | `asignarResponsable` | Solicitud CERRADA o RECHAZADA | false; el estado no cambia | ✔ | ✔ |
| CP-16 | `cambiarEstado` | 4 transiciones válidas: PENDIENTE→RECHAZADA, EN_REVISION→RESUELTA, EN_REVISION→RECHAZADA, RESUELTA→CERRADA | true; queda en el nuevo estado | ✔ | ✔ |
| CP-17 | `cambiarEstado` | 20 pares no permitidos (incluye mismo estado y salidas desde RECHAZADA y CERRADA) | false; el estado no cambia | ✔ | ✔ |
| CP-18 | `cambiarEstado` | #1 PENDIENTE sin responsable → EN_REVISION | false; sigue PENDIENTE | ✔ | ✔ |
| CP-19 | `cambiarEstado` | Nuevo estado en minúsculas (“resuelta”) | true; queda RESUELTA | ✔ | ✔ |
| CP-20 | `cambiarEstado` | Id inexistente, estado null y estado desconocido (“ARCHIVADA”) | false; sin cambios | ✔ | ✔ |
| CP-21 | `cambiarEstado` | Ciclo completo: alta, asignación, RESUELTA y CERRADA | Cada paso deja el estado esperado | ✔ | ✔ |
| CP-22 | `obtener` | Ids 1 y 3; ids 99, 0 y -5 | La solicitud existente; null si no existe | ✔ | ✔ |
| CP-23 | `buscarPorEstado` | 7 entradas: PENDIENTE, “pendiente”, “En_Revision”, RESUELTA, RECHAZADA, CERRADA, “INEXISTENTE” | 2, 2, 1, 0, 0, 0, 0 resultados | ✔ | ✔ |
| CP-24 | `buscarPorTipo` | 5 entradas (incluye “equivalencia” y un tipo inexistente) | 1, 1, 1, 0, 0 resultados | ✔ | ✔ |
| CP-25 | `buscarPorLegajo` | 1001, 1002 y 9999 | 2, 1 y 0 resultados | ✔ | ✔ |
| CP-26 | `búsquedas` | buscarPorEstado(PENDIENTE) y buscarPorLegajo(1001) | Devuelven [#1, #3] en orden de alta | ✔ | ✔ |
| CP-27 | `listarTodas` | Datos de prueba | 3 solicitudes, en orden #1, #2, #3 | ✔ | ✔ |
| CP-28 | `búsquedas y listarTodas` | Gestor sin datos | Listas vacías | ✔ | ✔ |
| CP-29 | `cargarDatosDePrueba` | Gestor nuevo | 3 solicitudes: #1 PENDIENTE, #2 EN_REVISION, #3 PENDIENTE | ✔ | ✔ |
| CP-30 | `generarReporte` | Datos de prueba | Texto idéntico al del contrato (Total 3, PENDIENTE 2, EN_REVISION 1, Vencidas 0) | ✔ | ✔ |
| CP-31 | `generarReporte` | Gestor vacío | Todos los contadores en 0 | ✔ | ✔ |
| CP-32 | `generarReporte` | 3 altas llevadas a RESUELTA, CERRADA y RECHAZADA | Total 6; cuenta correcta por estado y prioridad | ✔ | ✔ |
| CP-33 | `generarReporte` | Un alta rechazada por validación | El reporte no cambia | ✔ | ✔ |
| CP-34 | `generarReporte` | Solicitud antedatada en el límite del plazo: ALTA 2/3 días, MEDIA 5/6, BAJA 10/11 | Vencida solo si pasa más del plazo (0 o 1 vencida) | ✔ | ✔ |
| CP-35 | `generarReporte` | 3 solicitudes ALTA de 30 días en PENDIENTE, EN_REVISION y RESUELTA | Vencidas: 2 (la RESUELTA no cuenta) | ✔ | ✔ |
| CP-36 | `notificación` | Alta válida | La consola muestra el email del estudiante y el #id | ✔ | ✔ |
| CP-37 | `notificación` | Asignación de responsable | La consola muestra el email y el nombre del responsable | ✔ | ✔ |
| CP-38 | `notificación` | Cambio de estado a RESUELTA | La consola muestra el email y el nuevo estado | ✔ | ✔ |
| CP-39 | `notificación` | Alta inválida, asignación a id inexistente y transición inválida | No se envía ninguna notificación | ✔ | ✔ |
| CP-40 | `menú (Main)` | Opciones 7, 8 y 0 | Lista las solicitudes, muestra “Total: 3” y “Fin del programa” | ✔ | ✔ |
| CP-41 | `menú (Main)` | Flujo de la evidencia 1.8: EN_REVISION sin responsable, asignar, RESUELTA, reporte | Rechaza el pase sin responsable; luego “Responsable asignado”, “Estado actualizado” y RESUELTA: 1 | ✔ | ✔ |
| CP-42 | `menú (Main)` | Buscar por estado CERRADA (opción 4) | “No hay resultados” | ✔ | ✔ |
| CP-43 | `menú (Main)` | Registrar por el menú con datos válidos (opción 1) | “Solicitud registrada con id 4” y figura en el listado | ✔ | ✔ |
| CP-44 | `menú (Main)` | Registrar por el menú con email sin @ | No se crea la solicitud (Total: 3) | ✔ | ✔ |
| CP-45 | `menú (Main)` | Buscar por tipo y por legajo (opciones 5 y 6) | Lista las solicitudes #1, #2 y #3 según el filtro | ✔ | ✔ |
| CP-46 | `menú (Main)` | Opción 99 y opción “abc” | “Opcion invalida” y el menú sigue funcionando | ✔ | ✔ |

Pruebas de mejora (no forman parte del contrato; `-Dmejoras=true`):

| ID | Entrada | Resultado esperado | Antes | Después |
|---|---|---|---|---|
| MJ-01 | Menú: opción 3 con id “abc” | El programa no termina con `NumberFormatException` | ✘ falla (defecto conocido) | ✔ pasa |
| MJ-02 | `listarTodas().clear()` desde afuera | El repositorio conserva sus 3 solicitudes | ✘ falla (lista interna expuesta) | ✔ pasa |

## 4 bis. Resultados sobre `estado-refactorizado` (DESPUÉS)

Código: tag `etapa-refactoring` (commit `ad421e7`, rama `refactor/gestor`). Las pruebas del contrato **no se modificaron**.

| Medición | Antes (`estado-inicial`) | Después (`estado-refactorizado`) |
|---|---|---|
| Pruebas del contrato (108) | 108 pasan | **108 pasan** |
| Pruebas de mejora MJ-01 y MJ-02 | 2 fallan (defectos) | **2 pasan** (defectos corregidos) |
| Pruebas de inyección RI-01 a RI-07 (12 ejecuciones) | no aplican (no existe la interfaz) | **12 pasan** |
| Pruebas omitidas por `fechaCreacion` (CP-34, CP-35) | 0 | 0 (el campo sigue siendo `LocalDate`) |
| Cobertura de líneas (solo contrato) | 98,1 % (259/264) | 94,6 % (330/349) |
| Cobertura de líneas (con RI) | — | 96,3 % (336/349) |
| Cobertura de ramas (solo contrato / con RI) | 97,0 % | 92,6 % / 93,4 % |
| Mutantes detectados | 18 de 18 | 16 de 16 |

**Conclusión:** el refactoring preservó el comportamiento del contrato (0 regresiones) y corrigió los dos defectos conocidos. La cobertura baja un poco en porcentaje porque el código nuevo tiene getters y ramas defensivas que la API pública nunca ejecuta (ver abajo), no porque haya menos comportamiento probado.

### Pruebas de inyección de dependencias (solo `estado-refactorizado`)
| ID | Qué verifica | Resultado |
|---|---|---|
| RI-01 | Un `Notificador` en memoria (doble de prueba) recibe cada evento: alta, asignación y cambio de estado | ✔ |
| RI-02 | Las operaciones rechazadas no llaman al notificador | ✔ |
| RI-03 | Si el notificador lanza una excepción, la operación se completa y el fallo se informa por `System.err` | ✔ |
| RI-04 | Vencimiento en los valores límite (2/3, 5/6, 10/11 días) con un `Clock` inyectado, sin reflexión | ✔ (6) |
| RI-05 | Una solicitud RESUELTA no vence aunque pasen 60 días | ✔ |
| RI-06 | `registrarSolicitud(DatosRegistroSolicitud)` equivale a la firma de 7 parámetros | ✔ |
| RI-07 | `listarTodas()` devuelve una vista no modificable | ✔ |

Estas pruebas respaldan con evidencia ejecutable lo que el informe afirma de LSP y DIP (cualquier `Notificador` es sustituible; el reloj es inyectable).

### Lo que sigue sin cubrirse en `estado-refactorizado`
| Ubicación | Motivo |
|---|---|
| `Solicitud` líneas 55, 59, 63 | Getters sin uso: `getDescripcion`, `getResponsable`, `getFechaCreacion`. |
| `Solicitud` línea 96 | `estaVencida()` sin argumentos: **código sin uso** que además usa `LocalDate.now()` y no el `Clock` inyectado. |
| `EstadoSolicitud` líneas 22, 28, 34 | Ramas defensivas que la API pública no alcanza (destino nulo, estados finales y texto nulo ya filtrados antes por el gestor). |
| `Estudiante` / `Responsable` | `getNombre`, `getApellido` y `getArea` sin uso. |
| `Main` línea 6 y ramas de “id no numérico” de las opciones 2 y 3 | Constructor implícito de la clase; las ramas de error solo se recorren parcialmente (MJ-01 cubre la opción 3). |

## 5. Supuestos que las pruebas hacen sobre el refactoring (verificados)

Los seis supuestos se cumplieron en `etapa-refactoring`: se conservaron el constructor sin argumentos, las firmas del contrato, `Main.main`, los textos del menú, el formato del reporte y `fechaCreacion` como `LocalDate`.

Para que el mismo código de pruebas compile y corra sobre `estado-refactorizado`, el refactoring debe conservar:

1. `new GestorSolicitudes()` **sin argumentos** y `cargarDatosDePrueba()`. Si se agrega inyección de dependencias, dejar un constructor sin argumentos que arme los valores por defecto.
2. Las firmas del contrato con `String` (`registrarSolicitud`, `asignarResponsable`, `cambiarEstado`, `buscarPor…`) y que `obtener`/`buscarPor…`/`listarTodas` devuelvan **las mismas instancias** de `Solicitud` (las pruebas usan `contains` y `assertSame`; si se devuelven copias o DTO, hay que definir `equals`).
3. `Main.main(String[])` y los textos del menú que muestra el informe (1.8): “Opcion invalida”, “No hay resultados”, “Fin del programa”, “Responsable asignado”, “Estado actualizado”, “Solicitud registrada con id N”, y el formato de `Solicitud.toString()` (`#1 [CERTIFICADO] PENDIENTE - prioridad ALTA - Ana Gomez`).
4. Que cada notificación impresa por consola incluya el email del estudiante, el `#id`, el nombre del responsable o el nuevo estado según el evento.
5. Que `generarReporte()` conserve el texto exacto del contrato (las pruebas normalizan `\r\n`).
6. Para las pruebas de vencimiento (CP-34 y CP-35) se antedata la solicitud por reflexión sobre el campo `fechaCreacion` (`LocalDate`). Si el refactoring lo mueve o lo renombra, esas pruebas **se omiten** (no fallan) y hay que acordar otra forma de controlar la fecha, por ejemplo inyectar un `Clock`.

## 6. Limitaciones

- Las pruebas se ejecutaron con el lanzador de consola de JUnit 5 y JaCoCo; el entorno de trabajo no tenía acceso a Maven Central, por lo que **la primera corrida con Maven será la del pipeline de GitHub Actions** (`mvn -B test`). Usan solo API de JUnit 5.9/5.10 (`junit-jupiter` del `pom.xml`).
- La prueba de mutación es manual (18 mutantes), no automatizada con una herramienta como PIT.
- Las pruebas RI-01 a RI-07 solo compilan sobre el código refactorizado: se incorporan al repositorio recién después de que el refactoring esté integrado en `main`.
