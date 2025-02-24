package com.timetracker.sistema_gerenciamento.service;

import com.timetracker.sistema_gerenciamento.model.Tarefa;
import com.timetracker.sistema_gerenciamento.repository.TarefaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.timetracker.sistema_gerenciamento.model.Projeto;
import com.timetracker.sistema_gerenciamento.model.Prioridade;
import com.timetracker.sistema_gerenciamento.model.Status;
import com.timetracker.sistema_gerenciamento.repository.ProjetoRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProjetoService {

    @Autowired
    private ProjetoRepository projetoRepository;

    @Autowired
    private TarefaRepository tarefaRepository;

    @Transactional
    public Projeto getProjetoById(Long id) {
        return projetoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));
    }

    public List<Projeto> getProjetosPorUsuario(Long usuarioId) {
        return projetoRepository.findByUsuarioResponsavelId(usuarioId);
    }

    @Transactional
    public Projeto save(Projeto projeto) {
        return projetoRepository.save(projeto);
    }

    @Transactional
    public Projeto atualizarProjeto(Long id, Projeto projetoDetails) {
        Projeto projetoExistente = projetoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));

        if (projetoDetails.getNome() != null && !projetoDetails.getNome().isEmpty()) {
            projetoExistente.setNome(projetoDetails.getNome());
        }

        if (projetoDetails.getCliente() != null && projetoDetails.getCliente().getId() != null) {
            projetoExistente.setCliente(projetoDetails.getCliente());
        }

        if (projetoDetails.getHorasEstimadas() != null) {
            projetoExistente.setHorasEstimadas(projetoDetails.getHorasEstimadas());
        }

        if (projetoDetails.getCustoEstimado() != null) {
            projetoExistente.setCustoEstimado(projetoDetails.getCustoEstimado());
        }

        if (projetoDetails.getStatus() != null) {
            projetoExistente.setStatus(projetoDetails.getStatus());
        }

        if (projetoDetails.getPrioridade() != null) {
            projetoExistente.setPrioridade(projetoDetails.getPrioridade());
        }

        Projeto projetoAtualizado = projetoRepository.save(projetoExistente);
        projetoRepository.flush();

        return projetoAtualizado;
    }

    public List<Projeto> findByUsuarioId(Long usuarioId) {
        return projetoRepository.findByUsuarioResponsavelId(usuarioId);  // Correção
    }


    @Transactional(readOnly = true)
    public BigDecimal calcularHorasDisponiveis(Long projetoId) {
        Projeto projeto = getProjetoById(projetoId);
        if (projeto == null || projeto.getHorasEstimadas() == null) {
            return BigDecimal.ZERO;
        }

        List<Tarefa> tarefas = tarefaRepository.findByProjetoId(projetoId);

        BigDecimal horasUtilizadas = tarefas.stream()
                .map(Tarefa::getHorasEstimadas)
                .filter(horas -> horas != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return projeto.getHorasEstimadas().subtract(horasUtilizadas);
    }
}