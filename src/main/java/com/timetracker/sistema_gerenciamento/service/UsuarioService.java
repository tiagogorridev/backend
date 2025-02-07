package com.timetracker.sistema_gerenciamento.service;

import com.timetracker.sistema_gerenciamento.model.Usuario;
import com.timetracker.sistema_gerenciamento.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario salvarUsuario(Usuario usuario) {
        try {
            return usuarioRepository.save(usuario); // Salva o usuário no banco de dados
        } catch (Exception e) {
            e.printStackTrace(); // Imprime o erro no log
            throw new RuntimeException("Erro ao salvar o usuário", e); // Lança uma exceção para ser tratada pelo controlador
        }
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }
}