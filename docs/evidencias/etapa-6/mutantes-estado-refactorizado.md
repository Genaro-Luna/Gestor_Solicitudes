# Prueba de mutación manual (estado-refactorizado)

Se inyectó una falla a la vez en el código refactorizado (copia descartable) y se corrió toda la suite con `-Dmejoras=true` (122 pruebas). Un mutante está **muerto** si al menos una prueba falla.

| Mutante | Falla inyectada | Resultado | Pruebas que fallan | Casos que lo detectan |
|---|---|---|---|---|
| R1 | Plazo de Prioridad ALTA de 2 a 3 días | MUERTO | 2 | CP-34, RI-04 |
| R2 | Vencida con >= en lugar de > | MUERTO | 6 | CP-34, RI-04 |
| R3 | Se permite RESUELTA -> RECHAZADA | MUERTO | 1 | CP-17 |
| R4 | RECHAZADA deja de considerarse finalizada | MUERTO | 1 | CP-15 |
| R5 | Las RESUELTAS también vencen | MUERTO | 2 | CP-35, RI-05 |
| R6 | Descripción mínima de 10 a 9 caracteres | MUERTO | 1 | CP-03 |
| R7 | Asignar responsable ya no pasa a EN_REVISION | MUERTO | 38 | CP-10, CP-11, CP-12, CP-15, CP-16, CP-17, CP-19, CP-20, CP-21, CP-23, CP-26, CP-29, CP-30, CP-32, CP-33, CP-35, CP-38, CP-40, CP-41, CP-45, RI-01, RI-03, RI-05 |
| R8 | listarTodas devuelve la lista interna (defecto original) | MUERTO | 2 | MJ-02, RI-07 |
| R9 | Se oculta la falla del notificador (catch sin informar) | MUERTO | 1 | RI-03 |
| R10 | El reporte no cuenta ninguna vencida | MUERTO | 7 | CP-34, CP-35, RI-04 |
| R11 | buscarPorLegajo invertido | MUERTO | 6 | CP-08, CP-25, CP-26, CP-45 |
| R12 | Nunca se reutiliza el estudiante existente | MUERTO | 1 | CP-08 |
| R13 | Se quita la regla del responsable para pasar a EN_REVISION | MUERTO | 2 | CP-18, CP-41 |
| R14 | El alta ignora el reloj inyectado (usa LocalDate.now()) | MUERTO | 3 | RI-04 |
| R15 | Se pierde la normalización a mayúsculas de buscarPorEstado | MUERTO | 2 | CP-23 |
| R16 | El id deja de ser autoincremental de a 1 | MUERTO | 29 | CP-01, CP-02, CP-07, CP-10, CP-11, CP-14, CP-18, CP-19, CP-20, CP-22, CP-23, CP-26, CP-27, CP-29, CP-30, CP-32, CP-33, CP-37, CP-38, CP-40, CP-41, CP-43, CP-45, RI-03, RI-06 |

**Resultado: 16 de 16 mutantes detectados.** R9 y R14 (la falla del notificador y el reloj inyectado) solo los detectan las pruebas RI, que aprovechan lo que habilitó el refactoring.

Aclaración: verificación manual con mutantes elegidos a mano, no una herramienta como PIT.
