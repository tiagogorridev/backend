package com.timetracker.sistema_gerenciamento.controller;

import com.timetracker.sistema_gerenciamento.exception.ResourceNotFoundException;
import com.timetracker.sistema_gerenciamento.model.Tarefa;
import com.timetracker.sistema_gerenciamento.model.Projeto;
import com.timetracker.sistema_gerenciamento.service.TarefaService;
import com.timetracker.sistema_gerenciamento.repository.ProjetoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tarefas")
public class TarefaController {

    @Autowired
    private TarefaService tarefaService;

    @Autowired
    private ProjetoRepository projetoRepository;

    @GetMapping
    public ResponseEntity<List<Tarefa>> listarTodasTarefas() {
        List<Tarefa> tarefas = tarefaService.listarTodasTarefas();
        return ResponseEntity.ok(tarefas);
    }

    @GetMapping("/projeto/{projetoId}")
    public ResponseEntity<List<Tarefa>> listarTarefasPorProjeto(@PathVariable Long projetoId) {
        if (!projetoRepository.existsById(projetoId)) {
            throw new ResourceNotFoundException("Projeto não encontrado");
        }
        List<Tarefa> tarefas = tarefaService.listarTarefasPorProjeto(projetoId);
        return ResponseEntity.ok(tarefas);
    }

    @PostMapping
    public ResponseEntity<Tarefa> createTarefa(@RequestBody Tarefa tarefa) {
        if (tarefa.getProjeto() == null || tarefa.getProjeto().getId() == null) {
            throw new IllegalArgumentException("Projeto não pode ser nulo");
        }

        Projeto projeto = projetoRepository.findById(tarefa.getProjeto().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Projeto não encontrado"));

        tarefa.setProjeto(projeto);

        Tarefa novaTarefa = tarefaService.salvarTarefa(tarefa);

        return ResponseEntity.status(HttpStatus.CREATED).body(novaTarefa);
    }

    @GetMapping("/projetos/{projectId}/tarefas")
    public ResponseEntity<List<Tarefa>> getProjectTasks(@PathVariable Long projectId) {
        List<Tarefa> tarefas = tarefaService.getTarefasByProjectId(projectId);
        return ResponseEntity.ok(tarefas);
    }
}