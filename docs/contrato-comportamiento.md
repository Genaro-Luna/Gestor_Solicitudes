# Contrato de comportamiento externo — `GestorSolicitudes`

Este documento fija **qué tiene que seguir haciendo el sistema** después del refactoring.
Es la referencia común para:

- **Refactoring (Etapas 4 y 5):** se puede cambiar todo lo interno (clases nuevas, enums, interfaces, nombres privados), pero estos métodos públicos tienen que seguir existiendo con la misma firma y el mismo resultado. Si se agrega una versión nueva (por ejemplo con Parameter Object), la firma vieja se mantiene delegando en la nueva.
- **Pruebas (Etapa 6):** los tests se escriben contra estos métodos, así corren igual sobre `estado-inicial` y sobre `estado-refactorizado`.

## Datos de prueba (`cargarDatosDePrueba()`)

| Id | Legajo | Estudiante | Tipo | Prioridad | Estado inicial | Responsable |
|----|--------|-----------|------|-----------|----------------|-------------|
| 1 | 1001 | Ana Gomez | CERTIFICADO | ALTA | PENDIENTE | — |
| 2 | 1002 | Luis Perez | EQUIVALENCIA | MEDIA | EN_REVISION | Laura Diaz (Bedelia) |
| 3 | 1001 | Ana Gomez | RECLAMO_NOTA | BAJA | PENDIENTE | — |

## Métodos públicos y reglas

| Método | Resultado esperado |
|--------|--------------------|
| `int registrarSolicitud(legajo, nombre, apellido, email, tipo, prioridad, descripcion)` | Devuelve el id (autoincremental desde 1). Devuelve **-1** si: legajo vacío, nombre o apellido vacíos, email sin `@`, tipo fuera de {CERTIFICADO, EQUIVALENCIA, CAMBIO_COMISION, RECLAMO_NOTA}, prioridad fuera de {BAJA, MEDIA, ALTA}, descripción con menos de 10 caracteres (sin contar espacios de los extremos). Tipo y prioridad no distinguen mayúsculas. La solicitud nueva queda en **PENDIENTE**. Si el legajo ya existe, se reutiliza el estudiante. |
| `boolean asignarResponsable(id, nombreResponsable, area)` | **false** si la solicitud no existe, está CERRADA o RECHAZADA, o el responsable está vacío. Si estaba PENDIENTE pasa a **EN_REVISION**. |
| `boolean cambiarEstado(id, nuevoEstado)` | Transiciones válidas: PENDIENTE→EN_REVISION (solo con responsable asignado), PENDIENTE→RECHAZADA, EN_REVISION→RESUELTA, EN_REVISION→RECHAZADA, RESUELTA→CERRADA. Cualquier otra devuelve **false** y no cambia nada. No distingue mayúsculas. |
| `Solicitud obtener(id)` | La solicitud, o `null` si no existe. |
| `List<Solicitud> buscarPorEstado(estado)` / `buscarPorTipo(tipo)` | Lista filtrada (sin distinguir mayúsculas). Lista vacía si no hay resultados. |
| `List<Solicitud> buscarPorLegajo(legajo)` | Solicitudes de ese legajo. |
| `List<Solicitud> listarTodas()` | Todas, en orden de alta. |
| `String generarReporte()` | Texto con el formato de abajo. Una solicitud PENDIENTE o EN_REVISION está **vencida** si pasaron más días que su plazo: ALTA 2, MEDIA 5, BAJA 10. |

## Formato del reporte (con los datos de prueba)

```
===== REPORTE DE SOLICITUDES =====
Total: 3
-- Por estado --
PENDIENTE: 2
EN_REVISION: 1
RESUELTA: 0
RECHAZADA: 0
CERRADA: 0
-- Por prioridad --
ALTA: 1
MEDIA: 1
BAJA: 1
Vencidas: 0
==================================
```

## Defecto externo conocido (versión inicial)

En el menú de consola, si en las opciones 2 o 3 se ingresa un id no numérico, el programa termina con `NumberFormatException`. Se documenta en la Etapa 2; corregirlo es una mejora de comportamiento, no una regresión. **Corregido en `estado-refactorizado`:** el menú informa que el id debe ser numérico y continúa.

## Cambios de comportamiento declarados (versión refactorizada)

| Cambio | Antes (`estado-inicial`) | Después (`estado-refactorizado`) | Origen |
|--------|--------------------------|----------------------------------|--------|
| Espacios en los extremos de tipo, prioridad y estado | `" certificado "` devolvía -1 y `" resuelta "` devolvía false | Se ignoran los espacios: se aceptan como `CERTIFICADO` y `RESUELTA` | Observación F-01 del code review (Etapa 7). Se acepta como mejora: no rompe ninguna prueba del contrato y hace la entrada más tolerante. |
| `listarTodas()` | Devolvía la lista interna, modificable desde afuera | Devuelve una vista de solo lectura | Defecto de encapsulamiento de la Etapa 2 (prueba MJ-02) |
