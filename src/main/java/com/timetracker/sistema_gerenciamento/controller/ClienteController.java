package com.timetracker.sistema_gerenciamento.controller;

import com.timetracker.sistema_gerenciamento.model.Cliente;
import com.timetracker.sistema_gerenciamento.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {
    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public ResponseEntity<List<Cliente>> listarClientes() {
        List<Cliente> clientes = clienteService.listarTodosClientes();
        return ResponseEntity.ok(clientes);
    }

    @PostMapping
    public ResponseEntity<?> cadastrarCliente(@RequestBody Cliente cliente) {
        try {
            if (cliente == null || cliente.getEmail() == null || cliente.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "O email do cliente não pode ser nulo ou vazio"));
            }

            if (clienteService.buscarPorEmail(cliente.getEmail()) != null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Email já cadastrado"));
            }

            Cliente novoCliente = clienteService.adicionarCliente(cliente);
            Map<String, Object> response = new HashMap<>();
            response.put("nome", novoCliente.getNome());
            response.put("email", novoCliente.getEmail());

            return ResponseEntity.ok(clienteService.adicionarCliente(cliente));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erro ao cadastrar cliente: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarCliente(@PathVariable Long id) {
        try {
            clienteService.softDeleteCliente(id);
            return ResponseEntity.ok(Map.of("message", "Cliente deletado com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erro ao deletar cliente: " + e.getMessage()));
        }
    }
}