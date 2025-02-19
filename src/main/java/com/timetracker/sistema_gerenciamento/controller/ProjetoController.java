package com.timetracker.sistema_gerenciamento.controller;

import com.timetracker.sistema_gerenciamento.model.Projeto;
import com.timetracker.sistema_gerenciamento.model.Cliente;
import com.timetracker.sistema_gerenciamento.model.Usuario;
import com.timetracker.sistema_gerenciamento.repository.ClienteRepository;
import com.timetracker.sistema_gerenciamento.repository.UsuarioRepository;
import com.timetracker.sistema_gerenciamento.service.ProjetoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projetos")
public class ProjetoController {

    @Autowired
    private ProjetoService projetoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

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

        projetoService.save(existingProjeto);
        return ResponseEntity.ok(existingProjeto);
    }

    @PostMapping
    public Projeto createProjeto(@RequestBody Projeto projeto) {
        Long usuarioId = projeto.getUsuarioResponsavel().getId();

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        projeto.setUsuarioResponsavel(usuario);

        if (projeto.getCliente().getId() == null) {
            Cliente cliente = projeto.getCliente();
            clienteRepository.save(cliente);
        }

        return projetoService.save(projeto);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Projeto>> getProjetosDoUsuario(@PathVariable Long usuarioId) {
        List<Projeto> projetos = projetoService.getProjetosPorUsuario(usuarioId);
        return ResponseEntity.ok(projetos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Projeto> getProjeto(@PathVariable Long id) {
        Projeto projeto = projetoService.getProjetoById(id);
        if (projeto != null) {
            return ResponseEntity.ok(projeto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}