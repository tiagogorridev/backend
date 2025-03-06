package com.timetracker.sistema_gerenciamento.controller;

import com.timetracker.sistema_gerenciamento.model.Usuario;
import com.timetracker.sistema_gerenciamento.service.AssociacaoUsuarioProjetoService;
import com.timetracker.sistema_gerenciamento.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projetos")
public class AssociacaoUsuarioProjetoController {
    @Autowired
    private AssociacaoUsuarioProjetoService associacaoService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/{projetoId}/membros")
    public ResponseEntity<List<Usuario>> getMembrosProjeto(@PathVariable Long projetoId) {
        try {
            List<Usuario> membros = associacaoService.getMembrosProjetoPorId(projetoId);
            return ResponseEntity.ok(membros);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/usuarios-projetos")
    public ResponseEntity<?> listarUsuariosProjetos() {
        try {
            return ResponseEntity.ok(associacaoService.listarUsuariosProjetos());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/usuarios/emails")
    public ResponseEntity<?> listarEmailsUsuariosComProjetos() {
        try {
            List<String> emails = associacaoService.listarEmailsUsuariosComProjetos();
            return ResponseEntity.ok(emails);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/{projetoId}/associar-usuario/{usuarioId}")
    public ResponseEntity<String> associarUsuarioAoProjeto(@PathVariable Long projetoId, @PathVariable Long usuarioId) {
        try {
            associacaoService.associarUsuarioAoProjeto(usuarioId, projetoId);
            return ResponseEntity.ok("Usuário associado com sucesso ao projeto!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{projetoId}/remover-usuario/{usuarioId}")
    public ResponseEntity<String> removerUsuarioDoProjeto(@PathVariable Long projetoId, @PathVariable Long usuarioId) {
        try {
            associacaoService.removerUsuarioDoProjeto(usuarioId, projetoId);
            return ResponseEntity.ok("Usuário removido com sucesso do projeto!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/associacoes")
    public ResponseEntity<?> listarTodasAssociacoes() {
        try {
            return ResponseEntity.ok(associacaoService.listarTodasAssociacoes());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}