package com.said_jesus.Gestion_vacantes.controllers;

import com.said_jesus.Gestion_vacantes.services.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Endpoint de prueba para verificar que el SMTP funciona antes de integrarlo.
 * Ejemplo: http://localhost:8080/util/test-mail?to=correo@dominio.com
 */
@Controller
public class UtilController {

    private final EmailService emailService;

    public UtilController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/util/test-mail")
    public String testMail(@RequestParam String to, Model model) {
        try {
            String html = """
                <h2>Prueba de correo</h2>
                <p>Si ves este mensaje, el envío SMTP funciona correctamente.</p>
            """;
            emailService.sendHtml(to, "Prueba SMTP - Gestión de Vacantes", html, null);
            model.addAttribute("success", "Correo enviado a " + to);
        } catch (MessagingException | RuntimeException e) {
            model.addAttribute("error", "Fallo al enviar: " + e.getMessage());
        }
        // Reutilizo tu login para mostrar el mensaje; puedes cambiar la vista si quieres
        return "auth/login";
    }
}
