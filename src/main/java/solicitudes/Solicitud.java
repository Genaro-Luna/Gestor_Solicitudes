package solicitudes;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Solicitud {

    private static final int LONGITUD_MINIMA_DESCRIPCION = 10;

    private final int id;
    private final Estudiante estudiante;
    private final TipoSolicitud tipo;
    private final Prioridad prioridad;
    private EstadoSolicitud estado;
    private final String descripcion;
    private Responsable responsable;
    private final LocalDate fechaCreacion;

    public Solicitud(int id, Estudiante estudiante, TipoSolicitud tipo, Prioridad prioridad,
                     String descripcion, LocalDate fechaCreacion) {
        this.id = id;
        this.estudiante = estudiante;
        this.tipo = tipo;
        this.prioridad = prioridad;
        this.estado = EstadoSolicitud.PENDIENTE;
        this.descripcion = descripcion.trim();
        this.fechaCreacion = fechaCreacion;
    }

    public static boolean esDescripcionValida(String descripcion) {
        return descripcion != null && descripcion.trim().length() >= LONGITUD_MINIMA_DESCRIPCION;
    }

    public int getId() {
        return id;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public TipoSolicitud getTipo() {
        return tipo;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Responsable getResponsable() {
        return responsable;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public boolean tieneResponsable() {
        return responsable != null;
    }

    public boolean estaFinalizada() {
        return estado.esFinalizada();
    }

    public void asignarResponsable(Responsable nuevoResponsable) {
        this.responsable = nuevoResponsable;
        if (this.estado == EstadoSolicitud.PENDIENTE) {
            this.estado = EstadoSolicitud.EN_REVISION;
        }
    }

    public boolean requiereResponsableParaTransicionarA(EstadoSolicitud nuevoEstado) {
        return this.estado == EstadoSolicitud.PENDIENTE
                && nuevoEstado == EstadoSolicitud.EN_REVISION
                && !tieneResponsable();
    }

    public boolean puedeCambiarA(EstadoSolicitud nuevoEstado) {
        return this.estado.puedeTransicionarA(nuevoEstado);
    }

    public void cambiarEstado(EstadoSolicitud nuevoEstado) {
        this.estado = nuevoEstado;
    }

    public boolean estaVencida() {
        return estaVencida(LocalDate.now());
    }

    public boolean estaVencida(LocalDate fechaReferencia) {
        if (!estado.contabilizaVencimiento()) {
            return false;
        }
        long diasTranscurridos = ChronoUnit.DAYS.between(fechaCreacion, fechaReferencia);
        return diasTranscurridos > prioridad.getDiasPlazoVencimiento();
    }

    @Override
    public String toString() {
        return "#" + id + " [" + tipo + "] " + estado + " - prioridad " + prioridad
                + " - " + estudiante.getNombreCompleto()
                + (responsable != null ? " - resp: " + responsable.getNombre() : "");
    }
}