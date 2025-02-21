package com.timetracker.sistema_gerenciamento.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")  // Injeta o e-mail configurado no application.properties
    private String emailFrom;

    public void sendEmail(String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();

        // O remetente será sempre o e-mail do Gmail configurado
        message.setFrom(emailFrom);
        message.setTo("testeprojetowise@gmail.com");  // Destinatário fixo
        message.setSubject(subject);
        message.setText(text);

        // Enviar o e-mail
        mailSender.send(message);  // Envio do e-mail
    }
}