package com.timetracker.sistema_gerenciamento.repository;

import com.timetracker.sistema_gerenciamento.model.Projeto;
import com.timetracker.sistema_gerenciamento.model.UsuariosProjetos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuariosProjetosRepository extends JpaRepository<UsuariosProjetos, Long> {

    @Query("SELECT up FROM UsuariosProjetos up WHERE up.idUsuario = ?1 AND up.idProjeto = ?2 AND up.deletedAt IS NULL")
    Optional<UsuariosProjetos> findByIdUsuarioAndIdProjeto(Long idUsuario, Long idProjeto);

    @Query("SELECT up FROM UsuariosProjetos up WHERE up.idProjeto = ?1 AND up.deletedAt IS NULL")
    List<UsuariosProjetos> findByIdProjeto(Long idProjeto);

    @Query("SELECT up FROM UsuariosProjetos up WHERE up.idUsuario = ?1 AND up.deletedAt IS NULL")
    List<UsuariosProjetos> findByIdUsuario(Long idUsuario);

    @Query("SELECT CASE WHEN COUNT(up) > 0 THEN true ELSE false END FROM UsuariosProjetos up WHERE up.idUsuario = ?1 AND up.idProjeto = ?2 AND up.deletedAt IS NULL")
    boolean existsByIdUsuarioAndIdProjeto(Long idUsuario, Long idProjeto);

    @Query("SELECT p FROM Projeto p JOIN p.usuarios u WHERE u.id = :usuarioId")
    List<Projeto> findProjetosByUsuarioId(@Param("usuarioId") Long usuarioId);
}