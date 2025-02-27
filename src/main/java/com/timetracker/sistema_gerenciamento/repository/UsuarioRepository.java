package com.timetracker.sistema_gerenciamento.repository;

import com.timetracker.sistema_gerenciamento.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByEmail(String email);
    List<Usuario> findByAtivo(String ativo);
}