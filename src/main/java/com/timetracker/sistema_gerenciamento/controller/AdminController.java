package com.timetracker.sistema_gerenciamento.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {

    @GetMapping("/dashboard")
    public ResponseEntity<?> getAdminDashboard() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Painel do Administrador");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/activities")
    public ResponseEntity<?> getAdminActivities() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Gerenciamento de atividades para Admin");
        return ResponseEntity.ok(response);
    }
}