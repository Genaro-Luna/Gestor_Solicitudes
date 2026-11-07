package solicitudes;

import java.time.LocalDate;
import java.util.List;

public class GeneradorReporteSolicitudes {

    public String generar(List<Solicitud> solicitudes, LocalDate fechaReferencia) {
        long pendientes = contarPorEstado(solicitudes, EstadoSolicitud.PENDIENTE);
        long enRevision = contarPorEstado(solicitudes, EstadoSolicitud.EN_REVISION);
        long resueltas = contarPorEstado(solicitudes, EstadoSolicitud.RESUELTA);
        long rechazadas = contarPorEstado(solicitudes, EstadoSolicitud.RECHAZADA);
        long cerradas = contarPorEstado(solicitudes, EstadoSolicitud.CERRADA);

        long altas = contarPorPrioridad(solicitudes, Prioridad.ALTA);
        long medias = contarPorPrioridad(solicitudes, Prioridad.MEDIA);
        long bajas = contarPorPrioridad(solicitudes, Prioridad.BAJA);

        long vencidas = solicitudes.stream()
                .filter(solicitud -> solicitud.estaVencida(fechaReferencia))
                .count();

        StringBuilder reporte = new StringBuilder();
        reporte.append("===== REPORTE DE SOLICITUDES =====\n");
        reporte.append("Total: ").append(solicitudes.size()).append("\n");
        reporte.append("-- Por estado --\n");
        reporte.append("PENDIENTE: ").append(pendientes).append("\n");
        reporte.append("EN_REVISION: ").append(enRevision).append("\n");
        reporte.append("RESUELTA: ").append(resueltas).append("\n");
        reporte.append("RECHAZADA: ").append(rechazadas).append("\n");
        reporte.append("CERRADA: ").append(cerradas).append("\n");
        reporte.append("-- Por prioridad --\n");
        reporte.append("ALTA: ").append(altas).append("\n");
        reporte.append("MEDIA: ").append(medias).append("\n");
        reporte.append("BAJA: ").append(bajas).append("\n");
        reporte.append("Vencidas: ").append(vencidas).append("\n");
        reporte.append("==================================");
        return reporte.toString();
    }

    private long contarPorEstado(List<Solicitud> solicitudes, EstadoSolicitud estado) {
        return solicitudes.stream()
                .filter(solicitud -> solicitud.getEstado() == estado)
                .count();
    }

    private long contarPorPrioridad(List<Solicitud> solicitudes, Prioridad prioridad) {
        return solicitudes.stream()
                .filter(solicitud -> solicitud.getPrioridad() == prioridad)
                .count();
    }
}