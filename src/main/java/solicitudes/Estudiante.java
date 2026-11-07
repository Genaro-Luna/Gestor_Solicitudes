package solicitudes;

public class Estudiante {

    private final String legajo;
    private final String nombre;
    private final String apellido;
    private final String email;

    public Estudiante(String legajo, String nombre, String apellido, String email) {
        this.legajo = legajo;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
    }

    public String getLegajo() {
        return legajo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getEmail() {
        return email;
    }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
}