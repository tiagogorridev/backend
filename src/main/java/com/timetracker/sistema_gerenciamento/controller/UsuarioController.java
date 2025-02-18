package com.timetracker.sistema_gerenciamento.controller;

import com.timetracker.sistema_gerenciamento.model.Cliente;
import com.timetracker.sistema_gerenciamento.model.Usuario;
import com.timetracker.sistema_gerenciamento.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:4200")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<Usuario>> listarTodosUsuarios() {
        List<Usuario> usuarios = usuarioService.listarTodosUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    @PostMapping("/cadastro")
    public ResponseEntity<?> cadastrarUsuario(@RequestBody Usuario usuario) {
        try {
            if (usuarioService.buscarPorEmail(usuario.getEmail()) != null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Email já cadastrado"));
            }

            Usuario novoUsuario = usuarioService.salvarUsuario(usuario);

            // Criar um Map com apenas os dados necessários
            Map<String, Object> response = new HashMap<>();
            response.put("id", novoUsuario.getId());
            response.put("nome", novoUsuario.getNome());
            response.put("email", novoUsuario.getEmail());
            response.put("perfil", novoUsuario.getPerfil());
            response.put("message", "Usuário cadastrado com sucesso");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erro ao cadastrar usuário: " + e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/emails")
    public ResponseEntity<?> getEmails() {
        // Seu código para buscar os e-mails
        List<String> emails = usuarioService.getEmails();
        return ResponseEntity.ok(emails);
    }
}