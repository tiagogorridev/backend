package com.timetracker.sistema_gerenciamento.repository;

import com.timetracker.sistema_gerenciamento.model.Projeto;  // Adicione esta linha

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjetoRepository extends JpaRepository<Projeto, Long> {
    List<Projeto> findByUsuarioResponsavelId(Long usuarioId);
}