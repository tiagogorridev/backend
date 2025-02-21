package com.timetracker.sistema_gerenciamento.repository;

import com.timetracker.sistema_gerenciamento.model.UsuariosProjetos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuariosProjetosRepository extends JpaRepository<UsuariosProjetos, Long> {
    boolean existsByIdUsuarioAndIdProjeto(Long idUsuario, Long idProjeto);

    List<UsuariosProjetos> findByIdProjeto(Long idProjeto);

    List<UsuariosProjetos> findByIdUsuario(Long idUsuario);

    Optional<UsuariosProjetos> findByIdUsuarioAndIdProjeto(Long idUsuario, Long idProjeto);

}