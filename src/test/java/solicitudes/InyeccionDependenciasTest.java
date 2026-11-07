package solicitudes;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de lo que habilita el refactoring (Etapas 4 y 5): notificador y reloj inyectables.
 *
 * NO forman parte del contrato: solo compilan sobre estado-refactorizado (usan la interfaz
 * Notificador, el constructor con inyección y GeneradorReporteSolicitudes). Para correr las
 * pruebas del contrato sobre estado-inicial hay que excluir este archivo.
 */
@DisplayName("inyección de dependencias del refactoring (Notificador y Clock)")
class InyeccionDependenciasTest {

    /** Doble de prueba en memoria: guarda lo que se "enviaría" en lugar de imprimirlo. */
    static class NotificadorEnMemoria implements Notificador {
        final List<String[]> enviados = new ArrayList<>();

        @Override
        public void enviar(String destinatario, String asunto, String mensaje) {
            enviados.add(new String[]{destinatario, asunto, mensaje});
        }
    }

    /** Reloj que se puede adelantar para simular el paso de los días sin tocar campos internos. */
    static class RelojMutable extends Clock {
        private Instant ahora = LocalDate.of(2026, 3, 2).atStartOfDay(ZoneOffset.UTC).toInstant();

        void avanzarDias(int dias) {
            ahora = ahora.plusSeconds(dias * 86_400L);
        }

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return ahora;
        }
    }

    private PrintStream outOriginal;
    private PrintStream errOriginal;
    private ByteArrayOutputStream err;

    @BeforeEach
    void silenciarConsola() {
        outOriginal = System.out;
        errOriginal = System.err;
        err = new ByteArrayOutputStream();
        System.setOut(new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(err, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void restaurarConsola() {
        System.setOut(outOriginal);
        System.setErr(errOriginal);
    }

    private static int registrar(GestorSolicitudes gestor, String prioridad) {
        return gestor.registrarSolicitud("2001", "Sofia", "Lopez", "sofia.lopez@mail.com",
                "CAMBIO_COMISION", prioridad, "Pido cambio de comision por horario laboral");
    }

    @Test
    @DisplayName("RI-01 el gestor usa cualquier implementación de Notificador (doble en memoria)")
    void notificadorEnMemoriaRecibeCadaEvento() {
        // Arrange
        NotificadorEnMemoria doble = new NotificadorEnMemoria();
        GestorSolicitudes gestor = new GestorSolicitudes(doble);
        // Act
        int id = registrar(gestor, "ALTA");
        gestor.asignarResponsable(id, "Marcos Sosa", "Bedelia");
        gestor.cambiarEstado(id, "RESUELTA");
        // Assert: un mensaje por evento, siempre al email del estudiante
        assertEquals(3, doble.enviados.size());
        assertEquals("sofia.lopez@mail.com", doble.enviados.get(0)[0]);
        assertEquals("Solicitud registrada", doble.enviados.get(0)[1]);
        assertEquals("Responsable asignado", doble.enviados.get(1)[1]);
        assertTrue(doble.enviados.get(1)[2].contains("Marcos Sosa"));
        assertEquals("Cambio de estado", doble.enviados.get(2)[1]);
        assertTrue(doble.enviados.get(2)[2].contains("RESUELTA"));
    }

    @Test
    @DisplayName("RI-02 las operaciones rechazadas no llaman al notificador")
    void operacionesRechazadasNoNotifican() {
        // Arrange
        NotificadorEnMemoria doble = new NotificadorEnMemoria();
        GestorSolicitudes gestor = new GestorSolicitudes(doble);
        // Act
        gestor.registrarSolicitud("", "Ana", "Gomez", "ana@mail.com", "CERTIFICADO", "ALTA", "Descripcion suficiente");
        gestor.asignarResponsable(99, "Marcos Sosa", "Bedelia");
        gestor.cambiarEstado(99, "CERRADA");
        // Assert
        assertTrue(doble.enviados.isEmpty());
    }

    @Test
    @DisplayName("RI-03 si el notificador falla, la operación se completa y el fallo se informa por System.err")
    void fallaDelNotificadorNoCortaElFlujo() {
        // Arrange: notificador que siempre lanza una excepción
        Notificador roto = (destinatario, asunto, mensaje) -> {
            throw new IllegalStateException("servidor de correo caído");
        };
        GestorSolicitudes gestor = new GestorSolicitudes(roto);
        // Act
        int id = registrar(gestor, "MEDIA");
        boolean asignada = gestor.asignarResponsable(id, "Marcos Sosa", "Bedelia");
        // Assert: la solicitud existe y quedó EN_REVISION, y el error no se ocultó
        assertEquals(1, id);
        assertTrue(asignada);
        assertTrue(gestor.buscarPorEstado("EN_REVISION").contains(gestor.obtener(id)));
        assertTrue(err.toString(StandardCharsets.UTF_8).contains("servidor de correo caído"));
    }

    @ParameterizedTest(name = "RI-04 prioridad {0} con {1} días de antigüedad: vencidas = {2}")
    @CsvSource({
            "ALTA, 2, 0", "ALTA, 3, 1",
            "MEDIA, 5, 0", "MEDIA, 6, 1",
            "BAJA, 10, 0", "BAJA, 11, 1"
    })
    void vencimientoConRelojInyectado(String prioridad, int dias, int vencidasEsperadas) {
        // Arrange: reloj controlado, sin reflexión
        RelojMutable reloj = new RelojMutable();
        GestorSolicitudes gestor = new GestorSolicitudes(new NotificadorEnMemoria(), new GeneradorReporteSolicitudes(), reloj);
        registrar(gestor, prioridad);
        // Act
        reloj.avanzarDias(dias);
        // Assert
        assertTrue(gestor.generarReporte().contains("Vencidas: " + vencidasEsperadas + "\n"), gestor.generarReporte());
    }

    @Test
    @DisplayName("RI-05 una solicitud RESUELTA no vence aunque pasen los días")
    void resueltaNoVence() {
        // Arrange
        RelojMutable reloj = new RelojMutable();
        GestorSolicitudes gestor = new GestorSolicitudes(new NotificadorEnMemoria(), new GeneradorReporteSolicitudes(), reloj);
        int id = registrar(gestor, "ALTA");
        gestor.asignarResponsable(id, "Marcos Sosa", "Bedelia");
        gestor.cambiarEstado(id, "RESUELTA");
        // Act
        reloj.avanzarDias(60);
        // Assert
        assertTrue(gestor.generarReporte().contains("Vencidas: 0\n"));
    }

    @Test
    @DisplayName("RI-06 registrarSolicitud(DatosRegistroSolicitud) equivale a la firma original de 7 parámetros")
    void parameterObjectEquivaleALaFirmaOriginal() {
        // Arrange
        GestorSolicitudes gestor = new GestorSolicitudes(new NotificadorEnMemoria());
        DatosRegistroSolicitud validos = new DatosRegistroSolicitud("3001", "Marta", "Ruiz", "marta@mail.com",
                "CERTIFICADO", "ALTA", "Necesito constancia de examen final");
        DatosRegistroSolicitud invalidos = new DatosRegistroSolicitud("3001", "Marta", "Ruiz", "marta.mail.com",
                "CERTIFICADO", "ALTA", "Necesito constancia de examen final");
        // Act + Assert
        assertEquals(1, gestor.registrarSolicitud(validos));
        assertEquals(-1, gestor.registrarSolicitud(invalidos));
        assertEquals(2, gestor.registrarSolicitud("3001", "Marta", "Ruiz", "marta@mail.com",
                "CERTIFICADO", "ALTA", "Necesito constancia de examen final"));
    }

    @Test
    @DisplayName("RI-07 listarTodas devuelve una vista no modificable")
    void listarTodasEsNoModificable() {
        // Arrange
        GestorSolicitudes gestor = new GestorSolicitudes(new NotificadorEnMemoria());
        registrar(gestor, "BAJA");
        // Act + Assert
        org.junit.jupiter.api.Assertions.assertThrows(UnsupportedOperationException.class,
                () -> gestor.listarTodas().clear());
        assertEquals(1, gestor.listarTodas().size());
    }
}
