package com.timetracker.sistema_gerenciamento.service;

import com.timetracker.sistema_gerenciamento.exception.ResourceNotFoundException;
import com.timetracker.sistema_gerenciamento.model.*;
import com.timetracker.sistema_gerenciamento.repository.TarefaRepository;
import com.timetracker.sistema_gerenciamento.repository.UsuariosProjetosRepository;
import com.timetracker.sistema_gerenciamento.repository.ProjetoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProjetoService {

    @Autowired
    private ProjetoRepository projetoRepository;

    @Autowired
    private TarefaRepository tarefaRepository;

    @Autowired
    private UsuariosProjetosRepository usuariosProjetosRepository;

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

    public List<Projeto> getProjetosAtivosDoUsuario(Long usuarioId) {
        return projetoRepository.findByUsuarioResponsavelIdAndDeletedAtIsNull(usuarioId);
    }

    public List<Projeto> findAllProjetosAtivos() {
        return projetoRepository.findAllActive();
    }

    @Transactional
    public Projeto atualizarProjeto(Long id, Projeto projetoDetails) {
        Projeto projetoExistente = projetoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));

        atualizarDadosProjeto(projetoDetails, projetoExistente);

        return projetoRepository.save(projetoExistente);
    }

    private void atualizarDadosProjeto(Projeto projetoDetails, Projeto projetoExistente) {
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
    }

    public List<Projeto> findByUsuarioId(Long usuarioId) {
        return projetoRepository.findByUsuarioResponsavelId(usuarioId);
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularHorasDisponiveis(Long projetoId) {
        Projeto projeto = getProjetoById(projetoId);
        if (projeto == null || projeto.getHorasEstimadas() == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal horasUtilizadas = tarefaRepository.findByProjetoId(projetoId).stream()
                .map(Tarefa::getHorasEstimadas)
                .filter(horas -> horas != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return projeto.getHorasEstimadas().subtract(horasUtilizadas);
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularTempoRegistrado(Long projetoId) {
        return tarefaRepository.findByProjetoId(projetoId).stream()
                .map(Tarefa::getTempoRegistrado)
                .filter(tempo -> tempo != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<Projeto> findAllProjetos() {
        return projetoRepository.findAll();
    }

    @Transactional
    public boolean excluirProjeto(Long projetoId) {
        Projeto projeto = projetoRepository.findById(projetoId).orElse(null);
        if (projeto == null) {
            return false;
        }

        LocalDateTime agora = LocalDateTime.now();

        List<Tarefa> tarefas = tarefaRepository.findByProjetoId(projetoId);
        tarefas.forEach(tarefa -> {
            tarefa.setDeletedAt(agora);
            tarefaRepository.save(tarefa);
        });

        List<UsuariosProjetos> associacoes = usuariosProjetosRepository.findByIdProjeto(projetoId);
        associacoes.forEach(associacao -> {
            associacao.setDeletedAt(agora);
            usuariosProjetosRepository.save(associacao);
        });

        projeto.setDeletedAt(agora);
        projetoRepository.save(projeto);

        return true;
    }

    @Transactional
    public Tarefa registrarTempo(Long tarefaId, BigDecimal horas) {
        Tarefa tarefa = tarefaRepository.findById(tarefaId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada"));

        tarefa.adicionarTempoRegistrado(horas);
        tarefa.atualizarCustoRegistrado(tarefa.getTempoRegistrado());
        Tarefa savedTarefa = tarefaRepository.save(tarefa);

        // Recalculate and update project's registered cost
        Projeto projeto = savedTarefa.getProjeto();
        BigDecimal custoTotal = tarefaRepository.findByProjetoId(projeto.getId()).stream()
                .map(Tarefa::getCustoRegistrado)
                .filter(custo -> custo != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        projeto.setCustoRegistrado(custoTotal);
        projetoRepository.save(projeto);

        return savedTarefa;
    }

    // Modify this method to also recalculate project cost
    @Transactional
    public void atualizarTempoRegistradoProjeto(Long projetoId) {
        Projeto projeto = getProjetoById(projetoId);
        BigDecimal tempoTotal = calcularTempoRegistrado(projetoId);
        projeto.atualizarTempoRegistrado(tempoTotal);

        // Recalculate project's registered cost
        BigDecimal custoTotal = tarefaRepository.findByProjetoId(projetoId).stream()
                .map(Tarefa::getCustoRegistrado)
                .filter(custo -> custo != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        projeto.setCustoRegistrado(custoTotal);
        projetoRepository.save(projeto);
    }

    @Transactional
    public void atualizarCustoRegistradoProjeto(Long projetoId) {
        Projeto projeto = getProjetoById(projetoId);
        BigDecimal custoTotal = tarefaRepository.findByProjetoId(projetoId).stream()
                .map(Tarefa::getCustoRegistrado)
                .filter(custo -> custo != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        projeto.setCustoRegistrado(custoTotal);
        projetoRepository.save(projeto);
    }
}