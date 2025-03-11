package com.timetracker.sistema_gerenciamento.controller;

import com.timetracker.sistema_gerenciamento.exception.ResourceNotFoundException;
import com.timetracker.sistema_gerenciamento.model.Tarefa;
import com.timetracker.sistema_gerenciamento.model.Projeto;
import com.timetracker.sistema_gerenciamento.repository.TarefaRepository;
import com.timetracker.sistema_gerenciamento.service.ProjetoService;
import com.timetracker.sistema_gerenciamento.service.TarefaService;
import com.timetracker.sistema_gerenciamento.repository.ProjetoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Tarefas", description = "Gerenciamento de Tarefas") // Adicionada a tag aqui

public class TarefaController {

    @Autowired
    private TarefaService tarefaService;

    @Autowired
    private ProjetoRepository projetoRepository;

    @Autowired
    private ProjetoService projetoService;

    @Autowired
    private TarefaRepository tarefaRepository;

    @Operation(summary = "Lista todas as tarefas", description = "Retorna todas as tarefas cadastradas")
    @GetMapping
    public ResponseEntity<List<Tarefa>> listarTodasTarefas() {
        List<Tarefa> tarefas = tarefaService.listarTodasTarefas();
        return ResponseEntity.ok(tarefas);
    }

    @Operation(summary = "Lista as tarefas de um projeto", description = "Retorna todas as tarefas associadas a um projeto específico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tarefas encontradas"),
            @ApiResponse(responseCode = "404", description = "Projeto não encontrado")
    })
    @GetMapping("/projeto/{projetoId}")
    public ResponseEntity<List<Tarefa>> listarTarefasPorProjeto(@PathVariable Long projetoId) {
        if (!projetoRepository.existsById(projetoId)) {
            throw new ResourceNotFoundException("Projeto não encontrado");
        }
        List<Tarefa> tarefas = tarefaService.listarTarefasPorProjeto(projetoId);
        return ResponseEntity.ok(tarefas);
    }

    @Operation(summary = "Cria uma nova tarefa", description = "Cadastra uma nova tarefa associada a um projeto")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tarefa criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida")
    })
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

    @Operation(summary = "Lista as tarefas de um projeto específico", description = "Retorna todas as tarefas associadas a um projeto por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tarefas encontradas"),
            @ApiResponse(responseCode = "404", description = "Projeto não encontrado")
    })
    @GetMapping("/projetos/{projectId}/tarefas")
    public ResponseEntity<List<Tarefa>> getProjectTasks(@PathVariable Long projectId) {
        List<Tarefa> tarefas = tarefaService.getTarefasByProjectId(projectId);
        return ResponseEntity.ok(tarefas);
    }

    @Operation(summary = "Deleta uma tarefa", description = "Deleta uma tarefa existente pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tarefa deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTarefa(@PathVariable Long id) {
        if (!tarefaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tarefa não encontrada");
        }
        tarefaService.deleteTarefa(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obtém os detalhes de uma tarefa", description = "Retorna os detalhes de uma tarefa específica a partir do projeto e do ID da tarefa")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Detalhes da tarefa encontrados"),
            @ApiResponse(responseCode = "404", description = "Tarefa ou projeto não encontrado")
    })
    @GetMapping("/detalhes/{idprojeto}/{idtarefa}")
    public ResponseEntity<Tarefa> getTarefaDetails(@PathVariable Long idprojeto, @PathVariable Long idtarefa) {
        Tarefa tarefa = tarefaService.findTarefaById(idprojeto, idtarefa);
        return ResponseEntity.ok(tarefa);
    }

    @Operation(summary = "Obtém o tempo registrado de uma tarefa", description = "Retorna o tempo registrado, horas estimadas e o percentual de conclusão de uma tarefa")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Informações da tarefa encontradas"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    })
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

    @Operation(summary = "Atualiza os dados de uma tarefa", description = "Atualiza as informações de uma tarefa existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tarefa atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tarefa ou projeto não encontrado")
    })
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

    @Operation(summary = "Lista as tarefas de um usuário", description = "Retorna todas as tarefas associadas a um usuário específico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tarefas encontradas"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Tarefa>> listarTarefasPorUsuario(@PathVariable Long usuarioId) {
        List<Tarefa> tarefas = tarefaService.listarTarefasPorUsuario(usuarioId);
        return ResponseEntity.ok(tarefas);
    }

    @Operation(summary = "Lista as tarefas de um usuário por projeto", description = "Retorna todas as tarefas associadas aos projetos de um usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tarefas encontradas"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping("/projetos-usuario/{usuarioId}")
    public ResponseEntity<List<Tarefa>> getTarefasPorProjetoDoUsuario(@PathVariable Long usuarioId) {
        List<Tarefa> tarefas = tarefaService.listarTarefasPorProjetosDoUsuario(usuarioId);
        return ResponseEntity.ok(tarefas);
    }
}