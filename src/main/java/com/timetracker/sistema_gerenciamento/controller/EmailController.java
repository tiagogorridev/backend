package com.timetracker.sistema_gerenciamento.controller;

import com.timetracker.sistema_gerenciamento.DTO.EmailRequestDTO;
import com.timetracker.sistema_gerenciamento.service.EmailService;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/email")
public class EmailController {
    private final EmailService emailService;

    @Autowired
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequestDTO emailRequest) {
        try {
            emailService.sendEmail(emailRequest.getSubject(), emailRequest.getMessage());
            return ResponseEntity.ok("Email enviado com sucesso!");
        } catch (MailException e) {
            return ResponseEntity.status(500).body("Erro ao enviar o e-mail: " + e.getMessage());
        }
    }
}