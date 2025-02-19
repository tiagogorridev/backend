package com.timetracker.sistema_gerenciamento.repository;

import com.timetracker.sistema_gerenciamento.model.UsuariosProjetos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuariosProjetosRepository extends JpaRepository<UsuariosProjetos, Long> {
    boolean existsByIdUsuarioAndIdProjeto(Long idUsuario, Long idProjeto);
}