# Sistema de Gestión de Solicitudes Académicas

Trabajo Práctico Integrador — **Metodología de Sistemas II** (TUPaD - UTN).

Sistema de consola que permite registrar solicitudes de estudiantes (certificados, equivalencias, cambios de comisión y reclamos de nota), clasificarlas por tipo y prioridad, asignar un responsable, hacerlas avanzar por sus estados, consultarlas y generar un reporte simple.

El objetivo del trabajo no es sumar funcionalidades sino **diagnosticar y mejorar la calidad interna** del sistema: deuda técnica, code smells, SOLID, Clean Code, refactoring seguro, pruebas y code review.

## Funcionalidades

1. Registrar solicitud (con validación de datos)
2. Asignar responsable
3. Cambiar estado (PENDIENTE → EN_REVISION → RESUELTA → CERRADA, o RECHAZADA)
4. Buscar por estado, por tipo o por legajo
5. Listar todas
6. Generar reporte (totales por estado, por prioridad y solicitudes vencidas)

Las notificaciones al estudiante se simulan por consola. Los datos se guardan en memoria y se cargan datos de prueba sintéticos al iniciar.

## Tecnologías

- Java 17
- Maven
- JUnit 5 (pruebas)
- IntelliJ IDEA
- Git y GitHub

## Instalación y uso

```bash
git clone https://github.com/Genaro-Luna/Gestor_Solicitudes.git
cd Gestor_Solicitudes
mvn compile   # compila
mvn test      # corre las pruebas
```

En IntelliJ: *File > Open* → elegir la carpeta del proyecto (lo detecta como Maven) → ejecutar `solicitudes.Main`.

## Estructura

```
src/main/java/solicitudes/   código fuente
src/test/java/solicitudes/   pruebas JUnit del contrato de comportamiento
docs/                        contrato de comportamiento, diagnóstico, pruebas, checklist de code review y evidencias
```

## Diseño de la versión refactorizada

| Clase | Responsabilidad |
|-------|-----------------|
| `GestorSolicitudes` | Coordina las operaciones: registrar, asignar, cambiar estado, buscar y reportar |
| `Solicitud` | Entidad con su propio comportamiento (transiciones, vencimiento) |
| `Estudiante`, `Responsable` | Entidades encapsuladas |
| `TipoSolicitud`, `Prioridad`, `EstadoSolicitud` | Enums del dominio; `Prioridad` guarda el plazo de vencimiento y `EstadoSolicitud` las transiciones válidas |
| `DatosRegistroSolicitud` | Parameter Object con los datos de alta |
| `Notificador` / `NotificadorEmail` | Interfaz de notificación y su implementación por consola, inyectada en el gestor |
| `GeneradorReporteSolicitudes` | Arma el reporte (conteos por estado, prioridad y vencidas) |
| `Main` | Menú de consola |

`GestorSolicitudes` recibe por constructor el `Notificador`, el generador de reporte y un `Clock`, lo que permite probarlo con dobles y con fechas controladas. El constructor sin argumentos mantiene el comportamiento original.

## Documentación

- [Contrato de comportamiento](docs/contrato-comportamiento.md)
- [Diagnóstico de code smells](docs/code-smells.md)
- [Pruebas y resultados](docs/pruebas-contrato.md)
- [Checklist de code review](docs/checklist-code-review.md)

## Versiones (tags)

| Tag | Contenido |
|-----|-----------|
| `estado-inicial` | Sistema base con los problemas de diseño a diagnosticar |
| `etapa-diagnostico` | Diagnóstico de deuda técnica y code smells |
| `etapa-refactoring` | Refactorings aplicados |
| `etapa-pruebas` | Pruebas y verificación |
| `etapa-docs` | Documentación final |
| `estado-refactorizado` | Versión final mejorada |

## Equipo

- Stefan Dios Mayarin
- Mathias Jesus Flor
- Genaro Nahuel Luna
- Akier Aguirrezabala
