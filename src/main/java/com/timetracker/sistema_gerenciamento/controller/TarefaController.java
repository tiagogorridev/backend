package com.timetracker.sistema_gerenciamento.controller;

import com.timetracker.sistema_gerenciamento.model.ResourceNotFoundException;
import com.timetracker.sistema_gerenciamento.model.Tarefa;
import com.timetracker.sistema_gerenciamento.model.Projeto;  // Importando a classe Projeto
import com.timetracker.sistema_gerenciamento.repository.TarefaRepository;
import com.timetracker.sistema_gerenciamento.repository.ProjetoRepository;  // Importando o repositório de Projeto
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tarefas")
public class TarefaController {

    @Autowired
    private TarefaRepository tarefaRepository;

    @Autowired
    private ProjetoRepository projetoRepository;  // Repositório de Projeto para buscar o projeto

    @PostMapping
    public ResponseEntity<Tarefa> createTarefa(@RequestBody Tarefa tarefa) {
        // Buscando o projeto usando o ID passado na tarefa
        Projeto projeto = projetoRepository.findById(tarefa.getProjeto().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Projeto não encontrado"));

        // Associando o projeto à tarefa
        tarefa.setProjeto(projeto);

        // Salvando a tarefa com o projeto associado
        Tarefa novaTarefa = tarefaRepository.save(tarefa);

        return ResponseEntity.status(HttpStatus.CREATED).body(novaTarefa);
    }
}