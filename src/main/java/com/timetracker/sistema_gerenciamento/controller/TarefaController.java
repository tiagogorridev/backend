package com.timetracker.sistema_gerenciamento.controller;

import com.timetracker.sistema_gerenciamento.model.Tarefa;
import com.timetracker.sistema_gerenciamento.repository.TarefaRepository;
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

    @PostMapping
    public ResponseEntity<Tarefa> createTarefa(@RequestBody Tarefa tarefa) {
        Tarefa novaTarefa = tarefaRepository.save(tarefa);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaTarefa);
    }
}