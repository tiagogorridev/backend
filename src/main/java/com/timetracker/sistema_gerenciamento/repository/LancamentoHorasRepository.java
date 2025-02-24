package com.timetracker.sistema_gerenciamento.repository;

import com.timetracker.sistema_gerenciamento.model.LancamentoHoras;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LancamentoHorasRepository extends JpaRepository<LancamentoHoras, Long> {
    List<LancamentoHoras> findByUsuarioIdOrderByDataDescHoraInicioDesc(Long usuarioId);
}