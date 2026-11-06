package solicitudes;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase principal del sistema. Maneja todo lo de las solicitudes.
 */
public class GestorSolicitudes {

    private ArrayList<Solicitud> lst = new ArrayList<>();
    private ArrayList<Estudiante> estudiantes = new ArrayList<>();
    private int cont = 1;
    private NotificadorEmail notif = new NotificadorEmail();

    // registra una solicitud nueva. devuelve el id o -1 si hubo error
    public int registrarSolicitud(String legajo, String nombre, String apellido, String email,
                                  String tipo, String prioridad, String descripcion) {
        // validaciones
        if (legajo == null || legajo.trim().equals("")) {
            System.out.println("Error: el legajo es obligatorio");
            return -1;
        }
        if (nombre == null || nombre.trim().equals("") || apellido == null || apellido.trim().equals("")) {
            System.out.println("Error: nombre y apellido son obligatorios");
            return -1;
        }
        if (email == null || !email.contains("@")) {
            System.out.println("Error: email invalido");
            return -1;
        }
        if (tipo == null) {
            System.out.println("Error: tipo invalido");
            return -1;
        }
        tipo = tipo.toUpperCase();
        if (!tipo.equals("CERTIFICADO") && !tipo.equals("EQUIVALENCIA")
                && !tipo.equals("CAMBIO_COMISION") && !tipo.equals("RECLAMO_NOTA")) {
            System.out.println("Error: tipo invalido");
            return -1;
        }
        if (prioridad == null) {
            System.out.println("Error: prioridad invalida");
            return -1;
        }
        prioridad = prioridad.toUpperCase();
        if (!prioridad.equals("BAJA") && !prioridad.equals("MEDIA") && !prioridad.equals("ALTA")) {
            System.out.println("Error: prioridad invalida");
            return -1;
        }
        if (descripcion == null || descripcion.trim().length() < 10) {
            System.out.println("Error: la descripcion debe tener al menos 10 caracteres");
            return -1;
        }

        // busco si el estudiante ya existe, si no lo creo
        Estudiante e = null;
        for (int i = 0; i < estudiantes.size(); i++) {
            if (estudiantes.get(i).legajo.equals(legajo)) {
                e = estudiantes.get(i);
            }
        }
        if (e == null) {
            e = new Estudiante(legajo, nombre, apellido, email);
            estudiantes.add(e);
        }

        // creo la solicitud
        Solicitud s = new Solicitud();
        s.id = cont;
        cont++;
        s.estudiante = e;
        s.tipo = tipo;
        s.prioridad = prioridad;
        s.estado = "PENDIENTE";
        s.descripcion = descripcion.trim();
        s.fechaCreacion = LocalDate.now();
        lst.add(s);

        // aviso al estudiante
        try {
            notif.enviar(e.email, "Solicitud registrada",
                    "Hola " + e.nombre + " " + e.apellido + ", tu solicitud #" + s.id
                            + " de tipo " + s.tipo + " fue registrada con prioridad " + s.prioridad);
        } catch (Exception ex) {
        }

        return s.id;
    }

    // asigna un responsable a la solicitud
    public boolean asignarResponsable(int id, String nombreResp, String area) {
        Solicitud s = null;
        for (int i = 0; i < lst.size(); i++) {
            if (lst.get(i).id == id) {
                s = lst.get(i);
            }
        }
        if (s == null) {
            System.out.println("Error: no existe la solicitud " + id);
            return false;
        }
        if (s.estado.equals("CERRADA") || s.estado.equals("RECHAZADA")) {
            System.out.println("Error: la solicitud ya esta finalizada");
            return false;
        }
        if (nombreResp == null || nombreResp.trim().equals("")) {
            System.out.println("Error: el responsable es obligatorio");
            return false;
        }
        s.responsable = new Responsable(nombreResp, area);
        if (s.estado.equals("PENDIENTE")) {
            s.estado = "EN_REVISION";
        }
        try {
            notif.enviar(s.estudiante.email, "Responsable asignado",
                    "Hola " + s.estudiante.nombre + " " + s.estudiante.apellido + ", tu solicitud #" + s.id
                            + " fue asignada a " + nombreResp + " (" + area + ")");
        } catch (Exception ex) {
        }
        return true;
    }

    // cambia el estado de la solicitud
    public boolean cambiarEstado(int id, String nuevoEstado) {
        Solicitud s = null;
        for (int i = 0; i < lst.size(); i++) {
            if (lst.get(i).id == id) {
                s = lst.get(i);
            }
        }
        if (s == null) {
            System.out.println("Error: no existe la solicitud " + id);
            return false;
        }
        if (nuevoEstado == null) {
            return false;
        }
        nuevoEstado = nuevoEstado.toUpperCase();
        if (s.estado.equals("CERRADA") || s.estado.equals("RECHAZADA")) {
            System.out.println("Error: la solicitud ya esta finalizada");
            return false;
        }
        boolean ok = false;
        if (s.estado.equals("PENDIENTE")) {
            if (nuevoEstado.equals("EN_REVISION")) {
                if (s.responsable == null) {
                    System.out.println("Error: primero hay que asignar un responsable");
                    return false;
                }
                ok = true;
            } else if (nuevoEstado.equals("RECHAZADA")) {
                ok = true;
            }
        } else if (s.estado.equals("EN_REVISION")) {
            if (nuevoEstado.equals("RESUELTA") || nuevoEstado.equals("RECHAZADA")) {
                ok = true;
            }
        } else if (s.estado.equals("RESUELTA")) {
            if (nuevoEstado.equals("CERRADA")) {
                ok = true;
            }
        }
        if (!ok) {
            System.out.println("Error: no se puede pasar de " + s.estado + " a " + nuevoEstado);
            return false;
        }
        s.estado = nuevoEstado;
        try {
            notif.enviar(s.estudiante.email, "Cambio de estado",
                    "Hola " + s.estudiante.nombre + " " + s.estudiante.apellido + ", tu solicitud #" + s.id
                            + " ahora esta " + s.estado);
        } catch (Exception ex) {
        }
        return true;
    }

    public Solicitud obtener(int id) {
        for (int i = 0; i < lst.size(); i++) {
            if (lst.get(i).id == id) {
                return lst.get(i);
            }
        }
        return null;
    }

    public List<Solicitud> buscarPorEstado(String estado) {
        List<Solicitud> res = new ArrayList<>();
        for (int i = 0; i < lst.size(); i++) {
            if (lst.get(i).estado.equalsIgnoreCase(estado)) {
                res.add(lst.get(i));
            }
        }
        return res;
    }

    public List<Solicitud> buscarPorTipo(String tipo) {
        List<Solicitud> res = new ArrayList<>();
        for (int i = 0; i < lst.size(); i++) {
            if (lst.get(i).tipo.equalsIgnoreCase(tipo)) {
                res.add(lst.get(i));
            }
        }
        return res;
    }

    public List<Solicitud> buscarPorLegajo(String legajo) {
        List<Solicitud> res = new ArrayList<>();
        for (int i = 0; i < lst.size(); i++) {
            if (lst.get(i).estudiante.legajo.equals(legajo)) {
                res.add(lst.get(i));
            }
        }
        return res;
    }

    public List<Solicitud> listarTodas() {
        return lst;
    }

    // genera el reporte general
    public String generarReporte() {
        String r = "";
        r = r + "===== REPORTE DE SOLICITUDES =====\n";
        r = r + "Total: " + lst.size() + "\n";
        int p = 0, er = 0, res = 0, rech = 0, c = 0;
        int a = 0, m = 0, b = 0;
        int venc = 0;
        for (int i = 0; i < lst.size(); i++) {
            Solicitud s = lst.get(i);
            if (s.estado.equals("PENDIENTE")) p++;
            else if (s.estado.equals("EN_REVISION")) er++;
            else if (s.estado.equals("RESUELTA")) res++;
            else if (s.estado.equals("RECHAZADA")) rech++;
            else if (s.estado.equals("CERRADA")) c++;

            if (s.prioridad.equals("ALTA")) a++;
            else if (s.prioridad.equals("MEDIA")) m++;
            else if (s.prioridad.equals("BAJA")) b++;

            // calculo si esta vencida segun la prioridad
            if (s.estado.equals("PENDIENTE") || s.estado.equals("EN_REVISION")) {
                long dias = ChronoUnit.DAYS.between(s.fechaCreacion, LocalDate.now());
                int x = 0;
                if (s.prioridad.equals("ALTA")) x = 2;
                else if (s.prioridad.equals("MEDIA")) x = 5;
                else x = 10;
                if (dias > x) venc++;
            }
        }
        r = r + "-- Por estado --\n";
        r = r + "PENDIENTE: " + p + "\n";
        r = r + "EN_REVISION: " + er + "\n";
        r = r + "RESUELTA: " + res + "\n";
        r = r + "RECHAZADA: " + rech + "\n";
        r = r + "CERRADA: " + c + "\n";
        r = r + "-- Por prioridad --\n";
        r = r + "ALTA: " + a + "\n";
        r = r + "MEDIA: " + m + "\n";
        r = r + "BAJA: " + b + "\n";
        r = r + "Vencidas: " + venc + "\n";
        r = r + "==================================";
        return r;
    }

    // metodo viejo, ya no se usa
    // public void borrarSolicitud(int id) {
    //     for (int i = 0; i < lst.size(); i++) {
    //         if (lst.get(i).id == id) lst.remove(i);
    //     }
    // }

    public void cargarDatosDePrueba() {
        registrarSolicitud("1001", "Ana", "Gomez", "ana.gomez@mail.com", "CERTIFICADO", "ALTA",
                "Necesito certificado de alumno regular para el trabajo");
        registrarSolicitud("1002", "Luis", "Perez", "luis.perez@mail.com", "EQUIVALENCIA", "MEDIA",
                "Pido equivalencia de Programacion I cursada en otra facultad");
        registrarSolicitud("1001", "Ana", "Gomez", "ana.gomez@mail.com", "RECLAMO_NOTA", "BAJA",
                "Reclamo por nota del segundo parcial de Matematica");
        asignarResponsable(2, "Laura Diaz", "Bedelia");
    }
}
