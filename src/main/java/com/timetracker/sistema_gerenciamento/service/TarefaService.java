package com.timetracker.sistema_gerenciamento.service;

import com.timetracker.sistema_gerenciamento.exception.ResourceNotFoundException;
import com.timetracker.sistema_gerenciamento.model.Projeto;
import com.timetracker.sistema_gerenciamento.model.Tarefa;
import com.timetracker.sistema_gerenciamento.repository.TarefaRepository;
import com.timetracker.sistema_gerenciamento.repository.ProjetoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TarefaService {

    @Autowired
    private TarefaRepository tarefaRepository;

    @Autowired
    private ProjetoRepository projetoRepository;

    @Autowired
    private ProjetoService projetoService;

    public List<Tarefa> listarTarefasPorProjeto(Long projetoId) {
        return tarefaRepository.findByProjetoId(projetoId);
    }

    public List<Tarefa> listarTodasTarefas() {
        return tarefaRepository.findAll();
    }

    public Tarefa salvarTarefa(Tarefa tarefa) {
        BigDecimal horasDisponiveis = calcularHorasDisponiveisProjeto(tarefa.getProjeto().getId());

        if (tarefa.getHorasEstimadas().compareTo(horasDisponiveis) > 0) {
            throw new IllegalArgumentException("Horas estimadas excedem o limite disponível do projeto");
        }

        Projeto projeto = tarefa.getProjeto();
        if (tarefa.getDataInicio().isBefore(projeto.getDataInicio()) || tarefa.getDataFim().isAfter(projeto.getDataFim())) {
            throw new IllegalArgumentException("As datas da tarefa devem estar dentro do intervalo do projeto.");
        }

        return tarefaRepository.save(tarefa);
    }

    public List<Tarefa> getTarefasByProjectId(Long projectId) {
        return tarefaRepository.findByProjetoId(projectId);
    }

    @Transactional
    public void deleteTarefa(Long id) {
        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada"));

        tarefa.setDeletedAt(LocalDateTime.now());
        tarefaRepository.save(tarefa);
    }

    private BigDecimal calcularHorasDisponiveisProjeto(Long projetoId) {
        Projeto projeto = projetoRepository.findById(projetoId)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));

        List<Tarefa> tarefas = tarefaRepository.findByProjetoId(projetoId);

        BigDecimal horasUtilizadas = tarefas.stream()
                .map(Tarefa::getHorasEstimadas)
                .filter(horas -> horas != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return projeto.getHorasEstimadas().subtract(horasUtilizadas);
    }

    public Tarefa findTarefaById(Long idprojeto, Long idtarefa) {
        return tarefaRepository.findByIdAndProjetoId(idtarefa, idprojeto)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada"));
    }

    @Transactional
    public Tarefa registrarTempo(Long tarefaId, BigDecimal horas) {
        Tarefa tarefa = tarefaRepository.findById(tarefaId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada"));

        // Validate valor por hora
        if (tarefa.getValorPorHora() == null) {
            throw new IllegalStateException("Valor por hora não definido para a tarefa");
        }

        // Validate input
        if (horas == null || horas.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Horas inválidas. Deve ser um valor positivo.");
        }

        // Adicionar tempo registrado
        tarefa.adicionarTempoRegistrado(horas);

        // Calcular custo registrado uma única vez
        BigDecimal custoRegistrado = tarefa.getValorPorHora().multiply(tarefa.getTempoRegistrado());
        tarefa.setCustoRegistrado(custoRegistrado);

        // Salvar a tarefa
        Tarefa tarefaSalva = tarefaRepository.save(tarefa);
        tarefaRepository.flush();

        // Atualizar o custo do projeto
        projetoService.atualizarCustoRegistradoProjeto(tarefa.getProjeto().getId());
        atualizarCustoProjetoEmNovaTransacao(tarefa.getProjeto().getId());

        // Log for debugging
        System.out.println("Tempo registrado: " + tarefaSalva.getTempoRegistrado());
        System.out.println("Valor por hora: " + tarefaSalva.getValorPorHora());
        System.out.println("Custo registrado: " + tarefaSalva.getCustoRegistrado());

        return tarefaSalva;
    }

    public BigDecimal calcularCustoTarefa(Tarefa tarefa) {
        if (tarefa.getValorPorHora() == null || tarefa.getTempoRegistrado() == null) {
            return BigDecimal.ZERO;
        }
        return tarefa.getValorPorHora().multiply(tarefa.getTempoRegistrado());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void atualizarCustoProjetoEmNovaTransacao(Long projetoId) {
        projetoService.atualizarCustoRegistradoProjeto(projetoId);
    }
}