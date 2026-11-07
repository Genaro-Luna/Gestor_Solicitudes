package solicitudes;

// simula el envio de mails (no manda nada real)
public class NotificadorEmail implements Notificador{

    @Override
    public void enviar(String email, String asunto, String mensaje) {
        System.out.println("[EMAIL a " + email + "] " + asunto + ": " + mensaje);
    }
}
