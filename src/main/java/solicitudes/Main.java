package solicitudes;

import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        GestorSolicitudes gestor = new GestorSolicitudes();
        gestor.cargarDatosDePrueba();

        int opcion = -1;
        while (opcion != 0) {
            mostrarMenu();
            opcion = leerEntero(sc, -1);
            ejecutarOpcion(opcion, sc, gestor);
        }
        System.out.println("Fin del programa");
    }

    private static void ejecutarOpcion(int opcion, Scanner sc, GestorSolicitudes gestor) {
        switch (opcion) {
            case 1 -> opcionRegistrar(sc, gestor);
            case 2 -> opcionAsignarResponsable(sc, gestor);
            case 3 -> opcionCambiarEstado(sc, gestor);
            case 4 -> opcionBuscarPorEstado(sc, gestor);
            case 5 -> opcionBuscarPorTipo(sc, gestor);
            case 6 -> opcionBuscarPorLegajo(sc, gestor);
            case 7 -> imprimir(gestor.listarTodas());
            case 8 -> System.out.println(gestor.generarReporte());
            case 0 -> { }
            default -> System.out.println("Opcion invalida");
        }
    }

    private static void opcionRegistrar(Scanner sc, GestorSolicitudes gestor) {
        System.out.print("Legajo: ");
        String legajo = sc.nextLine();
        System.out.print("Nombre: ");
        String nombre = sc.nextLine();
        System.out.print("Apellido: ");
        String apellido = sc.nextLine();
        System.out.print("Email: ");
        String email = sc.nextLine();
        System.out.print("Tipo (CERTIFICADO/EQUIVALENCIA/CAMBIO_COMISION/RECLAMO_NOTA): ");
        String tipo = sc.nextLine();
        System.out.print("Prioridad (BAJA/MEDIA/ALTA): ");
        String prioridad = sc.nextLine();
        System.out.print("Descripcion: ");
        String desc = sc.nextLine();

        int id = gestor.registrarSolicitud(legajo, nombre, apellido, email, tipo, prioridad, desc);
        if (id != -1) {
            System.out.println("Solicitud registrada con id " + id);
        }
    }

    private static void opcionAsignarResponsable(Scanner sc, GestorSolicitudes gestor) {
        System.out.print("Id de solicitud: ");
        int id = leerEntero(sc, -1);
        if (id == -1) {
            System.out.println("Error: el id debe ser numerico");
            return;
        }
        System.out.print("Nombre del responsable: ");
        String resp = sc.nextLine();
        System.out.print("Area: ");
        String area = sc.nextLine();
        if (gestor.asignarResponsable(id, resp, area)) {
            System.out.println("Responsable asignado");
        }
    }

    private static void opcionCambiarEstado(Scanner sc, GestorSolicitudes gestor) {
        System.out.print("Id de solicitud: ");
        int id = leerEntero(sc, -1);
        if (id == -1) {
            System.out.println("Error: el id debe ser numerico");
            return;
        }
        System.out.print("Nuevo estado (EN_REVISION/RESUELTA/RECHAZADA/CERRADA): ");
        String est = sc.nextLine();
        if (gestor.cambiarEstado(id, est)) {
            System.out.println("Estado actualizado");
        }
    }

    private static void opcionBuscarPorEstado(Scanner sc, GestorSolicitudes gestor) {
        System.out.print("Estado: ");
        imprimir(gestor.buscarPorEstado(sc.nextLine().trim()));
    }

    private static void opcionBuscarPorTipo(Scanner sc, GestorSolicitudes gestor) {
        System.out.print("Tipo: ");
        imprimir(gestor.buscarPorTipo(sc.nextLine().trim()));
    }

    private static void opcionBuscarPorLegajo(Scanner sc, GestorSolicitudes gestor) {
        System.out.print("Legajo: ");
        imprimir(gestor.buscarPorLegajo(sc.nextLine().trim()));
    }

    private static void mostrarMenu() {
        System.out.println();
        System.out.println("=== GESTION DE SOLICITUDES ACADEMICAS ===");
        System.out.println("1. Registrar solicitud");
        System.out.println("2. Asignar responsable");
        System.out.println("3. Cambiar estado");
        System.out.println("4. Buscar por estado");
        System.out.println("5. Buscar por tipo");
        System.out.println("6. Buscar por legajo");
        System.out.println("7. Listar todas");
        System.out.println("8. Reporte");
        System.out.println("0. Salir");
        System.out.print("Opcion: ");
    }

    private static int leerEntero(Scanner sc, int valorPorDefecto) {
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            return valorPorDefecto;
        }
    }

    private static void imprimir(List<Solicitud> solicitudes) {
        if (solicitudes.isEmpty()) {
            System.out.println("No hay resultados");
            return;
        }
        for (Solicitud solicitud : solicitudes) {
            System.out.println(solicitud);
        }
    }
}