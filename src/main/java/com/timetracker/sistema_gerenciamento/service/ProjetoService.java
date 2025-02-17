package com.timetracker.sistema_gerenciamento.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.timetracker.sistema_gerenciamento.repository.ProjetoRepository;
import com.timetracker.sistema_gerenciamento.model.Projeto;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjetoService {

    @Autowired
    private ProjetoRepository projetoRepository;

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

        Projeto projetoAtualizado = projetoRepository.save(projetoExistente);
        projetoRepository.flush();

        return projetoAtualizado;
    }

    @Transactional
    public Projeto getProjetoById(Long id) {
        return projetoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));
    }

    public List<Projeto> getProjetosPorUsuario(Long usuarioId) {
        return projetoRepository.findByUsuarioResponsavelId(usuarioId);
    }
}