package com.timetracker.sistema_gerenciamento.config;

import com.timetracker.sistema_gerenciamento.model.Usuario;
import com.timetracker.sistema_gerenciamento.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.findByEmail("admin@gmail.com") == null) {
            Usuario admin = new Usuario();
            admin.setNome("Administrador");
            admin.setEmail("admin@gmail.com");
            admin.setSenha(passwordEncoder.encode("admin123"));
            admin.setPerfil("ADMIN");
            admin.setAtivo(Usuario.AtivoStatus.ATIVO);
            admin.setDataCriacao(LocalDateTime.now());

            usuarioRepository.save(admin);

            System.out.println("Usuário administrador criado com sucesso!");
        } else {
            System.out.println("Usuário administrador já existe.");
        }
    }
}