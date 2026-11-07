package solicitudes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("cambiarEstado")
class CambiarEstadoTest extends PruebaBase {

    @ParameterizedTest(name = "CP-16 transición válida {0} -> {1}")
    @CsvSource({
            "PENDIENTE, RECHAZADA",
            "EN_REVISION, RESUELTA",
            "EN_REVISION, RECHAZADA",
            "RESUELTA, CERRADA"
    })
    void transicionesValidas(String origen, String destino) {
        // Arrange
        int id = registrarValida("MEDIA");
        llevarA(id, origen);
        assertEquals(origen, estadoDe(id));
        // Act
        boolean resultado = gestor.cambiarEstado(id, destino);
        // Assert
        assertTrue(resultado);
        assertEquals(destino, estadoDe(id));
    }

    @ParameterizedTest(name = "CP-17 transición inválida {0} -> {1} devuelve false y no cambia nada")
    @MethodSource("transicionesInvalidas")
    void transicionesInvalidas(String origen, String destino) {
        // Arrange
        int id = registrarValida("MEDIA");
        llevarA(id, origen);
        int totalAntes = gestor.listarTodas().size();
        // Act
        boolean resultado = gestor.cambiarEstado(id, destino);
        // Assert
        assertFalse(resultado);
        assertEquals(origen, estadoDe(id));
        assertEquals(totalAntes, gestor.listarTodas().size());
    }

    /** Todos los pares origen/destino que NO están en la Figura 2 del informe. */
    static Stream<Arguments> transicionesInvalidas() {
        List<String> validas = List.of("PENDIENTE>RECHAZADA", "EN_REVISION>RESUELTA",
                "EN_REVISION>RECHAZADA", "RESUELTA>CERRADA");
        List<Arguments> pares = new ArrayList<>();
        for (String origen : ESTADOS) {
            for (String destino : ESTADOS) {
                boolean esValida = validas.contains(origen + ">" + destino);
                boolean esElCasoDelResponsable = origen.equals("PENDIENTE") && destino.equals("EN_REVISION");
                if (!esValida && !esElCasoDelResponsable) {
                    pares.add(Arguments.of(origen, destino));
                }
            }
        }
        return pares.stream();
    }

    @Test
    @DisplayName("CP-18 PENDIENTE -> EN_REVISION sin responsable asignado devuelve false")
    void pasarARevisionSinResponsableEsRechazado() {
        // Arrange: la solicitud 1 está PENDIENTE y sin responsable
        // Act
        boolean resultado = gestor.cambiarEstado(1, "EN_REVISION");
        // Assert
        assertFalse(resultado);
        assertEquals("PENDIENTE", estadoDe(1));
    }

    @Test
    @DisplayName("CP-19 el nuevo estado no distingue mayúsculas")
    void nuevoEstadoSinDistinguirMayusculas() {
        // Arrange: la solicitud 2 está EN_REVISION
        // Act
        boolean resultado = gestor.cambiarEstado(2, "resuelta");
        // Assert
        assertTrue(resultado);
        assertEquals("RESUELTA", estadoDe(2));
    }

    @Test
    @DisplayName("CP-20 solicitud inexistente, estado nulo o desconocido devuelven false")
    void entradasInvalidasDevuelvenFalse() {
        assertFalse(gestor.cambiarEstado(99, "RESUELTA"));
        assertFalse(gestor.cambiarEstado(2, null));
        assertFalse(gestor.cambiarEstado(2, "ARCHIVADA"));
        assertEquals("EN_REVISION", estadoDe(2));
    }

    @Test
    @DisplayName("CP-21 ciclo de vida completo: alta, asignación, resolución y cierre")
    void cicloDeVidaCompleto() {
        // Arrange
        int id = registrarValida("ALTA");
        // Act + Assert, paso a paso
        assertEquals("PENDIENTE", estadoDe(id));
        assertTrue(gestor.asignarResponsable(id, "Marcos Sosa", "Secretaria Academica"));
        assertEquals("EN_REVISION", estadoDe(id));
        assertTrue(gestor.cambiarEstado(id, "RESUELTA"));
        assertEquals("RESUELTA", estadoDe(id));
        assertTrue(gestor.cambiarEstado(id, "CERRADA"));
        assertEquals("CERRADA", estadoDe(id));
    }
}
