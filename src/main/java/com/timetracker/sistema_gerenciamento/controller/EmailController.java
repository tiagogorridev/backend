package com.timetracker.sistema_gerenciamento.controller;

import com.timetracker.sistema_gerenciamento.DTO.EmailRequestDTO;
import com.timetracker.sistema_gerenciamento.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/email")
@Tag(name = "Email", description = "API para envio de emails")

public class EmailController {
    private final EmailService emailService;

    @Autowired
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @Operation(
            summary = "Enviar email",
            description = "Envia um email com o assunto e mensagem especificados. Requer permissão de ADMIN.",
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email enviado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - Permissão de administrador necessária", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro ao enviar email", content = @Content)
    })
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