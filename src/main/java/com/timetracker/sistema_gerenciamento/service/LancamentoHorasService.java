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
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        return lancamentoHorasRepository.save(lancamentoHoras);
    }
}