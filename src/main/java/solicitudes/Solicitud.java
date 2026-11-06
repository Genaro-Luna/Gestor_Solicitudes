package solicitudes;

import java.time.LocalDate;

// clase que guarda los datos de una solicitud
public class Solicitud {

    public int id;
    public Estudiante estudiante;
    public String tipo;        // CERTIFICADO, EQUIVALENCIA, CAMBIO_COMISION, RECLAMO_NOTA
    public String prioridad;   // BAJA, MEDIA, ALTA
    public String estado;      // PENDIENTE, EN_REVISION, RESUELTA, RECHAZADA, CERRADA
    public String descripcion;
    public Responsable responsable;
    public LocalDate fechaCreacion;

    public Solicitud() {
    }

    @Override
    public String toString() {
        return "#" + id + " [" + tipo + "] " + estado + " - prioridad " + prioridad
                + " - " + estudiante.nombre + " " + estudiante.apellido
                + (responsable != null ? " - resp: " + responsable.nombre : "");
    }
}
