# Prueba de mutación manual (estado-inicial)

Se inyectó una falla a la vez en `GestorSolicitudes.java` (copia descartable) y se corrió toda la suite. Un mutante está **muerto** si al menos una prueba falla (la suite detecta la regresión).

| Mutante | Falla inyectada | Resultado | Pruebas que fallan | Casos que lo detectan |
|---|---|---|---|---|
| M1 | Plazo de prioridad ALTA de 2 a 3 días | MUERTO | 1 | CP-34 |
| M2 | Vencida con >= en lugar de > | MUERTO | 3 | CP-34 |
| M3 | Longitud mínima de descripción 10 -> 9 | MUERTO | 1 | CP-03 |
| M4 | RESUELTA pasa a RECHAZADA en vez de CERRADA | MUERTO | 10 | CP-15, CP-16, CP-17, CP-21, CP-32 |
| M5 | Asignar responsable ya no pasa PENDIENTE a EN_REVISION | MUERTO | 35 | CP-10, CP-11, CP-12, CP-15, CP-16, CP-17, CP-19, CP-20, CP-21, CP-23, CP-26, CP-29, CP-30, CP-32, CP-33, CP-35, CP-38, CP-40, CP-41, CP-45 |
| M6 | Email válido solo si tiene al menos 3 caracteres (ya no exige @) | MUERTO | 3 | CP-03, CP-33, CP-44 |
| M7 | buscarPorTipo distingue mayúsculas | MUERTO | 3 | CP-05, CP-24 |
| M8 | El id salta de a 2 | MUERTO | 19 | CP-01, CP-02, CP-07, CP-11, CP-19, CP-20, CP-23, CP-26, CP-27, CP-29, CP-30, CP-32, CP-33, CP-38, CP-40, CP-43, CP-45 |
| M9 | El reporte cuenta doble las RECHAZADAS | MUERTO | 1 | CP-32 |
| M10 | Se permite asignar responsable a solicitudes CERRADAS/RECHAZADAS | MUERTO | 2 | CP-15 |
| M11 | La notificación de cambio de estado va a otro email | MUERTO | 1 | CP-38 |
| M12 | Siempre se crea un estudiante nuevo (no se reutiliza el existente) | MUERTO | 1 | CP-08 |
| M13 | Se permite rechazar desde RESUELTA | MUERTO | 1 | CP-17 |
| M14 | Permitir pasar a EN_REVISION sin responsable (se quita la validación) | MUERTO | 2 | CP-18, CP-41 |
| M15 | buscarPorLegajo devuelve todas las solicitudes | MUERTO | 5 | CP-08, CP-25, CP-26 |
| M16 | El reporte omite el total de solicitudes | MUERTO | 5 | CP-30, CP-32, CP-33, CP-40, CP-44 |
| M17 | Plazo MEDIA de 5 a 6 días | MUERTO | 1 | CP-34 |
| M18 | Las solicitudes RESUELTAS también pueden vencer | MUERTO | 1 | CP-35 |

**Resultado: 18 de 18 mutantes detectados (100 %).** Los casos CP-xx corresponden a la tabla de `docs/pruebas-contrato.md`.

Aclaración: es una verificación manual con 18 mutantes elegidos a mano (no una herramienta como PIT); sirve para comprobar que las pruebas son sensibles a regresiones reales.
