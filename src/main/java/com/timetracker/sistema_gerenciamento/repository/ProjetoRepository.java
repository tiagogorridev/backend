package com.timetracker.sistema_gerenciamento.repository;

import com.timetracker.sistema_gerenciamento.model.Projeto;  // Adicione esta linha

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjetoRepository extends JpaRepository<Projeto, Long> {
    // Métodos personalizados, se necessário.
}