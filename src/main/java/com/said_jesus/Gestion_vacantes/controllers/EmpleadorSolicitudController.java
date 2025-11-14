package com.said_jesus.Gestion_vacantes.controllers;

import com.said_jesus.Gestion_vacantes.models.Empleador;
import com.said_jesus.Gestion_vacantes.models.EstadoSolicitud;
import com.said_jesus.Gestion_vacantes.models.Solicitud;
import com.said_jesus.Gestion_vacantes.services.SolicitudService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/empleador/solicitudes")
public class EmpleadorSolicitudController {

    private final SolicitudService solicitudService;

    public EmpleadorSolicitudController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    // Listado general -> VISTA: templates/empleador/solicitudes.html
    @GetMapping("")
    public String listarTodas(HttpSession session, Model model) {
        Object usuario = session.getAttribute("usuario");
        if (usuario == null || !(usuario instanceof Empleador)) {
            return "redirect:/auth/login";
        }
        Empleador empleador = (Empleador) usuario;
        List<Solicitud> solicitudes = solicitudService.obtenerSolicitudesPorEmpleador(empleador.getId());
        model.addAttribute("solicitudes", solicitudes);
        return "empleador/solicitudes";
    }

    // Listado por vacante -> misma vista
    @GetMapping("/{vacanteId}")
    public String listarPorVacante(@PathVariable Long vacanteId, HttpSession session, Model model) {
        Object usuario = session.getAttribute("usuario");
        if (usuario == null || !(usuario instanceof Empleador)) {
            return "redirect:/auth/login";
        }
        List<Solicitud> solicitudes = solicitudService.obtenerSolicitudesPorVacante(vacanteId);
        model.addAttribute("solicitudes", solicitudes);
        model.addAttribute("vacanteId", vacanteId);
        return "empleador/solicitudes";
    }

    // Detalle -> VISTA: templates/empleador/detalle-solicitud.html
    @GetMapping("/detalle/{solicitudId}")
    public String verDetalle(@PathVariable Long solicitudId, HttpSession session, Model model) {
        Object usuario = session.getAttribute("usuario");
        if (usuario == null || !(usuario instanceof Empleador)) {
            return "redirect:/auth/login";
        }
        Optional<Solicitud> solicitudOpt = solicitudService.obtenerSolicitudPorId(solicitudId);
        if (solicitudOpt.isPresent()) {
            model.addAttribute("solicitud", solicitudOpt.get());
            return "empleador/detalle-solicitud";
        }
        return "redirect:/empleador/solicitudes";
    }

    // ACEPTAR (permite GET/POST para pruebas y enlaces)
    @RequestMapping(value = "/{solicitudId}/aceptar", method = {RequestMethod.POST, RequestMethod.GET})
    public String aceptar(@PathVariable Long solicitudId, HttpSession session, RedirectAttributes ra) {
        Object usuario = session.getAttribute("usuario");
        if (usuario == null || !(usuario instanceof Empleador)) {
            return "redirect:/auth/login";
        }
        try {
            solicitudService.actualizarEstadoSolicitud(solicitudId, EstadoSolicitud.ACEPTADA, "Aceptado por empleador");
            ra.addFlashAttribute("success", "Solicitud aceptada y correo enviado al aspirante");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "No se pudo aceptar la solicitud: " + e.getMessage());
        }
        return "redirect:/empleador/solicitudes";
    }

    // RECHAZAR
    @RequestMapping(value = "/{solicitudId}/rechazar", method = {RequestMethod.POST, RequestMethod.GET})
    public String rechazar(@PathVariable Long solicitudId, HttpSession session, RedirectAttributes ra) {
        Object usuario = session.getAttribute("usuario");
        if (usuario == null || !(usuario instanceof Empleador)) {
            return "redirect:/auth/login";
        }
        try {
            solicitudService.actualizarEstadoSolicitud(solicitudId, EstadoSolicitud.RECHAZADA, "Rechazado por empleador");
            ra.addFlashAttribute("success", "Solicitud rechazada y correo enviado al aspirante");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "No se pudo rechazar la solicitud: " + e.getMessage());
        }
        return "redirect:/empleador/solicitudes";
    }

    // (Formulario genérico si lo usas)
    @PostMapping("/{solicitudId}/estado")
    public String actualizarEstado(@PathVariable Long solicitudId,
                                   @RequestParam EstadoSolicitud estado,
                                   @RequestParam(required = false) String nota,
                                   HttpSession session,
                                   RedirectAttributes ra) {
        Object usuario = session.getAttribute("usuario");
        if (usuario == null || !(usuario instanceof Empleador)) {
            return "redirect:/auth/login";
        }
        try {
            solicitudService.actualizarEstadoSolicitud(solicitudId, estado, nota);
            ra.addFlashAttribute("success", "Estado actualizado y correo enviado al aspirante");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "No se pudo actualizar el estado: " + e.getMessage());
        }
        return "redirect:/empleador/solicitudes";
    }
}
