package com.timetracker.sistema_gerenciamento.controller;

import com.timetracker.sistema_gerenciamento.exception.UsuarioNaoEncontradoException;
import com.timetracker.sistema_gerenciamento.model.Projeto;
import com.timetracker.sistema_gerenciamento.model.Usuario;
import com.timetracker.sistema_gerenciamento.repository.UsuariosProjetosRepository;
import com.timetracker.sistema_gerenciamento.service.AssociacaoUsuarioProjetoService;
import com.timetracker.sistema_gerenciamento.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:4200")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AssociacaoUsuarioProjetoService associacaoUsuarioProjetoService;

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
        List<String> emails = usuarioService.listarUsuariosAtivos().stream()
                .map(Usuario::getEmail)
                .collect(Collectors.toList());
        return ResponseEntity.ok(emails);
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<?> atualizarUsuario(@PathVariable Long id, @RequestBody Map<String, Object> requestBody) {
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
            if (requestBody.get("ativo") != null) {
                usuarioAtualizado.setAtivo(Usuario.AtivoStatus.valueOf((String) requestBody.get("ativo")));
            }

            Usuario usuario = usuarioService.atualizarUsuario(id, usuarioAtualizado, (String) requestBody.get("senhaAtual"));
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erro ao atualizar usuário: " + e.getMessage()));
        }
    }

    @GetMapping("/atual")
    public ResponseEntity<?> getUsuarioAtual() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                String email = authentication.getName();
                Usuario usuarioAtual = usuarioService.buscarPorEmail(email);

                if (usuarioAtual != null) {
                    Map<String, Object> usuarioInfo = new HashMap<>();
                    usuarioInfo.put("id", usuarioAtual.getId());
                    usuarioInfo.put("nome", usuarioAtual.getNome());
                    usuarioInfo.put("email", usuarioAtual.getEmail());
                    usuarioInfo.put("perfil", usuarioAtual.getPerfil());
                    usuarioInfo.put("dataCriacao", usuarioAtual.getDataCriacao());
                    usuarioInfo.put("ultimoLogin", usuarioAtual.getUltimoLogin());
                    usuarioInfo.put("ativo", usuarioAtual.getAtivo());

                    return ResponseEntity.ok(usuarioInfo);
                }
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Usuário não autenticado"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erro ao obter usuário atual: " + e.getMessage()));
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

    @GetMapping("/by-email")
    public ResponseEntity<Long> getUserIdByEmail(@RequestParam String email) {
        try {
            Usuario usuario = usuarioService.buscarPorEmail(email);
            if (usuario != null) {
                return ResponseEntity.ok(usuario.getId());
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluirUsuario(@PathVariable Long id) {
        try {
            usuarioService.excluirUsuario(id);
            return ResponseEntity.ok(Map.of("message", "Usuário excluído com sucesso"));
        } catch (UsuarioNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Usuário não encontrado"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erro ao excluir usuário: " + e.getMessage()));
        }
    }

    @GetMapping("/usuarios/{usuarioId}/projetos")
    public ResponseEntity<List<Projeto>> getProjetosPorUsuario(@PathVariable Long usuarioId) {
        try {
            List<Projeto> projetos = associacaoUsuarioProjetoService.getProjetosPorUsuario(usuarioId);
            return ResponseEntity.ok(projetos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/inativos")
    public ResponseEntity<List<Usuario>> listarUsuariosInativos() {
        List<Usuario> usuariosInativos = usuarioService.listarUsuariosInativos();
        return ResponseEntity.ok(usuariosInativos);
    }

    @PatchMapping("/{id}/reativar")
    public ResponseEntity<?> reativarUsuario(@PathVariable Long id) {
        try {
            usuarioService.reativarUsuario(id);
            return ResponseEntity.ok(Map.of("message", "Usuário reativado com sucesso"));
        } catch (UsuarioNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Usuário não encontrado"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erro ao reativar usuário: " + e.getMessage()));
        }
    }
}