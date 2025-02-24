package com.timetracker.sistema_gerenciamento.service;

import com.timetracker.sistema_gerenciamento.model.Projeto;
import com.timetracker.sistema_gerenciamento.model.Tarefa;
import com.timetracker.sistema_gerenciamento.repository.TarefaRepository;
import com.timetracker.sistema_gerenciamento.repository.ProjetoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TarefaService {

    @Autowired
    private TarefaRepository tarefaRepository;

    @Autowired
    private ProjetoRepository projetoRepository;

    public List<Tarefa> listarTarefasPorProjeto(Long projetoId) {
        return tarefaRepository.findByProjetoId(projetoId);
    }

    public List<Tarefa> listarTodasTarefas() {
        return tarefaRepository.findAll();
    }

    public Tarefa salvarTarefa(Tarefa tarefa) {
        // Primeiro verifica se as horas são válidas
        BigDecimal horasDisponiveis = calcularHorasDisponiveisProjeto(tarefa.getProjeto().getId());

        if (tarefa.getHorasEstimadas().compareTo(horasDisponiveis) > 0) {
            throw new IllegalArgumentException("Horas estimadas excedem o limite disponível do projeto");
        }

        return tarefaRepository.save(tarefa);
    }

    public List<Tarefa> getTarefasByProjectId(Long projectId) {
        return tarefaRepository.findByProjetoId(projectId);
    }

    public void deleteTarefa(Long id) {
        tarefaRepository.deleteById(id);
    }

    private BigDecimal calcularHorasDisponiveisProjeto(Long projetoId) {
        Projeto projeto = projetoRepository.findById(projetoId)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));

        List<Tarefa> tarefas = tarefaRepository.findByProjetoId(projetoId);

        BigDecimal horasUtilizadas = tarefas.stream()
                .map(tarefa -> tarefa.getHorasEstimadas())
                .filter(horas -> horas != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return projeto.getHorasEstimadas().subtract(horasUtilizadas);
    }
}