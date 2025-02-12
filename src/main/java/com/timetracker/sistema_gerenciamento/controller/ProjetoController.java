package com.timetracker.sistema_gerenciamento.controller;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import com.timetracker.sistema_gerenciamento.service.ProjetoService;
import com.timetracker.sistema_gerenciamento.model.Projeto;

@RestController
@RequestMapping("/api/projetos")
public class ProjetoController {

    @Autowired
    private ProjetoService projetoService;

    @PostMapping
    public Projeto createProjeto(@RequestBody Projeto projeto) {
        return projetoService.save(projeto);
    }
}