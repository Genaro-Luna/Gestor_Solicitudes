package solicitudes;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Pruebas de MEJORA (no del contrato): documentan los dos defectos de calidad detectados en la
 * Etapa 2 que NO forman parte del comportamiento a preservar. Se espera que FALLEN sobre
 * estado-inicial y PASEN sobre estado-refactorizado si el refactoring los corrigió.
 *
 * Se ejecutan solo con:  mvn test -Dmejoras=true
 * (por defecto quedan omitidas para que el pipeline compruebe únicamente el contrato).
 */
@EnabledIfSystemProperty(named = "mejoras", matches = "true")
@DisplayName("mejoras esperadas (defectos de la Etapa 2)")
class MejorasEsperadasTest extends PruebaBase {

    private final InputStream entradaOriginal = System.in;

    @AfterEach
    void restaurarEntrada() {
        System.setIn(entradaOriginal);
    }

    @Test
    @DisplayName("MJ-01 un id no numérico en el menú no debe cerrar el programa con excepción")
    void idNoNumericoNoTerminaConExcepcion() {
        // Arrange: opción 3 (cambiar estado), id "abc", luego salir
        System.setIn(new ByteArrayInputStream("3\nabc\n0\n".getBytes(StandardCharsets.UTF_8)));
        // Act + Assert
        assertDoesNotThrow(() -> Main.main(new String[0]));
    }

    @Test
    @DisplayName("MJ-02 modificar la lista devuelta por listarTodas no debe alterar el repositorio")
    void listarTodasNoExponeLaListaInterna() {
        // Arrange
        List<Solicitud> devuelta = gestor.listarTodas();
        // Act: intento vaciar la lista que recibí (falla si es de solo lectura; no importa)
        try {
            devuelta.clear();
        } catch (UnsupportedOperationException esperado) {
            // vista no modificable: protege el estado interno
        }
        // Assert
        assertEquals(3, gestor.listarTodas().size());
    }
}
