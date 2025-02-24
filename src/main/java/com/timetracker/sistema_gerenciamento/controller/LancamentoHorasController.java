package com.timetracker.sistema_gerenciamento.controller;

import com.timetracker.sistema_gerenciamento.DTO.LancamentoHorasDTO;
import com.timetracker.sistema_gerenciamento.model.LancamentoHoras;
import com.timetracker.sistema_gerenciamento.service.LancamentoHorasService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lancamento")
public class LancamentoHorasController {

    private final LancamentoHorasService lancamentoHorasService;

    public LancamentoHorasController(LancamentoHorasService lancamentoHorasService) {
        this.lancamentoHorasService = lancamentoHorasService;
    }

    @PostMapping
    public ResponseEntity<?> createLaunch(@RequestBody LancamentoHorasDTO lancamentoHorasDTO) {
        System.out.println("Recebido DTO: " + lancamentoHorasDTO);

        if (lancamentoHorasDTO.getIdUsuario() == null || lancamentoHorasDTO.getIdProjeto() == null || lancamentoHorasDTO.getIdTarefa() == null) {
            return new ResponseEntity<>("Usuário, Projeto ou Atividade não informados!", HttpStatus.BAD_REQUEST);
        }

        try {
            LancamentoHoras lancamentoHoras = lancamentoHorasService.salvarLancamento(lancamentoHorasDTO);
            return new ResponseEntity<>(lancamentoHoras, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Erro ao salvar o lançamento de horas", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<LancamentoHoras>> getLancamentosByUsuario(@PathVariable Long usuarioId) {
        try {
            List<LancamentoHoras> lancamentos = lancamentoHorasService.findByUsuarioId(usuarioId);
            return new ResponseEntity<>(lancamentos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}