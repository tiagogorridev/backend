package com.timetracker.sistema_gerenciamento.controller;

import com.timetracker.sistema_gerenciamento.model.Cliente;
import com.timetracker.sistema_gerenciamento.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "APIs para gerenciamento de clientes")

public class ClienteController {
    @Autowired
    private ClienteService clienteService;


    @Operation(summary = "Listar todos os clientes", description = "Retorna todos os clientes ativos cadastrados no sistema")
    @ApiResponse(responseCode = "200", description = "Clientes listados com sucesso")
    @GetMapping
    public ResponseEntity<List<Cliente>> listarClientes() {
        List<Cliente> clientes = clienteService.listarTodosClientes();
        return ResponseEntity.ok(clientes);
    }

    @Operation(summary = "Cadastrar cliente", description = "Cadastra um novo cliente no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou email já cadastrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro ao cadastrar cliente", content = @Content)
    })
    @PostMapping
    public ResponseEntity<?> cadastrarCliente(@RequestBody Cliente cliente) {
        System.out.println("Dados recebidos: " + cliente);  // Adicionando log
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

    @Operation(summary = "Deletar cliente", description = "Realiza a exclusão lógica (soft delete) de um cliente pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente deletado com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro ao deletar cliente", content = @Content)
    })
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

    @Operation(summary = "Reativar cliente", description = "Reativa um cliente que estava marcado como inativo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente reativado com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro ao reativar cliente", content = @Content)
    })
    @PatchMapping("/{id}/reativar")
    public ResponseEntity<?> reativarCliente(@PathVariable Long id) {
        try {
            clienteService.reativarCliente(id);
            return ResponseEntity.ok(Map.of("message", "Cliente reativado com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erro ao reativar cliente: " + e.getMessage()));
        }
    }

    @Operation(summary = "Listar clientes inativos", description = "Retorna todos os clientes marcados como inativos no sistema")
    @ApiResponse(responseCode = "200", description = "Clientes inativos listados com sucesso")
    @GetMapping("/inativos")
    public ResponseEntity<List<Cliente>> listarClientesInativos() {
        List<Cliente> clientesInativos = clienteService.listarClientesInativos();
        return ResponseEntity.ok(clientesInativos);
    }
}