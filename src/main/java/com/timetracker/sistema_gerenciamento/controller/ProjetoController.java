package com.timetracker.sistema_gerenciamento.controller;

import com.timetracker.sistema_gerenciamento.model.Projeto;
import com.timetracker.sistema_gerenciamento.model.Usuario;
import com.timetracker.sistema_gerenciamento.repository.UsuarioRepository;
import com.timetracker.sistema_gerenciamento.service.ProjetoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projetos")
public class ProjetoController {

    @Autowired
    private ProjetoService projetoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping
    public Projeto createProjeto(@RequestBody Projeto projeto) {
        Long usuarioId = projeto.getUsuarioResponsavel().getId();

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        projeto.setUsuarioResponsavel(usuario);

        return projetoService.save(projeto);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Projeto>> getProjetosDoUsuario(@PathVariable Long usuarioId) {
        List<Projeto> projetos = projetoService.getProjetosPorUsuario(usuarioId);
        return ResponseEntity.ok(projetos);
    }

    @GetMapping
    public ResponseEntity<List<Projeto>> getTodosProjetosComClientes() {
        List<Projeto> projetos = projetoService.getTodosProjetosComClientes();
        return ResponseEntity.ok(projetos);
    }
}