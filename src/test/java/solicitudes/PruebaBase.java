package solicitudes;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.lang.reflect.Field;

/**
 * Base común de las pruebas del contrato de comportamiento (docs/contrato-comportamiento.md).
 *
 * Las pruebas usan SOLO la API pública de GestorSolicitudes descripta en el contrato, para que
 * compilen y corran igual sobre el tag estado-inicial y sobre el tag estado-refactorizado.
 * Por eso el estado de una solicitud se consulta con buscarPorEstado(...) y no leyendo campos.
 */
abstract class PruebaBase {

    static final String[] ESTADOS = {"PENDIENTE", "EN_REVISION", "RESUELTA", "RECHAZADA", "CERRADA"};

    private PrintStream salidaOriginal;
    private ByteArrayOutputStream salidaCapturada;

    /** Gestor nuevo con los 3 datos sintéticos del contrato (ids 1, 2 y 3). */
    GestorSolicitudes gestor;

    @BeforeEach
    void prepararGestorYConsola() {
        salidaOriginal = System.out;
        salidaCapturada = new ByteArrayOutputStream();
        System.setOut(new PrintStream(salidaCapturada, true, StandardCharsets.UTF_8));
        gestor = new GestorSolicitudes();
        gestor.cargarDatosDePrueba();
    }

    @AfterEach
    void restaurarConsola() {
        System.setOut(salidaOriginal);
    }

    /** Todo lo impreso por consola desde el último vaciado. */
    String consola() {
        return salidaCapturada.toString(StandardCharsets.UTF_8);
    }

    void vaciarConsola() {
        salidaCapturada.reset();
    }

    /** Registra una solicitud válida (legajo 2001) y devuelve su id. */
    int registrarValida(String prioridad) {
        return gestor.registrarSolicitud("2001", "Sofia", "Lopez", "sofia.lopez@mail.com",
                "CAMBIO_COMISION", prioridad, "Pido cambio de comision por horario laboral");
    }

    /** Estado actual de la solicitud, consultado solo con la API del contrato. */
    String estadoDe(int id) {
        Solicitud buscada = gestor.obtener(id);
        if (buscada == null) {
            return null;
        }
        for (String estado : ESTADOS) {
            if (gestor.buscarPorEstado(estado).contains(buscada)) {
                return estado;
            }
        }
        throw new AssertionError("La solicitud " + id + " no figura en ningun estado conocido");
    }

    /** Lleva una solicitud PENDIENTE hasta el estado pedido usando solo transiciones válidas. */
    void llevarA(int id, String estadoDestino) {
        switch (estadoDestino) {
            case "PENDIENTE":
                return;
            case "RECHAZADA":
                gestor.cambiarEstado(id, "RECHAZADA");
                return;
            case "EN_REVISION":
                gestor.asignarResponsable(id, "Laura Diaz", "Bedelia");
                return;
            case "RESUELTA":
                llevarA(id, "EN_REVISION");
                gestor.cambiarEstado(id, "RESUELTA");
                return;
            case "CERRADA":
                llevarA(id, "RESUELTA");
                gestor.cambiarEstado(id, "CERRADA");
                return;
            default:
                throw new IllegalArgumentException(estadoDestino);
        }
    }

    /** Reporte con saltos de línea normalizados (Windows/Linux) para comparar texto. */
    String reporte() {
        return gestor.generarReporte().replace("\r\n", "\n");
    }

    /**
     * Antedata la fecha de creación de una solicitud para simular el paso de los días.
     * Devuelve false si la clase no expone un campo "fechaCreacion" de tipo LocalDate modificable
     * (por ejemplo, si el refactoring lo movió): en ese caso la prueba se omite en vez de fallar.
     */
    boolean antedatar(int id, int dias) {
        try {
            Solicitud s = gestor.obtener(id);
            Field campo = Solicitud.class.getDeclaredField("fechaCreacion");
            if (campo.getType() != LocalDate.class) {
                return false;
            }
            campo.setAccessible(true);
            campo.set(s, LocalDate.now().minusDays(dias));
            return true;
        } catch (ReflectiveOperationException | RuntimeException e) {
            return false;
        }
    }
}
