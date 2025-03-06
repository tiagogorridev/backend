package com.timetracker.sistema_gerenciamento.repository;

import com.timetracker.sistema_gerenciamento.model.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, Long> {
    @Query("SELECT t FROM Tarefa t WHERE t.projeto.id = ?1 AND t.deletedAt IS NULL")
    List<Tarefa> findByProjetoId(Long projetoId);

    @Query("SELECT t FROM Tarefa t WHERE t.id = ?1 AND t.projeto.id = ?2 AND t.deletedAt IS NULL")
    Optional<Tarefa> findByIdAndProjetoId(Long idTarefa, Long idProjeto);

    @Query("SELECT t FROM Tarefa t WHERE t.usuarioResponsavel.id = ?1 AND t.deletedAt IS NULL")
    List<Tarefa> findByUsuarioResponsavelId(Long usuarioId);

}