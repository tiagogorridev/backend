package com.timetracker.sistema_gerenciamento.service;

import com.timetracker.sistema_gerenciamento.model.Projeto;
import com.timetracker.sistema_gerenciamento.model.Usuario;
import com.timetracker.sistema_gerenciamento.model.UsuariosProjetos;
import com.timetracker.sistema_gerenciamento.repository.ProjetoRepository;
import com.timetracker.sistema_gerenciamento.repository.UsuarioRepository;
import com.timetracker.sistema_gerenciamento.repository.UsuariosProjetosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AssociacaoUsuarioProjetoService {

    @Autowired
    private UsuarioRepository usuarioRepository;  // Repositório de usuários
    @Autowired
    private ProjetoRepository projetoRepository;  // Repositório de projetos
    @Autowired
    private UsuariosProjetosRepository usuariosProjetosRepository;  // Repositório de associação

    public void associarUsuarioAoProjeto(Long idUsuario, Long idProjeto) {
        // Verifica se o usuário e o projeto existem
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        Projeto projeto = projetoRepository.findById(idProjeto)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));

        // Verifica se a associação já existe
        if (usuariosProjetosRepository.existsByIdUsuarioAndIdProjeto(idUsuario, idProjeto)) {
            throw new RuntimeException("O usuário já está associado a este projeto");
        }

        // Cria a associação entre usuário e projeto
        UsuariosProjetos associacao = new UsuariosProjetos();
        associacao.setIdUsuario(idUsuario);
        associacao.setIdProjeto(idProjeto);
        associacao.setDataAssociacao(LocalDateTime.now());

        // Salva a associação
        usuariosProjetosRepository.save(associacao);
    }
}