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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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


    public List<Usuario> getMembrosProjetoPorId(Long projetoId) {
        return usuariosProjetosRepository.findActiveUsersByProjectId(projetoId);
    }

    public List<Projeto> getProjetosAssociados(Long usuarioId) {
        List<UsuariosProjetos> associacoes = usuariosProjetosRepository.findByIdUsuario(usuarioId);

        if (associacoes.isEmpty()) {
            throw new RuntimeException("Este usuário não está associado a nenhum projeto.");
        }

        return associacoes.stream()
                .map(associacao -> projetoRepository.findById(associacao.getIdProjeto())
                        .orElseThrow(() -> new RuntimeException("Projeto não encontrado com ID " + associacao.getIdProjeto())))
                .collect(Collectors.toList());
    }

    public void removerUsuarioDoProjeto(Long idUsuario, Long idProjeto) {
        UsuariosProjetos associacao = usuariosProjetosRepository
                .findByIdUsuarioAndIdProjeto(idUsuario, idProjeto)
                .orElseThrow(() -> new RuntimeException("Associação não encontrada"));

        usuariosProjetosRepository.delete(associacao);
    }

    public List<UsuariosProjetos> listarTodasAssociacoes() {
        return usuariosProjetosRepository.findAll();
    }

    public List<UsuariosProjetos> listarUsuariosProjetos() {
        List<UsuariosProjetos> associacoes = usuariosProjetosRepository.findAll();

        if (associacoes.isEmpty()) {
            throw new RuntimeException("Nenhuma associação encontrada");
        }

        return associacoes;
    }

    public List<String> listarEmailsUsuariosComProjetos() {
        // Busca todas as associações com deletedAt nulo
        List<UsuariosProjetos> associacoes = usuariosProjetosRepository.findAll()
                .stream()
                .filter(up -> up.getDeletedAt() == null)
                .collect(Collectors.toList());

        if (associacoes.isEmpty()) {
            throw new RuntimeException("Nenhuma associação de usuário com projeto encontrada");
        }

        // Extrai IDs de usuários únicos das associações
        Set<Long> idsUsuarios = associacoes.stream()
                .map(UsuariosProjetos::getIdUsuario)
                .collect(Collectors.toSet());

        // Busca os usuários pelo ID e extrai os emails
        return idsUsuarios.stream()
                .map(id -> usuarioRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID " + id)))
                .map(Usuario::getEmail)
                .collect(Collectors.toList());
    }


    public List<Projeto> getProjetosPorUsuario(Long usuarioId) {
        // Chama o repositório para buscar os projetos do usuário
        return usuariosProjetosRepository.findProjetosByUsuarioId(usuarioId);
    }
}