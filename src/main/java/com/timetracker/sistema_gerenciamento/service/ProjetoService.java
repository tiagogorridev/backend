package com.timetracker.sistema_gerenciamento.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.timetracker.sistema_gerenciamento.repository.ProjetoRepository;
import com.timetracker.sistema_gerenciamento.model.Projeto;

import java.util.List;

@Service
public class ProjetoService {

    @Autowired
    private ProjetoRepository projetoRepository;

    public Projeto save(Projeto projeto) {
        return projetoRepository.save(projeto);
    }

    public List<Projeto> getProjetosPorUsuario(Long idUsuario) {
        return projetoRepository.findByUsuarioResponsavelId(idUsuario);
    }

    public List<Projeto> getTodosProjetosComClientes() {
        return projetoRepository.findAllWithClientes();
    }

    public Projeto atualizarProjeto(Long id, Projeto projetoDetails) {
        Projeto projetoExistente = projetoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));

        projetoExistente.setNome(projetoDetails.getNome());
        projetoExistente.setCliente(projetoDetails.getCliente());
        projetoExistente.setHorasEstimadas(projetoDetails.getHorasEstimadas());
        projetoExistente.setCustoEstimado(projetoDetails.getCustoEstimado());

        return projetoRepository.save(projetoExistente);
    }

    public Projeto getProjetoById(Long id) {
        return projetoRepository.findById(id).orElse(null);
    }
}