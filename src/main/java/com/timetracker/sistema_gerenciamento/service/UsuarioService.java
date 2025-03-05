package com.timetracker.sistema_gerenciamento.service;

import com.timetracker.sistema_gerenciamento.exception.UsuarioNaoEncontradoException;
import com.timetracker.sistema_gerenciamento.model.Usuario;
import com.timetracker.sistema_gerenciamento.model.UsuariosProjetos;
import com.timetracker.sistema_gerenciamento.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.timetracker.sistema_gerenciamento.repository.UsuariosProjetosRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuariosProjetosRepository usuariosProjetosRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          UsuariosProjetosRepository usuariosProjetosRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.usuariosProjetosRepository = usuariosProjetosRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario salvarUsuario(Usuario usuario) {
        try {
            if (usuario.getPerfil() == null || usuario.getPerfil().trim().isEmpty()) {
                usuario.setPerfil("ROLE_USER");
            }
            if (usuario.getAtivo() == null) {
                usuario.setAtivo(Usuario.AtivoStatus.ATIVO);
            }
            usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
            return usuarioRepository.save(usuario);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar o usuário: " + e.getMessage(), e);
        }
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public boolean verificarCredenciais(String email, String senhaDigitada) {
        Usuario usuario = buscarPorEmail(email);
        if (usuario == null) {
            return false;
        }
        return passwordEncoder.matches(senhaDigitada, usuario.getSenha());
    }

    public List<Usuario> listarTodosUsuarios() {
        return usuarioRepository.findAll();
    }

    public List<Usuario> listarUsuariosAtivos() {
        return usuarioRepository.findByAtivo(Usuario.AtivoStatus.ATIVO);
    }

    public Usuario atualizarUsuario(Long id, Usuario usuarioAtualizado, String senhaAtual) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id));

        if (usuarioAtualizado.getSenha() != null && !usuarioAtualizado.getSenha().isEmpty()) {
            if (senhaAtual == null || !passwordEncoder.matches(senhaAtual, usuarioExistente.getSenha())) {
                throw new RuntimeException("Senha atual incorreta");
            }
            usuarioExistente.setSenha(passwordEncoder.encode(usuarioAtualizado.getSenha()));
        }

        if (usuarioAtualizado.getNome() != null) {
            usuarioExistente.setNome(usuarioAtualizado.getNome());
        }
        if (usuarioAtualizado.getEmail() != null) {
            usuarioExistente.setEmail(usuarioAtualizado.getEmail());
        }
        if (usuarioAtualizado.getPerfil() != null) {
            usuarioExistente.setPerfil(usuarioAtualizado.getPerfil());
        }

        return usuarioRepository.save(usuarioExistente);
    }

    public boolean verificarSenhaAtual(Long id, String senhaAtual) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id));

        return passwordEncoder.matches(senhaAtual, usuario.getSenha());
    }

    public void atualizarUltimoLogin(String email) {
        Usuario usuario = buscarPorEmail(email);
        if (usuario != null) {
            usuario.atualizarUltimoLogin();
            usuarioRepository.save(usuario);
        }
    }
    public void excluirUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id));

        List<UsuariosProjetos> associacoes = usuariosProjetosRepository.findByIdProjeto(id);
        if (!associacoes.isEmpty()) {
            usuariosProjetosRepository.deleteAll(associacoes);
        }

        // Soft delete
        usuario.setAtivo(Usuario.AtivoStatus.INATIVO);
        usuario.setDeletedAt(LocalDateTime.now());
        usuarioRepository.save(usuario);
    }
}