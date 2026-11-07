package solicitudes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("asignarResponsable")
class AsignarResponsableTest extends PruebaBase {

    @Test
    @DisplayName("CP-10 asignar a una solicitud PENDIENTE devuelve true y la pasa a EN_REVISION")
    void asignarEnPendientePasaAEnRevision() {
        // Arrange: la solicitud 1 está PENDIENTE
        assertEquals("PENDIENTE", estadoDe(1));
        // Act
        boolean resultado = gestor.asignarResponsable(1, "Marcos Sosa", "Secretaria Academica");
        // Assert
        assertTrue(resultado);
        assertEquals("EN_REVISION", estadoDe(1));
    }

    @Test
    @DisplayName("CP-11 reasignar en EN_REVISION devuelve true y mantiene el estado")
    void reasignarEnRevisionMantieneEstado() {
        // Arrange: la solicitud 2 ya está EN_REVISION con responsable
        assertEquals("EN_REVISION", estadoDe(2));
        // Act
        boolean resultado = gestor.asignarResponsable(2, "Marcos Sosa", "Secretaria Academica");
        // Assert
        assertTrue(resultado);
        assertEquals("EN_REVISION", estadoDe(2));
    }

    @Test
    @DisplayName("CP-12 en RESUELTA permite asignar y no cambia el estado")
    void asignarEnResueltaMantieneEstado() {
        // Arrange
        int id = registrarValida("MEDIA");
        llevarA(id, "RESUELTA");
        // Act
        boolean resultado = gestor.asignarResponsable(id, "Marcos Sosa", "Departamento");
        // Assert
        assertTrue(resultado);
        assertEquals("RESUELTA", estadoDe(id));
    }

    @Test
    @DisplayName("CP-13 solicitud inexistente devuelve false")
    void solicitudInexistenteDevuelveFalse() {
        assertFalse(gestor.asignarResponsable(99, "Marcos Sosa", "Bedelia"));
        assertFalse(gestor.asignarResponsable(0, "Marcos Sosa", "Bedelia"));
        assertFalse(gestor.asignarResponsable(-1, "Marcos Sosa", "Bedelia"));
    }

    @ParameterizedTest(name = "CP-14 responsable \"{0}\" devuelve false y no cambia el estado")
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void responsableVacioDevuelveFalse(String responsable) {
        // Arrange: la solicitud 1 está PENDIENTE
        // Act
        boolean resultado = gestor.asignarResponsable(1, responsable, "Bedelia");
        // Assert
        assertFalse(resultado);
        assertEquals("PENDIENTE", estadoDe(1));
    }

    @ParameterizedTest(name = "CP-15 solicitud {0} no admite asignación")
    @ValueSource(strings = {"CERRADA", "RECHAZADA"})
    void solicitudFinalizadaDevuelveFalse(String estadoFinal) {
        // Arrange
        int id = registrarValida("BAJA");
        llevarA(id, estadoFinal);
        // Act
        boolean resultado = gestor.asignarResponsable(id, "Marcos Sosa", "Bedelia");
        // Assert
        assertFalse(resultado);
        assertEquals(estadoFinal, estadoDe(id));
    }
}
