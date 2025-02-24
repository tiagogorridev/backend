package com.timetracker.sistema_gerenciamento.controller;

import com.timetracker.sistema_gerenciamento.DTO.LancamentoHorasDTO;
import com.timetracker.sistema_gerenciamento.model.LancamentoHoras;
import com.timetracker.sistema_gerenciamento.service.LancamentoHorasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lancamento")
public class LancamentoHorasController {

    @Autowired
    private LancamentoHorasService lancamentoHorasService;

    @PostMapping
    public ResponseEntity<LancamentoHoras> salvarLancamento(@RequestBody LancamentoHorasDTO lancamentoHorasDTO) {
        try {
            LancamentoHoras lancamentoSalvo = lancamentoHorasService.salvarLancamento(lancamentoHorasDTO);
            return ResponseEntity.ok(lancamentoSalvo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<LancamentoHoras>> getLancamentosByUsuario(@PathVariable Long usuarioId) {
        try {
            List<LancamentoHoras> lancamentos = lancamentoHorasService.findByUsuarioId(usuarioId);
            return ResponseEntity.ok(lancamentos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/em-analise")
    public ResponseEntity<List<LancamentoHoras>> getLancamentosEmAnalise() {
        try {
            List<LancamentoHoras> lancamentos = lancamentoHorasService.findByStatus("EM_ANALISE");
            return ResponseEntity.ok(lancamentos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/aprovar/{lancamentoId}")
    public ResponseEntity<LancamentoHoras> aprovarLancamento(@PathVariable Long lancamentoId) {
        try {
            LancamentoHoras lancamento = lancamentoHorasService.atualizarStatus(lancamentoId, "APROVADO");
            return ResponseEntity.ok(lancamento);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/rejeitar/{lancamentoId}")
    public ResponseEntity<LancamentoHoras> rejeitarLancamento(@PathVariable Long lancamentoId) {
        try {
            LancamentoHoras lancamento = lancamentoHorasService.atualizarStatus(lancamentoId, "REJEITADO");
            return ResponseEntity.ok(lancamento);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{lancamentoId}/status")
    public ResponseEntity<LancamentoHoras> atualizarStatus(
            @PathVariable Long lancamentoId,
            @RequestBody StatusRequest statusRequest) {
        try {
            LancamentoHoras lancamento = lancamentoHorasService.atualizarStatus(lancamentoId, statusRequest.getStatus());
            return ResponseEntity.ok(lancamento);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Classe interna para receber a requisição de atualização de status
    private static class StatusRequest {
        private String status;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}