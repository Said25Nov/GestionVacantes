package com.said_jesus.Gestion_vacantes.controllers;

import com.said_jesus.Gestion_vacantes.models.*;
import com.said_jesus.Gestion_vacantes.services.VacanteService;
import com.said_jesus.Gestion_vacantes.services.SolicitudService;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/empleador")
public class EmpleadorController { // <- corregido el nombre y el espacio

    private final VacanteService vacanteService;
    private final SolicitudService solicitudService;

    public EmpleadorController(VacanteService vacanteService, SolicitudService solicitudService) {
        this.vacanteService = vacanteService;
        this.solicitudService = solicitudService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Object usuario = session.getAttribute("usuario");
        if (usuario == null || !(usuario instanceof Empleador)) {
            return "redirect:/auth/login";
        }

        Empleador empleador = (Empleador) usuario;

        try {
            List<Vacante> vacantesEmpleador = vacanteService.obtenerVacantesPorEmpleador(empleador.getId());
            List<Solicitud> todasSolicitudes = solicitudService.obtenerSolicitudesPorEmpleador(empleador.getId());

            long totalVacantes = vacantesEmpleador.size();
            long totalSolicitudes = todasSolicitudes.size();
            long solicitudesPendientes = todasSolicitudes.stream()
                    .filter(s -> s.getEstado() == EstadoSolicitud.PENDIENTE)
                    .count();
            long vacantesPublicadas = vacantesEmpleador.stream()
                    .filter(v -> v.getEstado() == EstadoVacante.PUBLICADA)
                    .count();

            List<Solicitud> solicitudesRecientes = todasSolicitudes.stream()
                    .sorted((s1, s2) -> s2.getFechaSolicitud().compareTo(s1.getFechaSolicitud()))
                    .limit(5)
                    .collect(Collectors.toList());

            model.addAttribute("empleador", empleador);
            model.addAttribute("totalVacantes", totalVacantes);
            model.addAttribute("totalSolicitudes", totalSolicitudes);
            model.addAttribute("solicitudesPendientes", solicitudesPendientes);
            model.addAttribute("vacantesPublicadas", vacantesPublicadas);
            model.addAttribute("solicitudesRecientes", solicitudesRecientes);

        } catch (Exception e) {
            model.addAttribute("empleador", empleador);
            model.addAttribute("totalVacantes", 0);
            model.addAttribute("totalSolicitudes", 0);
            model.addAttribute("solicitudesPendientes", 0);
            model.addAttribute("vacantesPublicadas", 0);
            model.addAttribute("solicitudesRecientes", java.util.Collections.emptyList());
        }

        return "empleador/dashboard";
    }

    // ------- VACANTES (se quedan aquí) --------

    @GetMapping("/vacantes")
    public String listarVacantes(HttpSession session, Model model) {
        Object usuario = session.getAttribute("usuario");
        if (usuario == null || !(usuario instanceof Empleador)) {
            return "redirect:/auth/login";
        }

        Empleador empleador = (Empleador) usuario;
        List<Vacante> vacantes = vacanteService.obtenerVacantesPorEmpleador(empleador.getId());
        model.addAttribute("vacantes", vacantes);

        return "empleador/vacantes";
    }

    @GetMapping("/vacantes/nueva")
    public String mostrarFormularioVacante(Model model, HttpSession session) {
        Object usuario = session.getAttribute("usuario");
        if (usuario == null || !(usuario instanceof Empleador)) {
            return "redirect:/auth/login";
        }

        model.addAttribute("vacante", new Vacante());
        model.addAttribute("tiposTrabajo", TipoTrabajo.values());
        return "empleador/form-vacante";
    }

    @PostMapping("/vacantes")
    public String crearVacante(@ModelAttribute Vacante vacante, HttpSession session) {
        Object usuario = session.getAttribute("usuario");
        if (usuario == null || !(usuario instanceof Empleador)) {
            return "redirect:/auth/login";
        }

        vacante.setEmpleador((Empleador) usuario);
        vacante.setEstado(EstadoVacante.PUBLICADA);
        vacanteService.crearVacante(vacante);

        return "redirect:/empleador/vacantes";
    }

    @GetMapping("/vacantes/editar/{id}")
    public String mostrarFormularioEditarVacante(@PathVariable Long id, HttpSession session, Model model) {
        Object usuario = session.getAttribute("usuario");
        if (usuario == null || !(usuario instanceof Empleador)) {
            return "redirect:/auth/login";
        }

        Optional<Vacante> vacanteOpt = vacanteService.obtenerVacantePorId(id);
        if (vacanteOpt.isPresent()) {
            Vacante vacante = vacanteOpt.get();
            if (vacante.getEmpleador().getId().equals(((Empleador) usuario).getId())) {
                model.addAttribute("vacante", vacante);
                model.addAttribute("tiposTrabajo", TipoTrabajo.values());
                model.addAttribute("estadosVacante", EstadoVacante.values());
                return "empleador/editar-vacante";
            }
        }

        return "redirect:/empleador/vacantes";
    }

    @PostMapping("/vacantes/editar/{id}")
    public String actualizarVacante(@PathVariable Long id, @ModelAttribute Vacante vacanteActualizada,
                                    HttpSession session, RedirectAttributes redirectAttributes) {
        Object usuario = session.getAttribute("usuario");
        if (usuario == null || !(usuario instanceof Empleador)) {
            return "redirect:/auth/login";
        }

        try {
            Optional<Vacante> vacanteExistenteOpt = vacanteService.obtenerVacantePorId(id);
            if (vacanteExistenteOpt.isPresent()) {
                Vacante vacanteExistente = vacanteExistenteOpt.get();

                if (!vacanteExistente.getEmpleador().getId().equals(((Empleador) usuario).getId())) {
                    redirectAttributes.addFlashAttribute("error", "No tienes permisos para editar esta vacante");
                    return "redirect:/empleador/vacantes";
                }

                vacanteExistente.setTitulo(vacanteActualizada.getTitulo());
                vacanteExistente.setDescripcion(vacanteActualizada.getDescripcion());
                vacanteExistente.setTipoTrabajo(vacanteActualizada.getTipoTrabajo());
                vacanteExistente.setSalario(vacanteActualizada.getSalario());
                vacanteExistente.setUbicacion(vacanteActualizada.getUbicacion());
                vacanteExistente.setRequisitos(vacanteActualizada.getRequisitos());
                vacanteExistente.setEstado(vacanteActualizada.getEstado());
                vacanteExistente.setFechaVencimiento(vacanteActualizada.getFechaVencimiento());

                vacanteService.actualizarVacante(vacanteExistente);
                redirectAttributes.addFlashAttribute("success", "Vacante actualizada correctamente");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar la vacante");
        }

        return "redirect:/empleador/vacantes";
    }

    @PostMapping("/vacantes/eliminar/{id}")
    public String eliminarVacante(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Object usuario = session.getAttribute("usuario");
        if (usuario == null || !(usuario instanceof Empleador)) {
            return "redirect:/auth/login";
        }

        try {
            Optional<Vacante> vacanteOpt = vacanteService.obtenerVacantePorId(id);
            if (vacanteOpt.isPresent()) {
                Vacante vacante = vacanteOpt.get();

                if (!vacante.getEmpleador().getId().equals(((Empleador) usuario).getId())) {
                    redirectAttributes.addFlashAttribute("error", "No tienes permisos para eliminar esta vacante");
                    return "redirect:/empleador/vacantes";
                }

                vacanteService.eliminarVacante(id);
                redirectAttributes.addFlashAttribute("success", "Vacante eliminada correctamente");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la vacante");
        }

        return "redirect:/empleador/vacantes";
    }

    // ------- DESCARGA DE CV (se puede quedar aquí) --------
    @GetMapping("/download/cv/{fileName}")
    public ResponseEntity<Resource> descargarCV(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get("uploads/cv/").resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
