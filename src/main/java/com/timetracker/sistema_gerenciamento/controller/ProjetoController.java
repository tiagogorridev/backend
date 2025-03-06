package com.timetracker.sistema_gerenciamento.controller;

import com.timetracker.sistema_gerenciamento.model.*;
import com.timetracker.sistema_gerenciamento.repository.ClienteRepository;
import com.timetracker.sistema_gerenciamento.repository.UsuarioRepository;
import com.timetracker.sistema_gerenciamento.service.AssociacaoUsuarioProjetoService;
import com.timetracker.sistema_gerenciamento.service.ProjetoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/projetos")
public class ProjetoController {
    @Autowired
    private ProjetoService projetoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private AssociacaoUsuarioProjetoService associacaoUsuarioProjetoService;

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProjeto(@PathVariable Long id, @RequestBody Projeto projeto) {
        Projeto existingProjeto = projetoService.getProjetoById(id);
        if (existingProjeto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Projeto não encontrado");
        }

        if (projeto.getNome() != null && !projeto.getNome().isEmpty()) {
            existingProjeto.setNome(projeto.getNome());
        }

        if (projeto.getCliente() != null && projeto.getCliente().getId() != null) {}

        if (projeto.getHorasEstimadas() != null) {
            existingProjeto.setHorasEstimadas(projeto.getHorasEstimadas());
        }

        if (projeto.getCustoEstimado() != null) {
            existingProjeto.setCustoEstimado(projeto.getCustoEstimado());
        }

        if (projeto.getStatus() != null) {
            existingProjeto.setStatus(projeto.getStatus());
        }

        if (projeto.getPrioridade() != null) {
            existingProjeto.setPrioridade(projeto.getPrioridade());
        }

        if (projeto.getDataInicio() != null) {
            existingProjeto.setDataInicio(projeto.getDataInicio());
        }

        if (projeto.getDataFim() != null) {
            existingProjeto.setDataFim(projeto.getDataFim());
        }

        projetoService.save(existingProjeto);
        return ResponseEntity.ok(existingProjeto);
    }

    @PostMapping
    public Projeto createProjeto(@RequestBody Projeto projeto) {
        Long usuarioId = projeto.getUsuarioResponsavel().getId();
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        projeto.setUsuarioResponsavel(usuario);
        projeto.setDataCriacao(LocalDateTime.now());

        if (projeto.getCliente().getId() == null) {
            Cliente cliente = projeto.getCliente();
            clienteRepository.save(cliente);
        }

        return projetoService.save(projeto);
    }

    @GetMapping("/usuario/{usuarioId}/projetos")
    public ResponseEntity<List<Projeto>> getProjetosDoUsuario(@PathVariable Long usuarioId) {
        List<Projeto> projetos = projetoService.getProjetosAtivosDoUsuario(usuarioId);
        return ResponseEntity.ok(projetos);
    }

    @GetMapping("/usuario/{usuarioId}/associacoes")
    public ResponseEntity<List<Projeto>> getProjetosAssociadosDoUsuario(@PathVariable Long usuarioId) {
        List<Projeto> projetos = associacaoUsuarioProjetoService.getProjetosAssociados(usuarioId);
        return ResponseEntity.ok(projetos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Projeto> getProjeto(@PathVariable Long id) {
        Projeto projeto = projetoService.getProjetoById(id);
        if (projeto != null) {
            projeto.getCliente().getId();
            return ResponseEntity.ok(projeto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/{id}/horas-disponiveis")
    public ResponseEntity<BigDecimal> getHorasDisponiveis(@PathVariable Long id) {
        Projeto projeto = projetoService.getProjetoById(id);
        if (projeto == null) {
            return ResponseEntity.notFound().build();
        }

        BigDecimal horasDisponiveis = projetoService.calcularHorasDisponiveis(id);
        return ResponseEntity.ok(horasDisponiveis);
    }

    @GetMapping("/{projetoId}/tempo-registrado")
    public ResponseEntity<Map<String, Object>> getTempoRegistrado(@PathVariable Long projetoId) {
        Projeto projeto = projetoService.getProjetoById(projetoId);
        if (projeto == null) {
            return ResponseEntity.notFound().build();
        }

        projetoService.atualizarTempoRegistradoProjeto(projetoId);
        projeto = projetoService.getProjetoById(projetoId);

        BigDecimal tempoRegistrado = projeto.getTempoRegistrado();
        BigDecimal horasEstimadas = projeto.getHorasEstimadas();

        double percentualConcluido = 0;
        if (horasEstimadas != null && horasEstimadas.compareTo(BigDecimal.ZERO) > 0) {
            percentualConcluido = tempoRegistrado.multiply(new BigDecimal(100))
                    .divide(horasEstimadas, 2, BigDecimal.ROUND_HALF_UP)
                    .doubleValue();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("projetoId", projeto.getId());
        response.put("nome", projeto.getNome());
        response.put("tempoRegistrado", tempoRegistrado);
        response.put("horasEstimadas", horasEstimadas);
        response.put("percentualConcluido", percentualConcluido);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{projetoId}/custo-registrado")
    public ResponseEntity<BigDecimal> getCustoRegistrado(@PathVariable Long projetoId) {
        Projeto projeto = projetoService.getProjetoById(projetoId);
        if (projeto == null) {
            return ResponseEntity.notFound().build();
        }

        projetoService.atualizarCustoRegistradoProjeto(projetoId);
        projeto = projetoService.getProjetoById(projetoId);

        return ResponseEntity.ok(projeto.getCustoRegistrado());
    }

    @GetMapping("/getProjetos")
    public List<Projeto> getAllProjetos() {
        return projetoService.findAllProjetosAtivos();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProjeto(@PathVariable Long id) {
        Projeto projeto = projetoService.getProjetoById(id);
        if (projeto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Projeto não encontrado");
        }

        boolean deleted = projetoService.excluirProjeto(id);
        if (deleted) {
            return ResponseEntity.ok("Projeto excluído com sucesso");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Falha ao excluir o projeto");
        }
    }
}