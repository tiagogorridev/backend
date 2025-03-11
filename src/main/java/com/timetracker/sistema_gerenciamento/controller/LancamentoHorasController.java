package com.timetracker.sistema_gerenciamento.controller;

import com.timetracker.sistema_gerenciamento.DTO.LancamentoHorasDTO;
import com.timetracker.sistema_gerenciamento.DTO.TimeEntryOverlapRequest;
import com.timetracker.sistema_gerenciamento.model.LancamentoHoras;
import com.timetracker.sistema_gerenciamento.service.LancamentoHorasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/lancamento")
@Tag(name = "Lançamento de Horas", description = "APIs para gerenciamento de lançamentos de horas")

public class LancamentoHorasController {
    @Autowired
    private LancamentoHorasService lancamentoHorasService;

    @Operation(summary = "Listar lançamentos por usuário", description = "Retorna todos os lançamentos de horas de um usuário específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lançamentos encontrados com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<LancamentoHoras>> getLancamentosByUsuario(@PathVariable Long usuarioId) {
        try {
            List<LancamentoHoras> lancamentos = lancamentoHorasService.findByUsuarioId(usuarioId);
            return ResponseEntity.ok(lancamentos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "Listar lançamentos em análise", description = "Retorna todos os lançamentos de horas com status 'EM_ANALISE'")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lançamentos encontrados com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    @GetMapping("/em-analise")
    public ResponseEntity<List<LancamentoHoras>> getLancamentosEmAnalise() {
        try {
            List<LancamentoHoras> lancamentos = lancamentoHorasService.findByStatus("EM_ANALISE");
            return ResponseEntity.ok(lancamentos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "Aprovar lançamento", description = "Atualiza o status de um lançamento de horas para 'APROVADO'")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lançamento aprovado com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    @PutMapping("/aprovar/{lancamentoId}")
    public ResponseEntity<LancamentoHoras> aprovarLancamento(@PathVariable Long lancamentoId) {
        try {
            LancamentoHoras lancamento = lancamentoHorasService.atualizarStatus(lancamentoId, "APROVADO");
            return ResponseEntity.ok(lancamento);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "Rejeitar lançamento", description = "Atualiza o status de um lançamento de horas para 'REPROVADO'")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lançamento rejeitado com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    @PutMapping("/rejeitar/{lancamentoId}")
    public ResponseEntity<LancamentoHoras> rejeitarLancamento(@PathVariable Long lancamentoId) {
        try {
            LancamentoHoras lancamento = lancamentoHorasService.atualizarStatus(lancamentoId, "REPROVADO");
            return ResponseEntity.ok(lancamento);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "Atualizar status de lançamento", description = "Atualiza o status de um lançamento de horas para o valor fornecido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
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

    private static class StatusRequest {
        private String status;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    @Operation(summary = "Verificar sobreposição de horários", description = "Verifica se existe sobreposição de horários para um usuário em uma data e período específicos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verificação realizada com sucesso")
    })
    @PostMapping("/check-overlap")
    public ResponseEntity<Boolean> checkTimeOverlap(@RequestBody TimeEntryOverlapRequest request) {
        // Add logging
        System.out.println("Received overlap check request:");
        System.out.println("User ID: " + request.getUsuarioId());
        System.out.println("Date: " + request.getData());
        System.out.println("Start Time: " + request.getHoraInicio());
        System.out.println("End Time: " + request.getHoraFim());

        boolean hasOverlap = lancamentoHorasService.checkForTimeOverlap(
                request.getUsuarioId(),
                request.getData(),
                request.getHoraInicio(),
                request.getHoraFim()
        );

        System.out.println("Overlap check result: " + hasOverlap);
        return ResponseEntity.ok(hasOverlap);
    }

    @Operation(summary = "Listar todos os lançamentos", description = "Retorna todos os lançamentos de horas cadastrados no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lançamentos listados com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<LancamentoHoras>> getTodosLancamentos() {
        try {
            List<LancamentoHoras> lancamentos = lancamentoHorasService.findAll();
            return ResponseEntity.ok(lancamentos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "Salvar lançamento de horas", description = "Registra um novo lançamento de horas, verificando a existência de sobreposição de horários")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lançamento salvo com sucesso"),
            @ApiResponse(responseCode = "409", description = "Conflito - Já existe um lançamento no período informado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content)
    })    @PostMapping
    public ResponseEntity<?> salvarLancamento(@RequestBody LancamentoHorasDTO lancamentoHorasDTO) {
        try {
            // Convert DTO data to appropriate types for overlap check
            LocalDate data = LocalDate.parse(lancamentoHorasDTO.getData());
            LocalTime horaInicio = LocalTime.parse(lancamentoHorasDTO.getHoraInicio());
            LocalTime horaFim = LocalTime.parse(lancamentoHorasDTO.getHoraFim());

            // Check for time overlap before saving
            boolean hasOverlap = lancamentoHorasService.checkForTimeOverlap(
                    lancamentoHorasDTO.getIdUsuario(),
                    data,
                    horaInicio,
                    horaFim
            );

            if (hasOverlap) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("Já existe um lançamento de horas neste período.");
            }

            // If no overlap, proceed with saving
            LancamentoHoras lancamentoSalvo = lancamentoHorasService.salvarLancamento(lancamentoHorasDTO);
            return ResponseEntity.ok(lancamentoSalvo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}