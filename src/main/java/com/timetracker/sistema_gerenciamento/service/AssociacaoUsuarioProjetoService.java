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
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProjetoRepository projetoRepository;

    @Autowired
    private UsuariosProjetosRepository usuariosProjetosRepository;

    public void associarUsuarioAoProjeto(Long idUsuario, Long idProjeto) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Projeto projeto = projetoRepository.findById(idProjeto)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));

        if (usuariosProjetosRepository.existsByIdUsuarioAndIdProjeto(idUsuario, idProjeto)) {
            throw new RuntimeException("O usuário já está associado a este projeto");
        }

        UsuariosProjetos associacao = new UsuariosProjetos();
        associacao.setIdUsuario(idUsuario);
        associacao.setIdProjeto(idProjeto);
        associacao.setDataAssociacao(LocalDateTime.now());

        usuariosProjetosRepository.save(associacao);
    }
}