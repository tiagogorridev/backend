package com.timetracker.sistema_gerenciamento.repository;

import com.timetracker.sistema_gerenciamento.model.Projeto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjetoRepository extends JpaRepository<Projeto, Long> {
    @Query("SELECT p FROM Projeto p LEFT JOIN FETCH p.cliente WHERE p.id = :id")
    Optional<Projeto> findProjetoWithClienteById(@Param("id") Long id);

    List<Projeto> findByUsuarioResponsavelId(Long usuarioId);

    @Query("SELECT p FROM Projeto p JOIN FETCH p.cliente")
    List<Projeto> findAllWithClientes();
}