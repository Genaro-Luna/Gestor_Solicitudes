package solicitudes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("obtener, buscarPor... y listarTodas")
class BusquedasTest extends PruebaBase {

    @Test
    @DisplayName("CP-22 obtener devuelve la solicitud existente y null si no existe")
    void obtenerExistenteYNoExistente() {
        assertNotNull(gestor.obtener(1));
        assertNotNull(gestor.obtener(3));
        assertNull(gestor.obtener(99));
        assertNull(gestor.obtener(0));
        assertNull(gestor.obtener(-5));
    }

    @ParameterizedTest(name = "CP-23 buscarPorEstado(\"{0}\") devuelve {1} solicitudes")
    @CsvSource({
            "PENDIENTE, 2",
            "pendiente, 2",
            "En_Revision, 1",
            "RESUELTA, 0",
            "RECHAZADA, 0",
            "CERRADA, 0",
            "INEXISTENTE, 0"
    })
    void buscarPorEstado(String estado, int esperadas) {
        // Act
        List<Solicitud> resultado = gestor.buscarPorEstado(estado);
        // Assert
        assertEquals(esperadas, resultado.size());
    }

    @ParameterizedTest(name = "CP-24 buscarPorTipo(\"{0}\") devuelve {1} solicitudes")
    @CsvSource({
            "CERTIFICADO, 1",
            "equivalencia, 1",
            "RECLAMO_NOTA, 1",
            "CAMBIO_COMISION, 0",
            "TITULO, 0"
    })
    void buscarPorTipo(String tipo, int esperadas) {
        assertEquals(esperadas, gestor.buscarPorTipo(tipo).size());
    }

    @ParameterizedTest(name = "CP-25 buscarPorLegajo(\"{0}\") devuelve {1} solicitudes")
    @CsvSource({
            "1001, 2",
            "1002, 1",
            "9999, 0"
    })
    void buscarPorLegajo(String legajo, int esperadas) {
        assertEquals(esperadas, gestor.buscarPorLegajo(legajo).size());
    }

    @Test
    @DisplayName("CP-26 las búsquedas respetan el orden de alta")
    void busquedasEnOrdenDeAlta() {
        // Act
        List<Solicitud> pendientes = gestor.buscarPorEstado("PENDIENTE");
        List<Solicitud> deAna = gestor.buscarPorLegajo("1001");
        // Assert
        assertEquals(List.of(gestor.obtener(1), gestor.obtener(3)), pendientes);
        assertEquals(List.of(gestor.obtener(1), gestor.obtener(3)), deAna);
    }

    @Test
    @DisplayName("CP-27 listarTodas devuelve todas en orden de alta")
    void listarTodasEnOrdenDeAlta() {
        // Act
        List<Solicitud> todas = gestor.listarTodas();
        // Assert
        assertEquals(3, todas.size());
        assertSame(gestor.obtener(1), todas.get(0));
        assertSame(gestor.obtener(2), todas.get(1));
        assertSame(gestor.obtener(3), todas.get(2));
    }

    @Test
    @DisplayName("CP-28 un gestor sin datos devuelve listas vacías")
    void gestorVacioDevuelveListasVacias() {
        // Arrange
        GestorSolicitudes vacio = new GestorSolicitudes();
        // Assert
        assertTrue(vacio.listarTodas().isEmpty());
        assertTrue(vacio.buscarPorEstado("PENDIENTE").isEmpty());
        assertTrue(vacio.buscarPorTipo("CERTIFICADO").isEmpty());
        assertTrue(vacio.buscarPorLegajo("1001").isEmpty());
    }

    @Test
    @DisplayName("CP-29 cargarDatosDePrueba deja las 3 solicitudes del contrato (la 2 en revisión)")
    void datosDePruebaSegunContrato() {
        assertEquals(3, gestor.listarTodas().size());
        assertEquals("PENDIENTE", estadoDe(1));
        assertEquals("EN_REVISION", estadoDe(2));
        assertEquals("PENDIENTE", estadoDe(3));
    }
}
