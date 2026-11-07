package solicitudes;

import java.util.Optional;

public enum TipoSolicitud {
    CERTIFICADO, EQUIVALENCIA, CAMBIO_COMISION, RECLAMO_NOTA;

    public static Optional<TipoSolicitud> desdeTexto(String valor) {
        if (valor == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(TipoSolicitud.valueOf(valor.trim().toUpperCase()));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }
}
