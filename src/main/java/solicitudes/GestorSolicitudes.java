package solicitudes;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Servicio coordinador de la gestion de solicitudes academicas.
 */
public class GestorSolicitudes {

    private final List<Solicitud> solicitudes = new ArrayList<>();
    private final List<Estudiante> estudiantes = new ArrayList<>();
    private int proximoId = 1;
    private final Notificador notificador;
    private final GeneradorReporteSolicitudes generadorReporte;
    private final Clock clock;

    public GestorSolicitudes() {
        this(new NotificadorEmail(), new GeneradorReporteSolicitudes(), Clock.systemDefaultZone());
    }

    public GestorSolicitudes(Notificador notificador) {
        this(notificador, new GeneradorReporteSolicitudes(), Clock.systemDefaultZone());
    }

    public GestorSolicitudes(Notificador notificador, GeneradorReporteSolicitudes generadorReporte, Clock clock) {
        this.notificador = notificador;
        this.generadorReporte = generadorReporte;
        this.clock = clock;
    }

    public int registrarSolicitud(String legajo, String nombre, String apellido, String email,
                                  String tipo, String prioridad, String descripcion) {
        DatosRegistroSolicitud datos = new DatosRegistroSolicitud(
                legajo, nombre, apellido, email, tipo, prioridad, descripcion
        );
        return registrarSolicitud(datos);
    }

    public int registrarSolicitud(DatosRegistroSolicitud datos) {
        if (!validarDatosEstudiante(datos)) {
            return -1;
        }

        Optional<TipoSolicitud> tipoOpt = TipoSolicitud.desdeTexto(datos.tipo());
        if (tipoOpt.isEmpty()) {
            System.out.println("Error: tipo invalido");
            return -1;
        }

        Optional<Prioridad> prioridadOpt = Prioridad.desdeTexto(datos.prioridad());
        if (prioridadOpt.isEmpty()) {
            System.out.println("Error: prioridad invalida");
            return -1;
        }

        if (!Solicitud.esDescripcionValida(datos.descripcion())) {
            System.out.println("Error: la descripcion debe tener al menos 10 caracteres");
            return -1;
        }

        Estudiante estudiante = obtenerOCrearEstudiante(datos);
        Solicitud nuevaSolicitud = new Solicitud(
                proximoId++,
                estudiante,
                tipoOpt.get(),
                prioridadOpt.get(),
                datos.descripcion(),
                LocalDate.now(clock)
        );
        solicitudes.add(nuevaSolicitud);

        notificarEstudiante(
                estudiante,
                "Solicitud registrada",
                "tu solicitud #" + nuevaSolicitud.getId()
                        + " de tipo " + nuevaSolicitud.getTipo()
                        + " fue registrada con prioridad " + nuevaSolicitud.getPrioridad()
        );

        return nuevaSolicitud.getId();
    }

    public boolean asignarResponsable(int id, String nombreResp, String area) {
        Solicitud solicitud = obtener(id);
        if (solicitud == null) {
            System.out.println("Error: no existe la solicitud " + id);
            return false;
        }
        if (solicitud.estaFinalizada()) {
            System.out.println("Error: la solicitud ya esta finalizada");
            return false;
        }
        if (esTextoVacio(nombreResp)) {
            System.out.println("Error: el responsable es obligatorio");
            return false;
        }

        solicitud.asignarResponsable(new Responsable(nombreResp, area));
        notificarEstudiante(
                solicitud.getEstudiante(),
                "Responsable asignado",
                "tu solicitud #" + solicitud.getId()
                        + " fue asignada a " + nombreResp + " (" + area + ")"
        );
        return true;
    }

    public boolean cambiarEstado(int id, String nuevoEstadoTexto) {
        Solicitud solicitud = obtener(id);
        if (solicitud == null) {
            System.out.println("Error: no existe la solicitud " + id);
            return false;
        }
        if (nuevoEstadoTexto == null) {
            return false;
        }
        if (solicitud.estaFinalizada()) {
            System.out.println("Error: la solicitud ya esta finalizada");
            return false;
        }

        String estadoNormalizado = nuevoEstadoTexto.toUpperCase();
        Optional<EstadoSolicitud> nuevoEstadoOpt = EstadoSolicitud.desdeTexto(estadoNormalizado);

        if (nuevoEstadoOpt.isEmpty()) {
            System.out.println("Error: no se puede pasar de " + solicitud.getEstado() + " a " + estadoNormalizado);
            return false;
        }

        EstadoSolicitud nuevoEstado = nuevoEstadoOpt.get();
        if (solicitud.requiereResponsableParaTransicionarA(nuevoEstado)) {
            System.out.println("Error: primero hay que asignar un responsable");
            return false;
        }
        if (!solicitud.puedeCambiarA(nuevoEstado)) {
            System.out.println("Error: no se puede pasar de " + solicitud.getEstado() + " a " + nuevoEstado);
            return false;
        }

        solicitud.cambiarEstado(nuevoEstado);
        notificarEstudiante(
                solicitud.getEstudiante(),
                "Cambio de estado",
                "tu solicitud #" + solicitud.getId() + " ahora esta " + solicitud.getEstado()
        );
        return true;
    }

    public Solicitud obtener(int id) {
        return solicitudes.stream()
                .filter(solicitud -> solicitud.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public List<Solicitud> buscarPorEstado(String estado) {
        return filtrarSolicitudes(solicitud -> solicitud.getEstado().name().equalsIgnoreCase(estado));
    }

    public List<Solicitud> buscarPorTipo(String tipo) {
        return filtrarSolicitudes(solicitud -> solicitud.getTipo().name().equalsIgnoreCase(tipo));
    }

    public List<Solicitud> buscarPorLegajo(String legajo) {
        return filtrarSolicitudes(solicitud -> solicitud.getEstudiante().getLegajo().equals(legajo));
    }

    public List<Solicitud> listarTodas() {
        return Collections.unmodifiableList(solicitudes);
    }

    public String generarReporte() {
        return generadorReporte.generar(solicitudes, LocalDate.now(clock));
    }

    public void cargarDatosDePrueba() {
        registrarSolicitud("1001", "Ana", "Gomez", "ana.gomez@mail.com", "CERTIFICADO", "ALTA",
                "Necesito certificado de alumno regular para el trabajo");
        registrarSolicitud("1002", "Luis", "Perez", "luis.perez@mail.com", "EQUIVALENCIA", "MEDIA",
                "Pido equivalencia de Programacion I cursada en otra facultad");
        registrarSolicitud("1001", "Ana", "Gomez", "ana.gomez@mail.com", "RECLAMO_NOTA", "BAJA",
                "Reclamo por nota del segundo parcial de Matematica");
        asignarResponsable(2, "Laura Diaz", "Bedelia");
    }

    private boolean validarDatosEstudiante(DatosRegistroSolicitud datos) {
        if (esTextoVacio(datos.legajo())) {
            System.out.println("Error: el legajo es obligatorio");
            return false;
        }
        if (esTextoVacio(datos.nombre()) || esTextoVacio(datos.apellido())) {
            System.out.println("Error: nombre y apellido son obligatorios");
            return false;
        }
        if (datos.email() == null || !datos.email().contains("@")) {
            System.out.println("Error: email invalido");
            return false;
        }
        return true;
    }

    private Estudiante obtenerOCrearEstudiante(DatosRegistroSolicitud datos) {
        return estudiantes.stream()
                .filter(estudiante -> estudiante.getLegajo().equals(datos.legajo()))
                .findFirst()
                .orElseGet(() -> {
                    Estudiante nuevo = new Estudiante(
                            datos.legajo(), datos.nombre(), datos.apellido(), datos.email()
                    );
                    estudiantes.add(nuevo);
                    return nuevo;
                });
    }

    private List<Solicitud> filtrarSolicitudes(Predicate<Solicitud> criterio) {
        return solicitudes.stream()
                .filter(criterio)
                .toList();
    }

    private void notificarEstudiante(Estudiante estudiante, String asunto, String detalleEvento) {
        try {
            String mensaje = "Hola " + estudiante.getNombreCompleto() + ", " + detalleEvento;
            notificador.enviar(estudiante.getEmail(), asunto, mensaje);
        } catch (RuntimeException ex) {
            System.err.println("Advertencia: no se pudo enviar la notificacion al estudiante: " + ex.getMessage());
        }
    }

    private boolean esTextoVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }
}