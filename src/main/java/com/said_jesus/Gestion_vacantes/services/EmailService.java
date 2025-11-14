package com.said_jesus.Gestion_vacantes.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.lang.Nullable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Servicio sencillo para enviar correos en texto plano y HTML.
 * Se integra con el flujo de negocio en el siguiente paso.
 */
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /** Envío de texto plano */
    public void sendPlain(String to, String subject, String text, @Nullable String from) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(text);
        if (from != null && !from.isBlank()) {
            msg.setFrom(from);
        }
        mailSender.send(msg);
    }

    /** Envío HTML (para plantillas bonitas) */
    public void sendHtml(String to, String subject, String html, @Nullable String from) throws MessagingException {
        MimeMessage mime = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mime, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true); // true = HTML
        if (from != null && !from.isBlank()) {
            helper.setFrom(from);
        }
        mailSender.send(mime);
    }
}
