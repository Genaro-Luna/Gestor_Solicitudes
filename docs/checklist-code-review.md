# Etapa 7 — Checklist de code review

**Responsable:** Akier Aguirrezabala · **Archivo:** `docs/checklist-code-review.md` · **Tag:** `etapa-pruebas`

La revisión de código es una conversación técnica, no una búsqueda de culpables: el objetivo es que el código sea mantenible, seguro y consistente con los acuerdos del equipo. Este documento tiene cuatro partes: (1) el checklist, (2) los criterios de aceptación, (3) su aplicación sobre el código base y (4) el registro de la revisión del Pull Request de refactoring.

## 1. Checklist técnico

**Tipo:** **B** = bloqueante (si no se cumple, el PR no se aprueba) · **R** = recomendado (se registra y se acuerda una acción o una justificación).

### A. Comportamiento y pruebas
| ID | Criterio | Tipo |
|---|---|---|
| CR-01 | El proyecto compila y el pipeline de CI (`mvn -B test`) está en verde. | B |
| CR-02 | Las pruebas del contrato (`etapa-pruebas`) pasan **sin modificarlas**. Si hubo que tocar una prueba, se justifica en el PR. | B |
| CR-03 | Las firmas públicas del contrato (`docs/contrato-comportamiento.md`) se mantienen; si hay una versión nueva, la vieja delega en ella. | B |
| CR-04 | El código nuevo tiene sus propias pruebas (por ejemplo, cada clase extraída con lógica). | R |
| CR-05 | No hay cambios de comportamiento sin documentar: lo que cambia está declarado como mejora (por ejemplo, el id no numérico del menú). | B |

### B. Principios SOLID
| ID | Criterio | Tipo |
|---|---|---|
| CR-06 | **SRP:** cada clase tiene una sola razón de cambio; `GestorSolicitudes` ya no mezcla validación, reglas de estado, almacenamiento, notificación y reporte. | B |
| CR-07 | **OCP:** agregar un tipo, un estado o un canal de notificación no obliga a modificar código existente (enums, estrategias o interfaces en lugar de cadenas de `if`). | R |
| CR-08 | **LSP:** las implementaciones de una interfaz o clase base se pueden usar sin cambiar el comportamiento esperado (no lanzan excepciones inesperadas ni devuelven valores fuera del contrato). | R |
| CR-09 | **ISP:** las interfaces son chicas y específicas; ningún cliente depende de métodos que no usa. | R |
| CR-10 | **DIP:** la lógica de negocio depende de abstracciones (notificador, repositorio), no de `new NotificadorEmail()` ni de `ArrayList`. | B |

### C. Clean Code
| ID | Criterio | Tipo |
|---|---|---|
| CR-11 | Nombres que revelan intención (sin `lst`, `cont`, `s`, `e`, `x`, `er`, `res`). | B |
| CR-12 | Métodos cortos que hacen una sola cosa; ningún método de más de ~20 líneas sin justificación. | R |
| CR-13 | Máximo 3 parámetros por método; más de eso se agrupa en un objeto (Introduce Parameter Object). | R |
| CR-14 | Sin números ni textos mágicos: plazos, longitudes mínimas, tipos, prioridades y estados son constantes o enums. | B |
| CR-15 | Sin código muerto ni código comentado (para eso está Git). | R |
| CR-16 | Los comentarios explican el *por qué*, no repiten el *qué*. | R |
| CR-17 | Manejo de errores explícito: sin `catch` vacíos y sin ocultar fallas; el error informado es coherente en todos los métodos. | B |
| CR-18 | Encapsulamiento: atributos privados, sin exponer colecciones internas (devolver copias o vistas no modificables). | B |

### D. Code smells y refactoring seguro
| ID | Criterio | Tipo |
|---|---|---|
| CR-19 | Cada refactoring resuelve un smell identificado en la Etapa 3 y está mapeado a una técnica (Extract Method, Move Method, Introduce Parameter Object, etc.). | B |
| CR-20 | Hay al menos 4 refactorings documentados con antes/después, entre ellos Extract Method, Move Method (o reubicación de responsabilidad) e Introduce Parameter Object. | B |
| CR-21 | Un refactoring por commit, con mensaje Conventional Commits (`refactor:`), de modo que cada paso se pueda revisar y revertir. | R |
| CR-22 | Se eliminó la duplicación señalada (búsqueda por id, bucles de `buscarPor…`, armado de mensajes). | R |

### E. Seguridad y robustez
| ID | Criterio | Tipo |
|---|---|---|
| CR-23 | Las entradas se validan en un solo lugar y se tratan los `null`. | B |
| CR-24 | No hay datos personales reales, contraseñas ni secretos en el repositorio. | B |
| CR-25 | Una falla de una parte periférica (el envío de la notificación) no deja el sistema en un estado inconsistente. | R |

### F. Repositorio, Git y documentación
| ID | Criterio | Tipo |
|---|---|---|
| CR-26 | El trabajo está en una rama propia (nunca directo en `main`) y entra por Pull Request. | B |
| CR-27 | El PR explica qué cambia, por qué y cómo se probó, e incluye evidencia (salida de pruebas). | R |
| CR-28 | Commits chicos, ordenados y con mensajes Conventional Commits. | R |
| CR-29 | Los tags de la etapa existen y apuntan al commit correcto. | B |
| CR-30 | README y `/docs` actualizados con la estructura nueva. | R |

## 2. Criterios de aceptación técnica

| Resultado | Condición |
|---|---|
| **Aprobado** | Todos los ítems bloqueantes (B) cumplen. Los recomendados (R) que no cumplen tienen una observación registrada con acción o justificación. |
| **Aprobado con cambios** | Cumplen todos los bloqueantes salvo uno o dos de impacto bajo, con corrección acordada y fecha. Se vuelve a revisar solo lo corregido. |
| **Cambios requeridos** | Falla CR-01, CR-02, CR-03 o CR-05 (regresión), o fallan tres o más bloqueantes. No se integra a `main`. |

Cada observación se registra con el formato: **dónde → qué se observa → por qué importa → qué se sugiere → ¿bloqueante?** Se redacta sobre el código, no sobre la persona, y siempre con una alternativa concreta.

## 3. Aplicación sobre el código base (`estado-inicial`)

Se aplicó el checklist al código de `main` (idéntico a `estado-inicial` en `src/` y `pom.xml`). Las observaciones se contrastaron ejecutando el código y con el reporte de cobertura de la Etapa 6. La columna *Smell* remite a la tabla de la Etapa 3 (`docs/code-smells.md`).

### 3.1. Resultado por ítem
| Ítem | Resultado | Evidencia |
|---|---|---|
| CR-01 | ✔ Cumple | Compila; las 108 pruebas del contrato pasan y el CI corre `mvn -B test`. |
| CR-02, CR-03, CR-05 | ✔ Cumple (línea de base) | Las pruebas se escribieron sobre este código y definen el comportamiento a preservar. |
| CR-04 | ✔ Cumple | Cobertura 98,1 % de líneas y 97,0 % de ramas. |
| CR-06 | ✘ No cumple | `GestorSolicitudes` (284 líneas) valida, guarda, aplica reglas de estado, busca, notifica, arma el reporte y carga datos de prueba (smell 9). |
| CR-07 | ✘ No cumple | Agregar el tipo “TITULO” obliga a tocar `registrarSolicitud` y `Main` (smells 3 y 11). |
| CR-08, CR-09 | N/A | No hay jerarquías ni interfaces todavía. |
| CR-10 | ✘ No cumple | `private NotificadorEmail notif = new NotificadorEmail();` (línea 16) y `ArrayList` concreto. |
| CR-11 | ✘ No cumple | `lst`, `cont`, `notif`, `s`, `e`, `x`, `p`, `er`, `res`, `c`. |
| CR-12 | ✘ No cumple | `registrarSolicitud` (~70 líneas), `generarReporte` (~40) y `Main.main` (~75) (smells 6 y 10). |
| CR-13 | ✘ No cumple | `registrarSolicitud` recibe 7 parámetros (smell 1). |
| CR-14 | ✘ No cumple | Estados, tipos y prioridades como `String`; plazos `2`, `5`, `10` y longitud `10` sueltos (smells 3 y 11). |
| CR-15 | ⚠ Observación | Método `borrarSolicitud` comentado “por las dudas” (smell 8). |
| CR-16 | ⚠ Observación | Los comentarios repiten el *qué* (“// validaciones”, “// creo la solicitud”). |
| CR-17 | ✘ No cumple | Tres `catch (Exception ex) { }` vacíos (líneas 87, 121 y 175) y errores informados con `-1`, `false`, `null` y mensajes por consola (smell 5). |
| CR-18 | ✘ No cumple | Atributos públicos en las tres entidades y `listarTodas()` devuelve la lista interna (smell 4). |
| CR-19 a CR-21 | Se evalúan en el PR | Ver la parte 4. |
| CR-22 | ✘ No cumple | La búsqueda por id se repite 4 veces; los tres `buscarPor…` son el mismo bucle; el saludo de la notificación se arma 3 veces (smell 2). |
| CR-23 | ⚠ Observación | Validación presente pero incompleta (observaciones O-03, O-04 y O-05). |
| CR-24 | ✔ Cumple | Datos de prueba sintéticos; no hay secretos. |
| CR-25 | ⚠ Observación | El `catch` vacío evita que una falla de notificación corte el flujo, pero la oculta por completo (CR-17). |
| CR-26 | ✔ Cumple | El trabajo entra por Pull Request (PR n.º 1 de la Etapa 3, rama `mathias/etapa3-docs`). |
| CR-27 | ⚠ No verificado | No se consultó la descripción del PR n.º 1 en GitHub; se revisa junto con el PR de refactoring. |
| CR-28 | ✔ Cumple | Commits con prefijos `feat:` y `docs:`. |
| CR-29 | ✔ Cumple | Existen `estado-inicial` y `etapa-diagnostico`. |
| CR-30 | ⚠ Observación | El README menciona SonarLint/SonarQube en la Etapa 1 pero el repositorio no tiene su configuración ni sus reportes (O-10). |

### 3.2. Observaciones nuevas (no incluidas en la tabla de la Etapa 3)
Estas observaciones surgieron al escribir las pruebas y ejecutar el código. Se verificaron con una corrida real.

| N.º | Ubicación | Observación | Por qué importa | Sugerencia | Bloqueante |
|---|---|---|---|---|---|
| O-01 | `GestorSolicitudes` líneas 147–153 | **Rama inalcanzable.** `PENDIENTE → EN_REVISION` exige responsable, pero asignar un responsable ya pasa la solicitud a EN_REVISION: nunca existe una PENDIENTE con responsable. La condición de la línea 149 y el `ok = true` de la 153 nunca se ejecutan (lo confirma la cobertura). | Código muerto que da una falsa idea de cómo funciona el flujo. | Definir la regla en un solo lugar (por ejemplo, en la transición del estado) y eliminar la rama que no puede ocurrir. | No |
| O-02 | `generarReporte` líneas 237 y 241 | Los últimos `else if` (`CERRADA`, y la prioridad `BAJA`) son siempre verdaderos por invariante. | Ruido; sugiere que podría haber estados o prioridades fuera del conjunto. | Con enums, un `switch` exhaustivo o un conteo por clave elimina estas ramas. | No |
| O-03 | `registrarSolicitud` línea 30 | El email solo se valida con `contains("@")`: `"@"` es aceptado (verificado: devuelve el id 4). | Datos inválidos entran al sistema. | Validar un formato mínimo (texto antes y después de la `@`) en una clase de validación. | No |
| O-04 | `registrarSolicitud` líneas 60–64 | El legajo no se normaliza: `"1001 "` (con un espacio) crea un estudiante distinto de `"1001"` (verificado: `buscarPorLegajo("1001")` devuelve 2 y `buscarPorLegajo("1001 ")` devuelve 1). | Duplica estudiantes y rompe las búsquedas por legajo. | Hacer `trim()` del legajo al registrar y al buscar. | No |
| O-05 | `asignarResponsable` línea 113 | El área no se valida: con `area = null` devuelve `true` y la notificación dice “(null)” (verificado). | Datos incompletos y mensajes incorrectos al estudiante. | Validar el área igual que el nombre, o documentar que es opcional. | No |
| O-06 | `cambiarEstado` línea 139 | Con `nuevoEstado == null` devuelve `false` sin mensaje, mientras los demás errores imprimen un texto. | Manejo de errores inconsistente (CR-17). | Un único mecanismo de error (excepción de dominio o resultado con motivo). | No |
| O-07 | `listarTodas` línea 220 | Devuelve la lista interna: `listarTodas().clear()` vacía el repositorio (verificado: el total pasa de 3 a 0). | Cualquier clase puede saltear las reglas de negocio. | Devolver `List.copyOf(...)` o `Collections.unmodifiableList(...)`. La prueba MJ-02 lo cubre. | **Sí** (CR-18) |
| O-08 | `cargarDatosDePrueba` línea 282 | Asume el id 2 (`asignarResponsable(2, ...)`): si se carga con el repositorio no vacío, falla con “no existe la solicitud 2” (verificado). | Acoplamiento a un valor fijo; frágil para las pruebas. | Usar el id devuelto por `registrarSolicitud`. | No |
| O-09 | `Main` líneas 52 y 60 | `Integer.parseInt` sin protección: un id no numérico cierra el programa con `NumberFormatException` (defecto conocido). | Cierre abrupto para el usuario. | Leer el id en un método auxiliar que reintente. La prueba MJ-01 lo cubre. | No (es una mejora) |
| O-10 | `.github/workflows/ci.yml` | El pipeline compila y corre las pruebas, pero no mide cobertura ni ejecuta análisis estático. | No hay una compuerta de calidad automática más allá de “las pruebas pasan”. | Agregar JaCoCo y publicar el reporte como artefacto; guardar en `/docs` las capturas de SonarLint/SonarQube. | No |

### 3.3. Aspectos positivos
- El contrato de comportamiento está documentado y verificado con pruebas ejecutables.
- Pipeline de CI desde el inicio, ramas por integrante y Pull Requests (GitHub Flow).
- Datos de prueba sintéticos y notificaciones simuladas: no hay datos personales ni dependencias externas.

## 4. Revisión del código de refactoring (rama `refactor/gestor`, tag `etapa-refactoring`)

**Revisado:** commit `ad421e7` (cabeza de `refactor/gestor` y tag `etapa-refactoring`) · **Autor:** Genaro Nahuel Luna · **Revisor:** Akier Aguirrezabala
**Alcance:** 13 archivos, 5 commits `refactor:` sobre `estado-inicial`. La rama todavía no estaba integrada en `main` al momento de la revisión. La descripción del Pull Request en GitHub no se pudo consultar (CR-27).

### 4.1. Procedimiento aplicado
```bash
git fetch origin --tags
git worktree add ../prueba-despues etapa-refactoring
cp -r src/test ../prueba-despues/src/            # pruebas del contrato, sin tocar el código de Genaro
(cd ../prueba-despues && mvn test -Dmejoras=true)
git log --oneline estado-inicial..etapa-refactoring
git diff estado-inicial etapa-refactoring --stat
```
Además se compiló cada commit por separado y se probó el comportamiento con casos de borde fuera del contrato.

### 4.2. Resultado del checklist sobre el refactoring
| Ítem | Resultado | Evidencia |
|---|---|---|
| CR-01 | ✔ Cumple (local) | Compila y las 122 pruebas pasan. El pipeline de GitHub no se pudo consultar. |
| CR-02 | ✔ Cumple | Las 108 pruebas del contrato pasan **sin modificarse** sobre `etapa-refactoring`. |
| CR-03 | ✔ Cumple | Se mantienen las firmas del contrato y el constructor sin argumentos; la firma de 7 parámetros delega en el nuevo `registrarSolicitud(DatosRegistroSolicitud)`. |
| CR-04 | ⚠ Observación | El refactoring no trae pruebas propias del código nuevo (`Notificador`, `Clock`, `GeneradorReporteSolicitudes`). Se cubrieron con RI-01 a RI-07 de la Etapa 6 (F-06). |
| CR-05 | ⚠ **Bloqueante menor** | Cambio de comportamiento no declarado: ahora se aceptan tipo, prioridad y estado con espacios en los extremos (F-01). |
| CR-06 | ⚠ Cumple parcialmente | `GestorSolicitudes` bajó de 284 a 238 líneas: el reporte pasó a `GeneradorReporteSolicitudes` y las reglas de estado y vencimiento a `Solicitud` y `EstadoSolicitud`. Todavía imprime errores por consola y administra estudiantes. |
| CR-07 | ✔ Cumple | `TipoSolicitud`, `Prioridad` (con su plazo) y `EstadoSolicitud` (con sus transiciones): agregar un valor no toca el gestor. |
| CR-08 | ✔ Cumple | RI-01 reemplaza `NotificadorEmail` por un doble en memoria sin cambiar el comportamiento. |
| CR-09 | ✔ Cumple | `Notificador` tiene una única operación. |
| CR-10 | ⚠ Cumple parcialmente | `Notificador` y `Clock` se inyectan por constructor; `GeneradorReporteSolicitudes` se inyecta como clase concreta (no interfaz) y el constructor por defecto sigue armando `new NotificadorEmail()`. Es razonable por compatibilidad. |
| CR-11 | ✔ Cumple | `lst`, `cont`, `notif`, `s`, `x`, `er`… se reemplazaron por nombres que revelan intención. |
| CR-12 | ⚠ Observación | `registrarSolicitud(DatosRegistroSolicitud)` (~40 líneas) y `cambiarEstado` (~38) siguen largos, aunque son una secuencia plana de validaciones. |
| CR-13 | ⚠ Observación | El Parameter Object solo se usa dentro del gestor: `Main` sigue llamando a la firma de 7 parámetros. |
| CR-14 | ✔ Cumple | Plazos en `Prioridad`, longitud mínima como constante. Queda un “10” escrito en el mensaje de error. |
| CR-15 | ⚠ Observación | `Solicitud.estaVencida()` sin argumentos no se usa y además ignora el `Clock` (F-03). `borrarSolicitud` se eliminó. |
| CR-16 | ✔ Cumple | Sin comentarios redundantes. |
| CR-17 | ✔ Cumple | Los `catch` vacíos ahora informan por `System.err` (lo prueba RI-03). Persiste el `return false` silencioso de `cambiarEstado(null)` (O-06). |
| CR-18 | ✔ Cumple, con observación | Atributos privados y `listarTodas()` no modificable (MJ-02, RI-07). Pero los métodos de cambio de `Solicitud` son públicos y saltean las reglas (F-02). |
| CR-19 | ✔ Cumple | Cada refactoring del informe corresponde a un smell de la Etapa 3. |
| CR-20 | ✔ Cumple | Introduce Parameter Object, Move Method, Extract Class, Extract Method y Replace Type Code with Enum. |
| CR-21 | ⚠ Observación | Mensajes Conventional Commits, pero 3 de los 5 commits no compilan solos y dos mezclan varios refactorings (F-07). |
| CR-22 | ✔ Cumple, con observación | Desaparecieron los bucles repetidos (`filtrarSolicitudes`, `obtener`). Aparece duplicación nueva en `desdeTexto` (F-04). |
| CR-23 | ⚠ Observación | Siguen sin resolverse O-03, O-04 y O-05 (verificado con una corrida): email `"@"` aceptado, legajo con espacio duplica estudiante, área nula aceptada (F-05). |
| CR-24 | ✔ Cumple | Sin datos personales ni secretos. |
| CR-25 | ✔ Cumple | Una falla del notificador no corta el flujo y se informa (RI-03). |
| CR-26 | ✔ Cumple | Trabajo en rama propia `refactor/gestor`, no directo en `main`. |
| CR-27 | — No verificado | No se pudo consultar la descripción del PR. |
| CR-28 | ✔ Cumple | Commits con prefijo `refactor:`, ordenados por tema. |
| CR-29 | ✔ Cumple | El tag `etapa-refactoring` apunta a `ad421e7`, la cabeza de la rama. |
| CR-30 | ⚠ Observación | El README solo cambió la URL de clonado; no describe la estructura nueva (enums, `Notificador`, generador de reporte). |

### 4.3. Registro de feedback
| N.º | Archivo / línea | Observación y por qué importa | Sugerencia | Tipo | Estado |
|---|---|---|---|---|---|
| F-01 | `TipoSolicitud`, `Prioridad` y `EstadoSolicitud` → `desdeTexto` (`.trim()`) | **Cambio de comportamiento no declarado.** `registrarSolicitud(..., " certificado ", ...)` pasa de `-1` a devolver un id, y `cambiarEstado(2, " resuelta ")` pasa de `false` a `true` (verificado con una corrida sobre ambos tags). El contrato dice que un tipo o prioridad fuera del conjunto devuelve `-1`. No rompe ninguna prueba, pero es una diferencia observable. | Quitar el `.trim()` de los tres `desdeTexto`, **o** declarar la mejora en `docs/contrato-comportamiento.md` y en la Etapa 5 del informe. | Bloqueante (impacto bajo) | ⏳ A resolver por el autor |
| F-02 | `Solicitud.cambiarEstado(...)`, `Solicitud.asignarResponsable(...)` | `obtener(id)` devuelve la `Solicitud` real y sus métodos de cambio son públicos: `gestor.obtener(1).cambiarEstado(EstadoSolicitud.CERRADA)` deja una PENDIENTE en CERRADA saltando las reglas (verificado), cuando el gestor lo rechazaría. Es el mismo riesgo que O-07, ahora en el objeto y no en la lista. | Que `Solicitud.cambiarEstado` valide con `puedeCambiarA` (lanzando excepción o devolviendo `boolean`), o hacer los mutadores *package-private* (el gestor está en el mismo paquete). | Sugerencia | ⏳ |
| F-03 | `Solicitud` línea 95; getters sin uso | `estaVencida()` sin argumentos no se usa y llama a `LocalDate.now()`, ignorando el `Clock` inyectado: es la dependencia estática que el informe dice haber eliminado. También quedan sin uso `getDescripcion`, `getResponsable`, `getFechaCreacion`, `Estudiante.getNombre/getApellido` y `Responsable.getArea`. | Eliminar `estaVencida()`. Los getters pueden quedar si se consideran parte de la API. | Sugerencia | ⏳ |
| F-04 | Los tres enums → `desdeTexto` | El mismo bloque `try { valueOf(trim().toUpperCase()) } catch` está copiado tres veces: duplicación nueva. | Un método auxiliar genérico, por ejemplo `static <E extends Enum<E>> Optional<E> parsear(Class<E> tipo, String texto)`. | Sugerencia | ⏳ |
| F-05 | `GestorSolicitudes` | Siguen sin resolverse las observaciones O-01 (rama `PENDIENTE → EN_REVISION` con responsable inalcanzable), O-03 (email `"@"`), O-04 (legajo sin `trim`), O-05 (área nula), O-06 (`cambiarEstado(null)` sin mensaje) y O-08 (`cargarDatosDePrueba` asume el id 2). | Si hay tiempo, normalizar el legajo con `trim` y validar el área. El resto puede quedar como deuda declarada en el informe. | Sugerencia | ⏳ |
| F-06 | Carpeta `src/test` | El refactoring no incluye pruebas del código nuevo. | Integrar `InyeccionDependenciasTest` (RI-01 a RI-07, Etapa 6) una vez que el refactoring esté en `main`. | Sugerencia | ⏳ Lo hace Akier |
| F-07 | Commits `3a6fa24`, `77570e5`, `80ccebe` | **Esos tres commits no compilan solos** (71 errores cada uno: `GestorSolicitudes` sigue usando los campos públicos y el constructor viejo hasta `2eabb24`). Compilan `2eabb24` y `ad421e7`. Además `3a6fa24` mezcla enums, encapsulamiento y Move Method, y `80ccebe` mezcla la interfaz `Notificador` con la extracción del generador. Esto impide revertir o hacer `git bisect` sobre un paso intermedio. | No reescribir el historial ya publicado. En las próximas etapas, hacer que cada commit compile y contenga un solo refactoring; al integrar, decidir en equipo entre conservar los commits o usar *Squash and merge*. | Sugerencia | ⏳ |
| F-08 | Informe, Etapa 4.1 (LSP) | El informe menciona “un doble de prueba en memoria para tests”, pero el repositorio no tenía ninguno. | Citar `InyeccionDependenciasTest` (RI-01) como evidencia, o ajustar la redacción. | Sugerencia | ⏳ |

### 4.4. Aspectos positivos
- El contrato se respeta: 0 regresiones en 108 pruebas, con las firmas públicas intactas.
- Se corrigieron los dos defectos de la Etapa 2 (id no numérico en el menú y lista interna expuesta) y las pruebas MJ-01 y MJ-02 lo demuestran.
- `Prioridad` y `EstadoSolicitud` concentran el plazo y las transiciones: es un uso claro de Strategy y State.
- La inyección de `Notificador` y `Clock` con un constructor por defecto mantiene la compatibilidad y permite probar el vencimiento sin reflexión (RI-04).
- Los refactorings están bien elegidos y mapeados a smells concretos; `filtrarSolicitudes` con `Predicate` elimina la duplicación de las búsquedas.

### 4.5. Decisión final
**Resultado: Aprobado con cambios.** Cumple todos los ítems bloqueantes salvo CR-05 (impacto bajo, F-01). Se pide resolver F-01 antes de integrar en `main` (quitar el `trim` o declararlo) y se recomienda atender F-02 y F-03. **Re-revisión:** solo F-01, F-02 y F-03 una vez corregidos. Las demás observaciones son sugerencias que no bloquean.
