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
        if (usuarioRepository.findByEmail("tiagogorri@gmail.com") == null) {
            Usuario admin = new Usuario();
            admin.setNome("Tiago Kasprzak Gorri");
            admin.setEmail("tiagogorri@gmail.com");
            admin.setSenha(passwordEncoder.encode("tiago123"));
            admin.setPerfil("ADMIN"); // Sem o prefixo ROLE_
            admin.setAtivo(Usuario.AtivoStatus.ATIVO);
            admin.setDataCriacao(LocalDateTime.now());

            usuarioRepository.save(admin);

            System.out.println("Usuário administrador criado com sucesso!");
        } else {
            System.out.println("Usuário administrador já existe.");
        }

        if (usuarioRepository.findByEmail("pedrosilva@gmail.com") == null) {
            Usuario usuario = new Usuario();
            usuario.setNome("Pedro Silva");
            usuario.setEmail("pedrosilva@gmail.com");
            usuario.setSenha(passwordEncoder.encode("pedro123"));
            usuario.setPerfil("USUARIO"); // Sem o prefixo ROLE_
            usuario.setAtivo(Usuario.AtivoStatus.ATIVO);
            usuario.setDataCriacao(LocalDateTime.now());

            usuarioRepository.save(usuario);

            System.out.println("Usuário comum criado com sucesso!");
        } else {
            System.out.println("Usuário comum já existe.");
        }
    }
}