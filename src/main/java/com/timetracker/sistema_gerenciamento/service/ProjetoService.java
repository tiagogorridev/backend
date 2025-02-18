package com.timetracker.sistema_gerenciamento.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.timetracker.sistema_gerenciamento.repository.ProjetoRepository;
import com.timetracker.sistema_gerenciamento.model.Projeto;
import com.timetracker.sistema_gerenciamento.model.Status;
import com.timetracker.sistema_gerenciamento.model.Prioridade;

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

        // Atualizando o nome do projeto
        if (projetoDetails.getNome() != null && !projetoDetails.getNome().isEmpty()) {
            projetoExistente.setNome(projetoDetails.getNome());
        }

        // Atualizando o cliente do projeto
        if (projetoDetails.getCliente() != null && projetoDetails.getCliente().getId() != null) {
            projetoExistente.setCliente(projetoDetails.getCliente());
        }

        // Atualizando as horas e custo estimado
        if (projetoDetails.getHorasEstimadas() != null) {
            projetoExistente.setHorasEstimadas(projetoDetails.getHorasEstimadas());
        }

        if (projetoDetails.getCustoEstimado() != null) {
            projetoExistente.setCustoEstimado(projetoDetails.getCustoEstimado());
        }

        // Atualizando o status (sem usar valueOf se já for do tipo Status)
        if (projetoDetails.getStatus() != null) {
            projetoExistente.setStatus(projetoDetails.getStatus()); // Prioridade já é do tipo enum
        }

        // Atualizando a prioridade (garantindo que a prioridade seja válida)
        if (projetoDetails.getPrioridade() != null) {
            projetoExistente.setPrioridade(projetoDetails.getPrioridade()); // Prioridade já é do tipo enum
        }

        // Salvando o projeto atualizado
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