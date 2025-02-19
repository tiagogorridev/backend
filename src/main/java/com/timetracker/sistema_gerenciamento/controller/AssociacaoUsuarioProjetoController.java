package com.timetracker.sistema_gerenciamento.controller;

import com.timetracker.sistema_gerenciamento.service.AssociacaoUsuarioProjetoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/associacoes")
public class AssociacaoUsuarioProjetoController {

    @Autowired
    private AssociacaoUsuarioProjetoService associacaoUsuarioProjetoService;

    // Endpoint para associar usuário ao projeto
    @PostMapping("/usuario-projeto")
    public String associarUsuarioAoProjeto(@RequestParam Long idUsuario, @RequestParam Long idProjeto) {
        try {
            associacaoUsuarioProjetoService.associarUsuarioAoProjeto(idUsuario, idProjeto);
            return "Usuário associado com sucesso ao projeto!";
        } catch (RuntimeException e) {
            return "Erro: " + e.getMessage();
        }
    }
}