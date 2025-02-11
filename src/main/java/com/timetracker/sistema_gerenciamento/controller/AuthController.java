package com.timetracker.sistema_gerenciamento.controller;

import com.timetracker.sistema_gerenciamento.security.JwtUtil;
import com.timetracker.sistema_gerenciamento.service.UsuarioService;
import com.timetracker.sistema_gerenciamento.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Permite requisições de qualquer origem
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UsuarioService usuarioService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          UserDetailsService userDetailsService,
                          JwtUtil jwtUtil,
                          UsuarioService usuarioService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        try {
            String email = request.getEmail();
            System.out.println("Tentativa de login para email: " + email);

            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Email não pode estar vazio"));
            }

            Usuario usuario = usuarioService.buscarPorEmail(email);
            if (usuario == null) {
                System.out.println("Usuário não encontrado: " + email);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Email não cadastrado no sistema"));
            }

            try {
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, request.getSenha()));

                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                String token = jwtUtil.generateToken(userDetails.getUsername());

                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put("message", "Login realizado com sucesso");
                response.put("statusCode", HttpStatus.OK.value());

                return ResponseEntity.ok(response);

            } catch (BadCredentialsException e) {
                System.out.println("Senha incorreta: " + email);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Senha incorreta"));
            }

        } catch (Exception e) {
            System.out.println("Erro inesperado: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Erro interno na autenticação"));
        }
    }

    public static class LoginRequest {
        private String email;
        private String senha;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getSenha() { return senha; }
        public void setSenha(String senha) { this.senha = senha; }
    }
}