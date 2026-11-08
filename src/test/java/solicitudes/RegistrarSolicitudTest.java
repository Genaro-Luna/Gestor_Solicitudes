package solicitudes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("registrarSolicitud")
class RegistrarSolicitudTest extends PruebaBase {

    private static final String DESCRIPCION_OK = "Necesito constancia de examen final";

    @Test
    @DisplayName("CP-01 datos válidos: devuelve el id siguiente y la solicitud queda PENDIENTE")
    void registroValidoDevuelveIdYQuedaPendiente() {
        // Arrange: gestor con 3 solicitudes de prueba (ids 1 a 3)
        // Act
        int id = gestor.registrarSolicitud("3001", "Marta", "Ruiz", "marta.ruiz@mail.com",
                "CERTIFICADO", "ALTA", DESCRIPCION_OK);
        // Assert
        assertEquals(4, id);
        assertNotNull(gestor.obtener(id));
        assertEquals("PENDIENTE", estadoDe(id));
        assertEquals(4, gestor.listarTodas().size());
    }

    @Test
    @DisplayName("CP-02 el id es autoincremental y arranca en 1")
    void idAutoincrementalDesdeUno() {
        // Arrange: gestor vacío, sin datos de prueba
        GestorSolicitudes vacio = new GestorSolicitudes();
        // Act
        int primero = vacio.registrarSolicitud("1", "A", "B", "a@b.com", "RECLAMO_NOTA", "BAJA", DESCRIPCION_OK);
        int segundo = vacio.registrarSolicitud("1", "A", "B", "a@b.com", "RECLAMO_NOTA", "BAJA", DESCRIPCION_OK);
        // Assert
        assertEquals(1, primero);
        assertEquals(2, segundo);
    }

    @ParameterizedTest(name = "CP-03 rechaza {0}")
    @CsvSource(delimiter = '|', value = {
            "legajo vacío            |''|Ana|Gomez|ana@mail.com|CERTIFICADO|ALTA|Descripcion suficiente",
            "legajo solo espacios    |'   '|Ana|Gomez|ana@mail.com|CERTIFICADO|ALTA|Descripcion suficiente",
            "nombre vacío            |9  |''|Gomez|ana@mail.com|CERTIFICADO|ALTA|Descripcion suficiente",
            "apellido vacío          |9  |Ana|''|ana@mail.com|CERTIFICADO|ALTA|Descripcion suficiente",
            "email sin arroba        |9  |Ana|Gomez|ana.mail.com|CERTIFICADO|ALTA|Descripcion suficiente",
            "tipo inexistente        |9  |Ana|Gomez|ana@mail.com|TITULO|ALTA|Descripcion suficiente",
            "prioridad inexistente   |9  |Ana|Gomez|ana@mail.com|CERTIFICADO|URGENTE|Descripcion suficiente",
            "descripción de 9 letras |9  |Ana|Gomez|ana@mail.com|CERTIFICADO|ALTA|123456789",
            "descripción con espacios que no cuentan|9|Ana|Gomez|ana@mail.com|CERTIFICADO|ALTA|'   corta    '"
    })
    void datosInvalidosDevuelvenMenosUnoYNoAgregan(String caso, String legajo, String nombre, String apellido,
                                                   String email, String tipo, String prioridad, String descripcion) {
        // Arrange: gestor con 3 solicitudes
        int antes = gestor.listarTodas().size();
        // Act
        int id = gestor.registrarSolicitud(legajo, nombre, apellido, email, tipo, prioridad, descripcion);
        // Assert
        assertEquals(-1, id);
        assertEquals(antes, gestor.listarTodas().size());
    }

    @Test
    @DisplayName("CP-04 valores nulos en cualquier parámetro devuelven -1 (sin excepción)")
    void parametrosNulosDevuelvenMenosUno() {
        // Arrange
        int antes = gestor.listarTodas().size();
        // Act
        int[] resultados = {
                gestor.registrarSolicitud(null, "Ana", "Gomez", "a@b.com", "CERTIFICADO", "ALTA", DESCRIPCION_OK),
                gestor.registrarSolicitud("9", null, "Gomez", "a@b.com", "CERTIFICADO", "ALTA", DESCRIPCION_OK),
                gestor.registrarSolicitud("9", "Ana", null, "a@b.com", "CERTIFICADO", "ALTA", DESCRIPCION_OK),
                gestor.registrarSolicitud("9", "Ana", "Gomez", null, "CERTIFICADO", "ALTA", DESCRIPCION_OK),
                gestor.registrarSolicitud("9", "Ana", "Gomez", "a@b.com", null, "ALTA", DESCRIPCION_OK),
                gestor.registrarSolicitud("9", "Ana", "Gomez", "a@b.com", "CERTIFICADO", null, DESCRIPCION_OK),
                gestor.registrarSolicitud("9", "Ana", "Gomez", "a@b.com", "CERTIFICADO", "ALTA", null)
        };
        // Assert
        for (int resultado : resultados) {
            assertEquals(-1, resultado);
        }
        assertEquals(antes, gestor.listarTodas().size());
    }

    @ParameterizedTest(name = "CP-05 tipo \"{0}\" válido (sin distinguir mayúsculas)")
    @ValueSource(strings = {"CERTIFICADO", "EQUIVALENCIA", "CAMBIO_COMISION", "RECLAMO_NOTA", "certificado", "Reclamo_Nota"})
    void tiposValidos(String tipo) {
        // Act
        int id = gestor.registrarSolicitud("4001", "Ana", "Gomez", "ana@mail.com", tipo, "MEDIA", DESCRIPCION_OK);
        // Assert
        assertTrue(id > 0);
        assertTrue(gestor.buscarPorTipo(tipo).contains(gestor.obtener(id)));
    }

    @ParameterizedTest(name = "CP-06 prioridad \"{0}\" válida (sin distinguir mayúsculas)")
    @ValueSource(strings = {"BAJA", "MEDIA", "ALTA", "baja", "Alta"})
    void prioridadesValidas(String prioridad) {
        // Act
        int id = gestor.registrarSolicitud("4002", "Ana", "Gomez", "ana@mail.com", "CERTIFICADO", prioridad, DESCRIPCION_OK);
        // Assert
        assertTrue(id > 0);
    }

    @Test
    @DisplayName("CP-07 descripción de exactamente 10 caracteres es válida (valor límite)")
    void descripcionDeDiezCaracteresEsValida() {
        // Act
        int id = gestor.registrarSolicitud("4003", "Ana", "Gomez", "ana@mail.com", "CERTIFICADO", "BAJA", "1234567890");
        // Assert
        assertEquals(4, id);
    }

    @Test
    @DisplayName("CP-08 si el legajo ya existe se reutiliza el estudiante (conserva su email original)")
    void legajoExistenteReutilizaEstudiante() {
        // Arrange: el legajo 1001 (Ana Gomez, ana.gomez@mail.com) ya existe en los datos de prueba
        vaciarConsola();
        // Act: se registra otra solicitud con el mismo legajo pero otro email
        int id = gestor.registrarSolicitud("1001", "Ana", "Gomez", "otro@mail.com",
                "CAMBIO_COMISION", "MEDIA", DESCRIPCION_OK);
        // Assert: se agrega a las del legajo y la notificación va al email original
        assertEquals(3, gestor.buscarPorLegajo("1001").size());
        assertTrue(consola().contains("ana.gomez@mail.com"));
        assertFalse(consola().contains("otro@mail.com"));
        assertTrue(id > 0);
    }

    @ParameterizedTest(name = "CP-09 legajo \"{0}\" rechazado")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void legajoVacioONuloEsRechazado(String legajo) {
        // Act
        int id = gestor.registrarSolicitud(legajo, "Ana", "Gomez", "ana@mail.com", "CERTIFICADO", "ALTA", DESCRIPCION_OK);
        // Assert
        assertEquals(-1, id);
    }
}
