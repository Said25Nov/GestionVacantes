package com.said_jesus.Gestion_vacantes.controllers;

import com.said_jesus.Gestion_vacantes.models.EstadoSolicitud;
import com.said_jesus.Gestion_vacantes.services.SolicitudService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class ConfirmacionController {

    private final SolicitudService solicitudService;

    public ConfirmacionController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    @GetMapping("/confirmar/{solicitudId}")
    public String confirmar(@PathVariable Long solicitudId) {
        try {
            solicitudService.actualizarEstadoSolicitud(
                    solicitudId, EstadoSolicitud.REVISADA, "Candidato confirmó entrevista");
        } catch (Exception ignored) {}
        // VISTA: templates/aspirante/confirmado.html
        return "aspirante/confirmado";
    }
}
