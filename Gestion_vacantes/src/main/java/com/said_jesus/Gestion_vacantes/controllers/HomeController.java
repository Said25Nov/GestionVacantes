package com.said_jesus.Gestion_vacantes.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    // Cuando entren a http://IP:8080/ los mando al login
    @GetMapping("/")
    public String root() {
        return "redirect:/auth/login";
    }

    // Endpoint rápido para probar desde el cel: http://IP:8080/health
    @GetMapping("/health")
    @ResponseBody
    public String health() {
        return "OK";
    }
}
