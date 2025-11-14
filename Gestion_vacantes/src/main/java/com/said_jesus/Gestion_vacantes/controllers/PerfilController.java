package com.said_jesus.Gestion_vacantes.controllers;

import com.said_jesus.Gestion_vacantes.models.Aspirante;
import com.said_jesus.Gestion_vacantes.models.Empleador;
import com.said_jesus.Gestion_vacantes.services.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/perfil")
public class PerfilController {

    private final UsuarioService usuarioService;

    public PerfilController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/editar")
    public String mostrarEditarPerfil(HttpSession session, Model model) {
        Object usuario = session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("usuario", usuario);
        return "perfil/editar";
    }

    @PostMapping("/actualizar")
    public String actualizarPerfil(@ModelAttribute("usuario") Object usuarioActualizado,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        try {
            Object usuarioSession = session.getAttribute("usuario");
            Object usuarioActualizadoObj;

            if (usuarioSession instanceof Aspirante) {
                Aspirante aspirante = (Aspirante) usuarioActualizado;
                usuarioActualizadoObj = usuarioService.actualizarAspirante(((Aspirante) usuarioSession).getId(), aspirante);
                session.setAttribute("usuario", usuarioActualizadoObj);
                redirectAttributes.addFlashAttribute("success", "Perfil de aspirante actualizado correctamente");
                return "redirect:/aspirante/dashboard";
            } else if (usuarioSession instanceof Empleador) {
                Empleador empleador = (Empleador) usuarioActualizado;
                usuarioActualizadoObj = usuarioService.actualizarEmpleador(((Empleador) usuarioSession).getId(), empleador);
                session.setAttribute("usuario", usuarioActualizadoObj);
                redirectAttributes.addFlashAttribute("success", "Perfil de empleador actualizado correctamente");
                return "redirect:/empleador/dashboard";
            } else {
                throw new RuntimeException("Tipo de usuario no válido");
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el perfil: " + e.getMessage());
            return "redirect:/perfil/editar";
        }
    }
}
