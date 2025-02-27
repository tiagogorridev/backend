package com.timetracker.sistema_gerenciamento.repository;

import com.timetracker.sistema_gerenciamento.model.LancamentoHoras;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface LancamentoHorasRepository extends JpaRepository<LancamentoHoras, Long> {
    List<LancamentoHoras> findByUsuarioIdOrderByDataDescHoraInicioDesc(Long usuarioId);
    List<LancamentoHoras> findByStatusOrderByDataDesc(String status);

    @Repository
    public interface LancamentosHorasRepository extends JpaRepository<LancamentoHoras, Long> {
        @Query("SELECT COALESCE(SUM(l.horas), 0) FROM LancamentoHoras l WHERE l.projeto.id = ?1 AND l.status = 'APROVADO'")
        BigDecimal calcularTempoTotalPorProjeto(Long projetoId);
    }
}