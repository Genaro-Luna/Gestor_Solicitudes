package solicitudes;

public record DatosRegistroSolicitud(
        String legajo,
        String nombre,
        String apellido,
        String email,
        String tipo,
        String prioridad,
        String descripcion
) {
}
