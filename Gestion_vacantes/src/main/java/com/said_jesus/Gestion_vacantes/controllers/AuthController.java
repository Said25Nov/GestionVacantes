package com.said_jesus.Gestion_vacantes.controllers;

import com.said_jesus.Gestion_vacantes.models.Aspirante;
import com.said_jesus.Gestion_vacantes.models.Empleador;
import com.said_jesus.Gestion_vacantes.services.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String mostrarLogin() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String correo,
                        @RequestParam String contrasena,
                        HttpSession session,
                        Model model) {
        Object usuario = usuarioService.login(correo, contrasena);
        if (usuario != null) {
            session.setAttribute("usuario", usuario);
            session.setAttribute("tipoUsuario", usuario.getClass().getSimpleName());

            if (usuario instanceof Aspirante) {
                return "redirect:/aspirante/dashboard";
            } else if (usuario instanceof Empleador) {
                return "redirect:/empleador/dashboard";
            }
        }

        model.addAttribute("error", "Credenciales inválidas");
        return "auth/login";
    }

    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        return "auth/registro";
    }

    @PostMapping("/registro")
    public String registrar(@RequestParam String nombre,
                            @RequestParam String correo,
                            @RequestParam String contrasenaHash,
                            @RequestParam String tipoUsuario,
                            @RequestParam(required = false) String empresa,
                            @RequestParam(required = false) String habilidades,
                            Model model) {
        try {
            usuarioService.registrarUsuario(nombre, correo, contrasenaHash, tipoUsuario, empresa, habilidades);
            model.addAttribute("success", "Usuario registrado exitosamente");
            return "auth/login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/registro";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/auth/login";
    }
}