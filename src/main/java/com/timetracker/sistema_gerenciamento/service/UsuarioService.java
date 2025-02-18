package com.timetracker.sistema_gerenciamento.service;

import com.timetracker.sistema_gerenciamento.model.Usuario;
import com.timetracker.sistema_gerenciamento.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario salvarUsuario(Usuario usuario) {
        try {
            // Define um perfil padrão se não foi especificado
            if (usuario.getPerfil() == null || usuario.getPerfil().trim().isEmpty()) {
                usuario.setPerfil("ROLE_USER");
            }

            // Codifica a senha
            usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
            return usuarioRepository.save(usuario);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao salvar o usuário: " + e.getMessage(), e);
        }
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public boolean verificarCredenciais(String email, String senhaDigitada) {
        Usuario usuario = buscarPorEmail(email);
        if (usuario == null) {
            System.out.println("Usuário não encontrado.");
            return false;
        }

        boolean senhaCorreta = passwordEncoder.matches(senhaDigitada, usuario.getSenha());
        System.out.println("Usuário encontrado: " + email);
        System.out.println("Senha correta? " + senhaCorreta);
        return senhaCorreta;
    }

    public void verificarUsuario(String email) {
        Usuario usuario = buscarPorEmail(email);
        if (usuario != null) {
            System.out.println("Usuário encontrado no banco:");
            System.out.println("Email: " + usuario.getEmail());
            System.out.println("Senha hash: " + usuario.getSenha());
        } else {
            System.out.println("Usuário não encontrado no banco!");
        }
    }

    public List<Usuario> listarTodosUsuarios() {
        return usuarioRepository.findAll();
    }

    // Novo método para retornar todos os e-mails dos usuários
    public List<String> getEmails() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return usuarios.stream()
                .map(Usuario::getEmail)
                .collect(Collectors.toList());
    }
}