package com.timetracker.sistema_gerenciamento.controller;

import com.timetracker.sistema_gerenciamento.model.Usuario;
import com.timetracker.sistema_gerenciamento.service.AssociacaoUsuarioProjetoService;
import com.timetracker.sistema_gerenciamento.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projetos")
@Tag(name = "Associação Usuário-Projeto", description = "APIs para gerenciar associações entre usuários e projetos")
public class AssociacaoUsuarioProjetoController {
    @Autowired
    private AssociacaoUsuarioProjetoService associacaoService;

    @Autowired
    private UsuarioService usuarioService;

    @Operation(summary = "Associar usuário a projeto", description = "Cria uma associação entre um usuário e um projeto específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário associado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content)
    })
    @PostMapping("/{projetoId}/associar-usuario/{usuarioId}")
    public ResponseEntity<String> associarUsuarioAoProjeto(@PathVariable Long projetoId, @PathVariable Long usuarioId) {
        try {
            associacaoService.associarUsuarioAoProjeto(usuarioId, projetoId);
            return ResponseEntity.ok("Usuário associado com sucesso ao projeto!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Listar membros de um projeto", description = "Retorna todos os usuários associados a um projeto específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Membros encontrados com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    @GetMapping("/{projetoId}/membros")
    public ResponseEntity<List<Usuario>> getMembrosProjeto(@PathVariable Long projetoId) {
        try {
            List<Usuario> membros = associacaoService.getMembrosProjetoPorId(projetoId);
            return ResponseEntity.ok(membros);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Remover usuário de projeto", description = "Remove a associação entre um usuário e um projeto específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário removido com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content)
    })
    @DeleteMapping("/{projetoId}/remover-usuario/{usuarioId}")
    public ResponseEntity<String> removerUsuarioDoProjeto(@PathVariable Long projetoId, @PathVariable Long usuarioId) {
        try {
            associacaoService.removerUsuarioDoProjeto(usuarioId, projetoId);
            return ResponseEntity.ok("Usuário removido com sucesso do projeto!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Listar todas as associações usuários-projetos", description = "Retorna todas as associações entre usuários e projetos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Associações listadas com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    @GetMapping("/usuarios-projetos")
    public ResponseEntity<?> listarUsuariosProjetos() {
        try {
            return ResponseEntity.ok(associacaoService.listarUsuariosProjetos());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @Operation(summary = "Listar emails de usuários com projetos", description = "Retorna todos os emails dos usuários que estão associados a algum projeto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Emails listados com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    @GetMapping("/usuarios/emails")
    public ResponseEntity<?> listarEmailsUsuariosComProjetos() {
        try {
            List<String> emails = associacaoService.listarEmailsUsuariosComProjetos();
            return ResponseEntity.ok(emails);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}