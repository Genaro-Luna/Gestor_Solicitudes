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
git clone <URL-del-repo>
cd gestion-solicitudes-academicas
mvn compile   # compila
mvn test      # corre las pruebas
```

En IntelliJ: *File > Open* → elegir la carpeta del proyecto (lo detecta como Maven) → ejecutar `solicitudes.Main`.

## Estructura

```
src/main/java/solicitudes/   código fuente
src/test/java/solicitudes/   pruebas
docs/                        contrato de comportamiento, diagnóstico, checklist de code review y evidencias
```

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
