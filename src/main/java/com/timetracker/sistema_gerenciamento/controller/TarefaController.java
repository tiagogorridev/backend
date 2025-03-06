package com.timetracker.sistema_gerenciamento.controller;

import com.timetracker.sistema_gerenciamento.exception.ResourceNotFoundException;
import com.timetracker.sistema_gerenciamento.model.Tarefa;
import com.timetracker.sistema_gerenciamento.model.Projeto;
import com.timetracker.sistema_gerenciamento.repository.TarefaRepository;
import com.timetracker.sistema_gerenciamento.service.ProjetoService;
import com.timetracker.sistema_gerenciamento.service.TarefaService;
import com.timetracker.sistema_gerenciamento.repository.ProjetoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/tarefas")
public class TarefaController {

    @Autowired
    private TarefaService tarefaService;

    @Autowired
    private ProjetoRepository projetoRepository;

    @Autowired
    private ProjetoService projetoService;

    @Autowired
    private TarefaRepository tarefaRepository;

    @GetMapping
    public ResponseEntity<List<Tarefa>> listarTodasTarefas() {
        List<Tarefa> tarefas = tarefaService.listarTodasTarefas();
        return ResponseEntity.ok(tarefas);
    }

    @GetMapping("/projeto/{projetoId}")
    public ResponseEntity<List<Tarefa>> listarTarefasPorProjeto(@PathVariable Long projetoId) {
        if (!projetoRepository.existsById(projetoId)) {
            throw new ResourceNotFoundException("Projeto não encontrado");
        }
        List<Tarefa> tarefas = tarefaService.listarTarefasPorProjeto(projetoId);
        return ResponseEntity.ok(tarefas);
    }

    @PostMapping
    public ResponseEntity<Tarefa> createTarefa(@RequestBody Tarefa tarefa) {
        if (tarefa.getProjeto() == null || tarefa.getProjeto().getId() == null) {
            throw new IllegalArgumentException("Projeto não pode ser nulo");
        }
        Projeto projeto = projetoRepository.findById(tarefa.getProjeto().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Projeto não encontrado"));
        tarefa.setProjeto(projeto);
        Tarefa novaTarefa = tarefaService.salvarTarefa(tarefa);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaTarefa);
    }

    @GetMapping("/projetos/{projectId}/tarefas")
    public ResponseEntity<List<Tarefa>> getProjectTasks(@PathVariable Long projectId) {
        List<Tarefa> tarefas = tarefaService.getTarefasByProjectId(projectId);
        return ResponseEntity.ok(tarefas);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTarefa(@PathVariable Long id) {
        if (!tarefaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tarefa não encontrada");
        }
        tarefaService.deleteTarefa(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/detalhes/{idprojeto}/{idtarefa}")
    public ResponseEntity<Tarefa> getTarefaDetails(@PathVariable Long idprojeto, @PathVariable Long idtarefa) {
        Tarefa tarefa = tarefaService.findTarefaById(idprojeto, idtarefa);
        return ResponseEntity.ok(tarefa);
    }

    @GetMapping("/{tarefaId}/tempo-registrado")
    public ResponseEntity<Map<String, Object>> getTempoRegistrado(@PathVariable Long tarefaId) {
        Tarefa tarefa = tarefaRepository.findById(tarefaId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada"));

        Map<String, Object> response = new HashMap<>();
        response.put("tarefaId", tarefa.getId());
        response.put("nome", tarefa.getNome());
        response.put("tempoRegistrado", tarefa.getTempoRegistrado());
        response.put("horasEstimadas", tarefa.getHorasEstimadas());
        response.put("percentualConcluido", calcularPercentualConcluido(tarefa.getTempoRegistrado(), tarefa.getHorasEstimadas()));

        return ResponseEntity.ok(response);
    }

    private double calcularPercentualConcluido(BigDecimal tempoRegistrado, BigDecimal horasEstimadas) {
        if (horasEstimadas == null || horasEstimadas.compareTo(BigDecimal.ZERO) == 0 || tempoRegistrado == null) {
            return 0;
        }
        return tempoRegistrado.multiply(new BigDecimal(100))
                .divide(horasEstimadas, 2, BigDecimal.ROUND_HALF_UP)
                .doubleValue();
    }

    @PutMapping("/detalhes/{idprojeto}/{idtarefa}")
    public ResponseEntity<Tarefa> updateTarefa(@PathVariable Long idprojeto, @PathVariable Long idtarefa, @RequestBody Tarefa tarefaAtualizada) {
        Projeto projeto = projetoRepository.findById(idprojeto)
                .orElseThrow(() -> new ResourceNotFoundException("Projeto não encontrado"));

        Tarefa tarefa = tarefaRepository.findByIdAndProjetoId(idtarefa, idprojeto)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada"));

        tarefa.setNome(tarefaAtualizada.getNome());
        tarefa.setDescricao(tarefaAtualizada.getDescricao());
        tarefa.setDataInicio(tarefaAtualizada.getDataInicio());
        tarefa.setDataFim(tarefaAtualizada.getDataFim());
        tarefa.setStatus(tarefaAtualizada.getStatus());
        tarefa.setHorasEstimadas(tarefaAtualizada.getHorasEstimadas());
        tarefa.setValorPorHora(tarefaAtualizada.getValorPorHora());

        Tarefa tarefaSalva = tarefaRepository.save(tarefa);

        return ResponseEntity.ok(tarefaSalva);
    }

    @PutMapping("/{tarefaId}/registrar-tempo")
    public ResponseEntity<Tarefa> registrarTempo(@PathVariable Long tarefaId, @RequestBody Map<String, BigDecimal> payload) {
        BigDecimal horas = payload.get("horas");

        // Add logging
        System.out.println("Registrando tempo para tarefa: " + tarefaId);
        System.out.println("Horas a registrar: " + horas);

        // Validate input
        if (horas == null || horas.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Horas inválidas. Deve ser um valor positivo.");
        }

        Tarefa tarefaAtualizada = tarefaService.registrarTempo(tarefaId, horas);

        // Additional logging to verify update
        System.out.println("Tempo registrado: " + tarefaAtualizada.getTempoRegistrado());
        System.out.println("Custo registrado: " + tarefaAtualizada.getCustoRegistrado());

        return ResponseEntity.ok(tarefaAtualizada);
    }

    @PutMapping("/projeto/{projetoId}/atualizar-custo")
    public ResponseEntity<Map<String, Object>> atualizarCustoProjeto(@PathVariable Long projetoId) {
        try {
            Projeto projeto = projetoService.getProjetoById(projetoId);
            if (projeto == null) {
                return ResponseEntity.notFound().build();
            }

            // Calculando o custo total diretamente
            BigDecimal custoTotal = BigDecimal.ZERO;
            List<Tarefa> tarefas = tarefaRepository.findByProjetoId(projetoId);

            for (Tarefa tarefa : tarefas) {
                if (tarefa.getCustoRegistrado() != null) {
                    custoTotal = custoTotal.add(tarefa.getCustoRegistrado());
                }
            }

            // Atualizando o projeto
            projeto.setCustoRegistrado(custoTotal);
            projetoRepository.save(projeto);

            Map<String, Object> response = new HashMap<>();
            response.put("projetoId", projetoId);
            response.put("custoRegistrado", custoTotal);
            response.put("mensagem", "Custo do projeto atualizado com sucesso");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("erro", "Falha ao atualizar custo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}