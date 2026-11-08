package solicitudes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("notificaciones al estudiante (simuladas por consola)")
class NotificacionesTest extends PruebaBase {

    @Test
    @DisplayName("CP-36 el alta notifica al email del estudiante e indica el número de solicitud")
    void altaNotificaAlEstudiante() {
        // Arrange
        vaciarConsola();
        // Act
        int id = gestor.registrarSolicitud("3001", "Marta", "Ruiz", "marta.ruiz@mail.com",
                "CERTIFICADO", "ALTA", "Necesito constancia de examen final");
        // Assert
        assertTrue(consola().contains("marta.ruiz@mail.com"));
        assertTrue(consola().contains("#" + id));
    }

    @Test
    @DisplayName("CP-37 asignar responsable notifica al estudiante e incluye el nombre del responsable")
    void asignacionNotificaAlEstudiante() {
        // Arrange
        vaciarConsola();
        // Act
        gestor.asignarResponsable(1, "Marcos Sosa", "Secretaria Academica");
        // Assert
        assertTrue(consola().contains("ana.gomez@mail.com"));
        assertTrue(consola().contains("Marcos Sosa"));
    }

    @Test
    @DisplayName("CP-38 cambiar de estado notifica al estudiante e incluye el nuevo estado")
    void cambioDeEstadoNotificaAlEstudiante() {
        // Arrange
        vaciarConsola();
        // Act
        gestor.cambiarEstado(2, "RESUELTA");
        // Assert
        assertTrue(consola().contains("luis.perez@mail.com"));
        assertTrue(consola().contains("RESUELTA"));
    }

    @Test
    @DisplayName("CP-39 las operaciones rechazadas no envían notificaciones")
    void operacionesRechazadasNoNotifican() {
        // Arrange
        vaciarConsola();
        // Act: alta inválida, asignación a inexistente y transición inválida
        gestor.registrarSolicitud("", "Ana", "Gomez", "ana@mail.com", "CERTIFICADO", "ALTA", "Descripcion suficiente");
        gestor.asignarResponsable(99, "Marcos Sosa", "Bedelia");
        gestor.cambiarEstado(1, "CERRADA");
        // Assert
        assertFalse(consola().contains("@mail.com"), consola());
    }
}
