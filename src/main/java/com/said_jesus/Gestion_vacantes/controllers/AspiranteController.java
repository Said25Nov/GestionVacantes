package com.said_jesus.Gestion_vacantes.controllers;

import com.said_jesus.Gestion_vacantes.models.Aspirante;
import com.said_jesus.Gestion_vacantes.models.Solicitud;
import com.said_jesus.Gestion_vacantes.services.SolicitudService;
import com.said_jesus.Gestion_vacantes.services.VacanteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/aspirante")
public class AspiranteController {

    private final SolicitudService solicitudService;
    private final VacanteService vacanteService; // <<-- USAMOS TU VacanteService

    public AspiranteController(SolicitudService solicitudService, VacanteService vacanteService) {
        this.solicitudService = solicitudService;
        this.vacanteService = vacanteService;
    }

    // Redirige el dashboard a Mis Solicitudes
    @GetMapping("/dashboard")
    public String dashboardAspirante(HttpSession session) {
        Object usuario = session.getAttribute("usuario");
        if (usuario == null || !(usuario instanceof Aspirante)) {
            return "redirect:/auth/login";
        }
        return "redirect:/aspirante/mis-solicitudes";
    }

    // Vista: templates/aspirante/mis-solicitudes.html
    @GetMapping("/mis-solicitudes")
    public String misSolicitudes(HttpSession session, Model model) {
        Object usuario = session.getAttribute("usuario");
        if (usuario == null || !(usuario instanceof Aspirante)) {
            return "redirect:/auth/login";
        }
        Aspirante asp = (Aspirante) usuario;
        model.addAttribute("solicitudes", solicitudService.obtenerSolicitudesPorAspirante(asp.getId()));
        return "aspirante/mis-solicitudes";
    }

    // >>>>>> ESTE ENDPOINT ES EL QUE TE FALTABA <<<<<<<
    // Vista: templates/aspirante/vacantes.html
    @GetMapping("/vacantes")
    public String listarVacantes(HttpSession session, Model model) {
        Object usuario = session.getAttribute("usuario");
        if (usuario == null || !(usuario instanceof Aspirante)) {
            return "redirect:/auth/login";
        }
        model.addAttribute("vacantes", vacanteService.listarVacantesPublicadas());
        return "aspirante/vacantes";
    }

    // Postular a una vacante (envía correo al empleador)
    @PostMapping("/postular/{vacanteId}")
    public String postular(@PathVariable Long vacanteId,
                           @RequestParam(value = "cv", required = false) MultipartFile cv,
                           HttpSession session,
                           RedirectAttributes ra) {
        Object usuario = session.getAttribute("usuario");
        if (usuario == null || !(usuario instanceof Aspirante)) {
            return "redirect:/auth/login";
        }
        Aspirante aspirante = (Aspirante) usuario;

        String cvNombre = null;
        try {
            if (cv != null && !cv.isEmpty()) {
                Path dir = Paths.get("uploads/cv").toAbsolutePath().normalize();
                Files.createDirectories(dir);
                cvNombre = System.currentTimeMillis() + "_" + cv.getOriginalFilename();
                Files.copy(cv.getInputStream(), dir.resolve(cvNombre));
            }

            Solicitud s = solicitudService.crearPostulacion(vacanteId, aspirante.getId(), cvNombre);
            ra.addFlashAttribute("success", "¡Postulación enviada con éxito!");
            return "redirect:/aspirante/mis-solicitudes";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al postular: " + e.getMessage());
            return "redirect:/aspirante/vacantes";
        }
    }
    @PostMapping("/aplicar/{vacanteId}")
    public String aplicar(@PathVariable Long vacanteId,
                          @RequestParam(value = "cv", required = false) MultipartFile cv,
                          HttpSession session,
                          RedirectAttributes ra) {
        return postular(vacanteId, cv, session, ra);
    }

}
