package solicitudes;

import java.util.Optional;

public enum Prioridad {
    ALTA(2),
    MEDIA(5),
    BAJA(10);

    private final int diasPlazoVencimiento;

    Prioridad(int diasPlazoVencimiento) {
        this.diasPlazoVencimiento = diasPlazoVencimiento;
    }

    public int getDiasPlazoVencimiento() {
        return diasPlazoVencimiento;
    }

    public static Optional<Prioridad> desdeTexto(String valor) {
        if (valor == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(Prioridad.valueOf(valor.trim().toUpperCase()));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }
}
