package solicitudes;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Prueba de humo de extremo a extremo: se maneja el menú de consola igual que lo haría el usuario. */
@DisplayName("menú de consola (Main)")
class MenuConsolaTest extends PruebaBase {

    private final InputStream entradaOriginal = System.in;

    @AfterEach
    void restaurarEntrada() {
        System.setIn(entradaOriginal);
    }

    private String ejecutarMenu(String... lineas) {
        System.setIn(new ByteArrayInputStream((String.join("\n", lineas) + "\n").getBytes(StandardCharsets.UTF_8)));
        vaciarConsola();
        Main.main(new String[0]);
        return consola().replace("\r\n", "\n");
    }

    @Test
    @DisplayName("CP-40 listar (7), reporte (8) y salir (0) muestran los datos de prueba")
    void listarReporteYSalir() {
        // Act
        String salida = ejecutarMenu("7", "8", "0");
        // Assert
        assertTrue(salida.contains("#1 [CERTIFICADO] PENDIENTE - prioridad ALTA - Ana Gomez"), salida);
        assertTrue(salida.contains("#2 [EQUIVALENCIA] EN_REVISION - prioridad MEDIA - Luis Perez - resp: Laura Diaz"), salida);
        assertTrue(salida.contains("Total: 3"), salida);
        assertTrue(salida.contains("Fin del programa"), salida);
    }

    @Test
    @DisplayName("CP-41 flujo de la evidencia 1.8: pasar a EN_REVISION sin responsable es rechazado y luego se asigna")
    void flujoDeLaEvidencia() {
        // Act: cambiar estado de #1 sin responsable, asignar responsable, resolver, reporte
        String salida = ejecutarMenu(
                "3", "1", "EN_REVISION",
                "2", "1", "Marcos Sosa", "Secretaria Academica",
                "3", "1", "RESUELTA",
                "8", "0");
        // Assert
        assertTrue(salida.contains("primero hay que asignar un responsable"), salida);
        assertTrue(salida.contains("Responsable asignado"), salida);
        assertTrue(salida.contains("Estado actualizado"), salida);
        assertTrue(salida.contains("RESUELTA: 1"), salida);
    }

    @Test
    @DisplayName("CP-42 buscar sin resultados informa que no hay resultados")
    void busquedaSinResultados() {
        // Act
        String salida = ejecutarMenu("4", "CERRADA", "0");
        // Assert
        assertTrue(salida.contains("No hay resultados"), salida);
    }

    @Test
    @DisplayName("CP-43 registrar por el menú (1) con datos válidos informa el id asignado")
    void registrarPorElMenu() {
        // Act
        String salida = ejecutarMenu("1", "3001", "Marta", "Ruiz", "marta.ruiz@mail.com",
                "certificado", "alta", "Necesito constancia de examen final", "7", "0");
        // Assert
        assertTrue(salida.contains("Solicitud registrada con id 4"), salida);
        assertTrue(salida.contains("#4 [CERTIFICADO] PENDIENTE - prioridad ALTA - Marta Ruiz"), salida);
    }

    @Test
    @DisplayName("CP-44 registrar por el menú con datos inválidos no crea la solicitud")
    void registrarInvalidoPorElMenu() {
        // Act: email sin arroba
        String salida = ejecutarMenu("1", "3001", "Marta", "Ruiz", "marta.ruiz.mail.com",
                "CERTIFICADO", "ALTA", "Necesito constancia de examen final", "8", "0");
        // Assert
        assertFalse(salida.contains("Solicitud registrada con id"), salida);
        assertTrue(salida.contains("Total: 3"), salida);
    }

    @Test
    @DisplayName("CP-45 buscar por tipo (5) y por legajo (6) listan las solicitudes encontradas")
    void buscarPorTipoYPorLegajoEnElMenu() {
        // Act
        String salida = ejecutarMenu("5", "EQUIVALENCIA", "6", "1001", "0");
        // Assert
        assertTrue(salida.contains("#2 [EQUIVALENCIA] EN_REVISION"), salida);
        assertTrue(salida.contains("#1 [CERTIFICADO] PENDIENTE"), salida);
        assertTrue(salida.contains("#3 [RECLAMO_NOTA] PENDIENTE"), salida);
    }

    @Test
    @DisplayName("CP-46 una opción inválida o no numérica muestra 'Opcion invalida' y el menú sigue funcionando")
    void opcionInvalidaNoCierraElMenu() {
        // Act
        String salida = ejecutarMenu("99", "abc", "0");
        // Assert
        assertTrue(salida.contains("Opcion invalida"), salida);
        assertTrue(salida.contains("Fin del programa"), salida);
    }
}
