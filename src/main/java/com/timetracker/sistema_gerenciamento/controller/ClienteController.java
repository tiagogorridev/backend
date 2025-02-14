package com.timetracker.sistema_gerenciamento.controller;

import com.timetracker.sistema_gerenciamento.model.Cliente;
import com.timetracker.sistema_gerenciamento.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @PostMapping
    public ResponseEntity<?> cadastrarCliente(@RequestBody Cliente cliente) {
        try {
            System.out.println("Cliente recebido: Nome: " + cliente.getNomeCliente() + ", Email: " + cliente.getEmailCliente());

            if (cliente == null || cliente.getEmailCliente() == null || cliente.getEmailCliente().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "O email do cliente não pode ser nulo ou vazio"));
            }

            if (clienteService.buscarPorEmail(cliente.getEmailCliente()) != null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Email já cadastrado"));
            }

            Cliente novoCliente = clienteService.adicionarCliente(cliente);

            Map<String, Object> response = new HashMap<>();
            response.put("fullName", novoCliente.getNomeCliente());
            response.put("email", novoCliente.getEmailCliente());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erro ao cadastrar cliente: " + e.getMessage()));
        }
    }
}