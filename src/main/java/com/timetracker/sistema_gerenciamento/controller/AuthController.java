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
@CrossOrigin(origins = "http://localhost:4200")
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
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            System.out.println("Tentativa de login para: " + request.getEmail());

            // Autenticação do usuário
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha());
            authenticationManager.authenticate(authenticationToken);

            // Recuperar detalhes do usuário
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

            // Gerar o token JWT usando o nome de usuário
            String token = jwtUtil.generateToken(userDetails.getUsername());

            // Retornar resposta com token JWT
            return ResponseEntity.ok(Map.of("message", "Login bem-sucedido", "token", token));

        } catch (BadCredentialsException e) {
            System.out.println("Credenciais inválidas para: " + request.getEmail());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Credenciais inválidas"));
        } catch (Exception e) {
            System.out.println("Erro inesperado no login: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erro ao realizar login: " + e.getMessage()));
        }
    }
    public static class LoginRequest {
        private String email;
        private String senha;

        // Getters e Setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getSenha() { return senha; }
        public void setSenha(String senha) { this.senha = senha; }
    }
}