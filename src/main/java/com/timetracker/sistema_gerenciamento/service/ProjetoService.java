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
        return projetoRepository.findAllWithClientes(); // Retorna projetos já com os clientes
    }
}