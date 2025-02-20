package com.timetracker.sistema_gerenciamento.service;

import com.timetracker.sistema_gerenciamento.model.Tarefa;
import com.timetracker.sistema_gerenciamento.repository.TarefaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TarefaService {

    @Autowired
    private TarefaRepository tarefaRepository;

    public List<Tarefa> listarTarefasPorProjeto(Long projetoId) {
        return tarefaRepository.findByProjetoId(projetoId);
    }

    public List<Tarefa> listarTodasTarefas() {
        return tarefaRepository.findAll();
    }

    public Tarefa salvarTarefa(Tarefa tarefa) {
        return tarefaRepository.save(tarefa);
    }

    public List<Tarefa> getTarefasByProjectId(Long projectId) {
        return tarefaRepository.findByProjetoId(projectId);
    }
}