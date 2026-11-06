package solicitudes;

import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        GestorSolicitudes g = new GestorSolicitudes();
        g.cargarDatosDePrueba();

        int op = -1;
        while (op != 0) {
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
            try {
                op = Integer.parseInt(sc.nextLine().trim());
            } catch (Exception e) {
                op = -1;
            }

            if (op == 1) {
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
                int id = g.registrarSolicitud(legajo, nombre, apellido, email, tipo, prioridad, desc);
                if (id != -1) System.out.println("Solicitud registrada con id " + id);
            } else if (op == 2) {
                System.out.print("Id de solicitud: ");
                int id = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Nombre del responsable: ");
                String resp = sc.nextLine();
                System.out.print("Area: ");
                String area = sc.nextLine();
                if (g.asignarResponsable(id, resp, area)) System.out.println("Responsable asignado");
            } else if (op == 3) {
                System.out.print("Id de solicitud: ");
                int id = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Nuevo estado (EN_REVISION/RESUELTA/RECHAZADA/CERRADA): ");
                String est = sc.nextLine();
                if (g.cambiarEstado(id, est)) System.out.println("Estado actualizado");
            } else if (op == 4) {
                System.out.print("Estado: ");
                imprimir(g.buscarPorEstado(sc.nextLine().trim()));
            } else if (op == 5) {
                System.out.print("Tipo: ");
                imprimir(g.buscarPorTipo(sc.nextLine().trim()));
            } else if (op == 6) {
                System.out.print("Legajo: ");
                imprimir(g.buscarPorLegajo(sc.nextLine().trim()));
            } else if (op == 7) {
                imprimir(g.listarTodas());
            } else if (op == 8) {
                System.out.println(g.generarReporte());
            } else if (op != 0) {
                System.out.println("Opcion invalida");
            }
        }
        System.out.println("Fin del programa");
    }

    private static void imprimir(List<Solicitud> l) {
        if (l.isEmpty()) {
            System.out.println("No hay resultados");
            return;
        }
        for (Solicitud s : l) {
            System.out.println(s);
        }
    }
}
