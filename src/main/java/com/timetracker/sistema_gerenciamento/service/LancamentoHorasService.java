package com.timetracker.sistema_gerenciamento.service;

import com.timetracker.sistema_gerenciamento.DTO.LancamentoHorasDTO;
import com.timetracker.sistema_gerenciamento.model.LancamentoHoras;
import com.timetracker.sistema_gerenciamento.model.Projeto;
import com.timetracker.sistema_gerenciamento.model.Tarefa;
import com.timetracker.sistema_gerenciamento.model.Usuario;
import com.timetracker.sistema_gerenciamento.repository.LancamentoHorasRepository;
import com.timetracker.sistema_gerenciamento.repository.ProjetoRepository;
import com.timetracker.sistema_gerenciamento.repository.TarefaRepository;
import com.timetracker.sistema_gerenciamento.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class LancamentoHorasService {

    @Autowired
    private LancamentoHorasRepository lancamentoHorasRepository;
    @Autowired
    private TarefaRepository tarefaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ProjetoRepository projetoRepository;


    @Transactional
    public LancamentoHoras salvarLancamento(LancamentoHorasDTO lancamentoHorasDTO) {
        Tarefa tarefa = tarefaRepository.findById(lancamentoHorasDTO.getIdTarefa())
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));
        Usuario usuario = usuarioRepository.findById(lancamentoHorasDTO.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        Projeto projeto = projetoRepository.findById(lancamentoHorasDTO.getIdProjeto())
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));

        LancamentoHoras lancamentoHoras = new LancamentoHoras();
        lancamentoHoras.setTarefa(tarefa);
        lancamentoHoras.setUsuario(usuario);
        lancamentoHoras.setProjeto(projeto);
        lancamentoHoras.setHoras(lancamentoHorasDTO.getHoras());
        lancamentoHoras.setData(LocalDate.parse(lancamentoHorasDTO.getData()));
        lancamentoHoras.setHoraInicio(LocalTime.parse(lancamentoHorasDTO.getHoraInicio()));
        lancamentoHoras.setHoraFim(LocalTime.parse(lancamentoHorasDTO.getHoraFim()));
        lancamentoHoras.setDescricao(lancamentoHorasDTO.getDescricao());
        lancamentoHoras.setStatus("EM_ANALISE");

        return lancamentoHorasRepository.save(lancamentoHoras);
    }

    public List<LancamentoHoras> findByUsuarioId(Long usuarioId) {
        return lancamentoHorasRepository.findByUsuarioIdOrderByDataDescHoraInicioDesc(usuarioId);
    }

    public List<LancamentoHoras> findByStatus(String status) {
        return lancamentoHorasRepository.findByStatusOrderByDataDesc(status);
    }

    @Transactional
    public LancamentoHoras atualizarStatus(Long lancamentoId, String novoStatus) {
        LancamentoHoras lancamento = lancamentoHorasRepository.findById(lancamentoId)
                .orElseThrow(() -> new RuntimeException("Lançamento não encontrado"));

        if (!novoStatus.equals("APROVADO") && !novoStatus.equals("REPROVADO") && !novoStatus.equals("EM_ANALISE")) {
            throw new RuntimeException("Status inválido");
        }

        String statusAnterior = lancamento.getStatus();
        lancamento.setStatus(novoStatus);

        if (novoStatus.equals("APROVADO") && !statusAnterior.equals("APROVADO")) {
            Tarefa tarefa = lancamento.getTarefa();
            BigDecimal horasLancamento = BigDecimal.valueOf(lancamento.getHoras());
            tarefa.adicionarTempoRegistrado(horasLancamento);
            tarefaRepository.save(tarefa);
        } else if (statusAnterior.equals("APROVADO") && novoStatus.equals("REPROVADO")) {
            Tarefa tarefa = lancamento.getTarefa();
            BigDecimal horasLancamento = BigDecimal.valueOf(lancamento.getHoras());
            BigDecimal novoTempoRegistrado = tarefa.getTempoRegistrado().subtract(horasLancamento);
            tarefa.setTempoRegistrado(novoTempoRegistrado.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : novoTempoRegistrado);
            tarefaRepository.save(tarefa);
        }

        return lancamentoHorasRepository.save(lancamento);
    }

    public boolean checkForTimeOverlap(Long usuarioId, LocalDate data, LocalTime horaInicio, LocalTime horaFim) {
        System.out.println("Checking time overlap for:");
        System.out.println("User ID: " + usuarioId);
        System.out.println("Date: " + data);
        System.out.println("Start Time: " + horaInicio);
        System.out.println("End Time: " + horaFim);

        // Find all time entries for the user on the specified date
        List<LancamentoHoras> existingEntries = lancamentoHorasRepository
                .findByUsuarioIdAndDataAndStatusNot(usuarioId, data, "REPROVADO");

        System.out.println("Existing entries: " + existingEntries.size());

        boolean hasOverlap = existingEntries.stream().anyMatch(entry -> {
            boolean overlap = !(horaFim.isBefore(entry.getHoraInicio()) || horaInicio.isAfter(entry.getHoraFim()));
            System.out.println("Checking entry: " + entry.getId() + ", Overlap: " + overlap);
            return overlap;
        });

        System.out.println("Final overlap result: " + hasOverlap);
        return hasOverlap;
    }
}