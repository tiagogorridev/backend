package com.timetracker.sistema_gerenciamento.repository;

import com.timetracker.sistema_gerenciamento.model.Projeto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjetoRepository extends JpaRepository<Projeto, Long> {

    List<Projeto> findByUsuarioResponsavelId(Long usuarioId);

    @Query("SELECT p FROM Projeto p JOIN FETCH p.cliente")
    List<Projeto> findAllWithClientes();  // Busca todos os projetos com os clientes associados
}