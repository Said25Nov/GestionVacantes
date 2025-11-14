package com.said_jesus.Gestion_vacantes.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // ===== Helper genérico =====
    private void enviar(String para, String asunto, String cuerpo) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(para);
        msg.setSubject(asunto);
        msg.setText(cuerpo);
        mailSender.send(msg);
    }

    // ===== 1) Notificar a EMPLEADOR: nueva postulación =====
    public void notificarNuevaPostulacionAEmpleador(
            String correoEmpleador,
            String nombreEmpleador,
            String nombreAspirante,
            String tituloVacante,
            String urlVerSolicitudes
    ) {
        String asunto = "Nueva postulación a tu vacante: " + tituloVacante;
        String cuerpo = "Hola " + (nombreEmpleador != null ? nombreEmpleador : "Empleador") + ",\n\n"
                + "El aspirante " + nombreAspirante + " se ha postulado a la vacante \"" + tituloVacante + "\".\n\n"
                + "Revisa los detalles en: " + urlVerSolicitudes + "\n\n"
                + "— EmpleaPro";
        enviar(correoEmpleador, asunto, cuerpo);
    }

    // ===== 2) Notificar a ASPIRANTE: resultado (aceptado / rechazado) =====
    public void notificarResultadoAlAspirante(
            String correoAspirante,
            String nombreAspirante,
            String tituloVacante,
            String empresa,
            boolean aceptada,
            String urlConfirmacion // puede ser null cuando es rechazo
    ) {
        String asunto = aceptada
                ? "✅ Has sido preseleccionado: " + tituloVacante
                : "Actualización sobre tu solicitud: " + tituloVacante;

        String cuerpo;
        if (aceptada) {
            cuerpo = "Hola " + nombreAspirante + ",\n\n"
                    + "¡Felicidades! Tu solicitud para \"" + tituloVacante + "\" ha sido ACEPTADA por " + empresa + ".\n"
                    + (urlConfirmacion != null
                    ? "Confirma tu asistencia a entrevista aquí: " + urlConfirmacion + "\n\n"
                    : "\n")
                    + "¡Éxitos!\n— EmpleaPro";
        } else {
            cuerpo = "Hola " + nombreAspirante + ",\n\n"
                    + "Gracias por postularte a \"" + tituloVacante + "\".\n"
                    + "Tras revisar tu perfil, en esta ocasión tu solicitud no ha sido seleccionada.\n\n"
                    + "Te invitamos a seguir postulando en otras vacantes.\n— EmpleaPro";
        }
        enviar(correoAspirante, asunto, cuerpo);
    }
}
