package com.timetracker.sistema_gerenciamento.controller;

import com.timetracker.sistema_gerenciamento.model.Usuario;
import com.timetracker.sistema_gerenciamento.repository.UsuarioRepository; // Importe o repositório de usuários
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import com.timetracker.sistema_gerenciamento.service.ProjetoService;
import com.timetracker.sistema_gerenciamento.model.Projeto;

import java.util.List;

@RestController
@RequestMapping("/api/projetos")
public class ProjetoController {

    @Autowired
    private ProjetoService projetoService;

    @Autowired
    private UsuarioRepository usuarioRepository;  // Injetando o UsuarioRepository

    @PostMapping
    public Projeto createProjeto(@RequestBody Projeto projeto) {
        Long usuarioId = projeto.getUsuarioResponsavel().getId();  // Extrai o id do usuário responsável

        // Verifique se o usuário existe
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Associa o usuário ao projeto
        projeto.setUsuarioResponsavel(usuario);

        // Salva o projeto associado ao usuário
        return projetoService.save(projeto);
    }

    @GetMapping("/usuario/{idUsuario}")
    public List<Projeto> getProjetosPorUsuario(@PathVariable Long idUsuario) {
        return projetoService.findProjetosByUsuario(idUsuario);
    }
}