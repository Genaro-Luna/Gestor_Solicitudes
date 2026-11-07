package solicitudes;

import java.util.Optional;

public enum EstadoSolicitud {
    PENDIENTE,
    EN_REVISION,
    RESUELTA,
    RECHAZADA,
    CERRADA;

    public boolean esFinalizada() {
        return this == CERRADA || this == RECHAZADA;
    }

    public boolean contabilizaVencimiento() {
        return this == PENDIENTE || this == EN_REVISION;
    }

    public boolean puedeTransicionarA(EstadoSolicitud destino) {
        if (destino == null) {
            return false;
        }
        return switch (this) {
            case PENDIENTE -> destino == EN_REVISION || destino == RECHAZADA;
            case EN_REVISION -> destino == RESUELTA || destino == RECHAZADA;
            case RESUELTA -> destino == CERRADA;
            case RECHAZADA, CERRADA -> false;
        };
    }

    public static Optional<EstadoSolicitud> desdeTexto(String valor) {
        if (valor == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(EstadoSolicitud.valueOf(valor.trim().toUpperCase()));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }
}
