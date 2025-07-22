package com.matcheval.stage.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;
    @Async
    public void sendAccountCreatedEmail(String toEmail, String rawPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        System.out.println("✅ Tentative d'envoi d'email à : " + toEmail);
        message.setSubject("Bienvenue - Votre compte a été créé");
        message.setText("Bonjour,\n\nVotre compte a été créé avec succès.\n\n"
                + "📧 Email : " + toEmail + "\n"
                + "🔑 Mot de passe : " + rawPassword + "\n\n"
                + "Vous pouvez maintenant vous connecter à la plateforme.\n\nCordialement.");

        mailSender.send(message);
    }
}

