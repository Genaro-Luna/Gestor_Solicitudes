package solicitudes;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("generarReporte")
class ReporteTest extends PruebaBase {

    private static String esperado(int total, int pend, int rev, int res, int rech, int cerr,
                                   int alta, int media, int baja, int vencidas) {
        return String.join("\n",
                "===== REPORTE DE SOLICITUDES =====",
                "Total: " + total,
                "-- Por estado --",
                "PENDIENTE: " + pend,
                "EN_REVISION: " + rev,
                "RESUELTA: " + res,
                "RECHAZADA: " + rech,
                "CERRADA: " + cerr,
                "-- Por prioridad --",
                "ALTA: " + alta,
                "MEDIA: " + media,
                "BAJA: " + baja,
                "Vencidas: " + vencidas,
                "==================================");
    }

    @Test
    @DisplayName("CP-30 reporte con los datos de prueba: texto idéntico al del contrato")
    void reporteConDatosDePrueba() {
        assertEquals(esperado(3, 2, 1, 0, 0, 0, 1, 1, 1, 0), reporte());
    }

    @Test
    @DisplayName("CP-31 reporte de un gestor vacío: todo en cero")
    void reporteVacio() {
        // Arrange
        gestor = new GestorSolicitudes();
        // Assert
        assertEquals(esperado(0, 0, 0, 0, 0, 0, 0, 0, 0, 0), reporte());
    }

    @Test
    @DisplayName("CP-32 el reporte cuenta cada estado tras mover solicitudes")
    void reporteTrasCambiosDeEstado() {
        // Arrange: se suman 3 solicitudes y se las lleva a RESUELTA, CERRADA y RECHAZADA
        int resuelta = registrarValida("ALTA");
        int cerrada = registrarValida("MEDIA");
        int rechazada = registrarValida("BAJA");
        llevarA(resuelta, "RESUELTA");
        llevarA(cerrada, "CERRADA");
        llevarA(rechazada, "RECHAZADA");
        // Assert: 6 en total; PENDIENTE 2, EN_REVISION 1, RESUELTA 1, RECHAZADA 1, CERRADA 1
        assertEquals(esperado(6, 2, 1, 1, 1, 1, 2, 2, 2, 0), reporte());
    }

    @Test
    @DisplayName("CP-33 una solicitud rechazada por validación no suma al reporte")
    void solicitudInvalidaNoSumaAlReporte() {
        // Act
        gestor.registrarSolicitud("5", "Ana", "Gomez", "sin-arroba", "CERTIFICADO", "ALTA", "Descripcion suficiente");
        // Assert
        assertEquals(esperado(3, 2, 1, 0, 0, 0, 1, 1, 1, 0), reporte());
    }

    /** Valores límite del plazo: ALTA 2 días, MEDIA 5, BAJA 10 (vence si pasa MÁS de ese plazo). */
    @ParameterizedTest(name = "CP-34 prioridad {0} con {1} días de antigüedad: vencidas = {2}")
    @CsvSource({
            "ALTA, 2, 0", "ALTA, 3, 1",
            "MEDIA, 5, 0", "MEDIA, 6, 1",
            "BAJA, 10, 0", "BAJA, 11, 1"
    })
    void plazoDeVencimientoPorPrioridad(String prioridad, int dias, int vencidasEsperadas) {
        // Arrange: gestor vacío con una sola solicitud PENDIENTE, antedatada
        gestor = new GestorSolicitudes();
        int id = registrarValida(prioridad);
        Assumptions.assumeTrue(antedatar(id, dias),
                "No se puede antedatar la solicitud: el campo fechaCreacion cambió tras el refactoring");
        // Act + Assert
        assertTrue(reporte().contains("Vencidas: " + vencidasEsperadas + "\n"),
                "Se esperaba 'Vencidas: " + vencidasEsperadas + "' y el reporte fue:\n" + reporte());
    }

    @Test
    @DisplayName("CP-35 solo vencen las PENDIENTE y EN_REVISION: una RESUELTA antigua no cuenta")
    void soloVencenPendientesYEnRevision() {
        // Arrange: tres solicitudes ALTA de hace 30 días, en PENDIENTE, EN_REVISION y RESUELTA
        gestor = new GestorSolicitudes();
        int pendiente = registrarValida("ALTA");
        int enRevision = registrarValida("ALTA");
        int resuelta = registrarValida("ALTA");
        llevarA(enRevision, "EN_REVISION");
        llevarA(resuelta, "RESUELTA");
        Assumptions.assumeTrue(antedatar(pendiente, 30) && antedatar(enRevision, 30) && antedatar(resuelta, 30),
                "No se puede antedatar la solicitud: el campo fechaCreacion cambió tras el refactoring");
        // Act + Assert
        assertTrue(reporte().contains("Vencidas: 2\n"), reporte());
    }
}
