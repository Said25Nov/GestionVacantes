package com.said_jesus.Gestion_vacantes.services;

import com.said_jesus.Gestion_vacantes.models.Solicitud;
import org.springframework.stereotype.Service;

@Service
public class NotificacionSolicitudService {

    private final MailService mailService;

    public NotificacionSolicitudService(MailService mailService) {
        this.mailService = mailService;
    }

    public void notificarNuevaPostulacionAEmpleador(Solicitud s) {
        try {
            String correoEmpleador   = s.getVacante().getEmpleador().getCorreo();
            String nombreEmpleador   = s.getVacante().getEmpleador().getNombre();
            String nombreAspirante   = s.getAspirante().getNombre();
            String tituloVacante     = s.getVacante().getTitulo();
            String urlVerSolicitudes = "http://localhost:8080/empleador/solicitudes";
            mailService.notificarNuevaPostulacionAEmpleador(
                    correoEmpleador, nombreEmpleador, nombreAspirante, tituloVacante, urlVerSolicitudes
            );
        } catch (Exception ex) {
            System.err.println("WARN mail empleador: " + ex.getMessage());
        }
    }

    public void notificarResultadoAlAspirante(Solicitud s, boolean aceptada) {
        try {
            String correoAspirante = s.getAspirante().getCorreo();
            String nombreAspirante = s.getAspirante().getNombre();
            String tituloVacante   = s.getVacante().getTitulo();
            String empresa         = s.getVacante().getEmpleador().getEmpresa();
            String urlConfirmacion = aceptada ? ("http://localhost:8080/confirmar/" + s.getId()) : null;

            mailService.notificarResultadoAlAspirante(
                    correoAspirante, nombreAspirante, tituloVacante, empresa, aceptada, urlConfirmacion
            );
        } catch (Exception ex) {
            System.err.println("WARN mail aspirante: " + ex.getMessage());
        }
    }
}
