package com.timetracker.sistema_gerenciamento.controller;

import com.timetracker.sistema_gerenciamento.exception.UsuarioNaoEncontradoException;
import com.timetracker.sistema_gerenciamento.model.Usuario;
import com.timetracker.sistema_gerenciamento.service.AssociacaoUsuarioProjetoService;
import com.timetracker.sistema_gerenciamento.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:4200")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<Usuario>> listarTodosUsuarios() {
        List<Usuario> usuarios = usuarioService.listarTodosUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    @PostMapping("/cadastro")
    public ResponseEntity<?> cadastrarUsuario(@RequestBody Usuario usuario) {
        try {
            if (usuarioService.buscarPorEmail(usuario.getEmail()) != null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Email já cadastrado"));
            }
            Usuario novoUsuario = usuarioService.salvarUsuario(usuario);
            Map<String, Object> response = new HashMap<>();
            response.put("id", novoUsuario.getId());
            response.put("nome", novoUsuario.getNome());
            response.put("email", novoUsuario.getEmail());
            response.put("perfil", novoUsuario.getPerfil());
            response.put("message", "Usuário cadastrado com sucesso");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erro ao cadastrar usuário: " + e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/emails")
    public ResponseEntity<List<String>> getEmails() {
        List<String> emails = usuarioService.getEmails();
        return ResponseEntity.ok(emails);
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<?> atualizarUsuario(
            @PathVariable Long id,
            @RequestBody Map<String, Object> requestBody
    ) {
        try {
            Usuario usuarioAtualizado = new Usuario();
            if (requestBody.get("nome") != null) {
                usuarioAtualizado.setNome((String) requestBody.get("nome"));
            }
            if (requestBody.get("email") != null) {
                usuarioAtualizado.setEmail((String) requestBody.get("email"));
            }
            if (requestBody.get("perfil") != null) {
                usuarioAtualizado.setPerfil((String) requestBody.get("perfil"));
            }
            if (requestBody.get("senha") != null) {
                usuarioAtualizado.setSenha((String) requestBody.get("senha"));
            }

            String senhaAtual = (String) requestBody.get("senhaAtual");

            Usuario usuario = usuarioService.atualizarUsuario(id, usuarioAtualizado, senhaAtual);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erro ao atualizar usuário: " + e.getMessage()));
        }
    }

    @GetMapping("/usuarios/{id}")
    public ResponseEntity<?> getUsuarioById(@PathVariable Long id) {
        try {
            Usuario usuario = usuarioService.buscarPorId(id)
                    .orElseThrow(() -> new UsuarioNaoEncontradoException(id));
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erro ao buscar usuário: " + e.getMessage()));
        }
    }

    @PostMapping("/usuarios/{id}/verify-password")
    public ResponseEntity<?> verificarSenhaAtual(@PathVariable Long id, @RequestBody Map<String, String> credentials) {
        try {
            boolean senhaCorreta = usuarioService.verificarSenhaAtual(id, credentials.get("senha"));
            return ResponseEntity.ok(Map.of("valid", senhaCorreta));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erro ao verificar senha: " + e.getMessage()));
        }
    }

    @RestController
    @RequestMapping("/api/projetos")
    public class ProjetoController {

        @Autowired
        private AssociacaoUsuarioProjetoService associacaoService;

        @PostMapping("/{idProjeto}/adicionar-usuario/{idUsuario}")
        public ResponseEntity<String> associarUsuarioAoProjeto(@PathVariable Long idProjeto, @PathVariable Long idUsuario) {
            try {
                associacaoService.associarUsuarioAoProjeto(idUsuario, idProjeto);
                return ResponseEntity.ok("Usuário adicionado ao projeto com sucesso!");
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
        }
    }
}