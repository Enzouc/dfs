package com.dfs.authservice.controller;

import com.dfs.authservice.model.entity.Usuario;
import com.dfs.authservice.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UsuarioRepository usuarioRepository;

    @PostMapping("/usuarios")
    public ResponseEntity<Usuario> crearUsuario(@jakarta.validation.Valid @RequestBody Usuario usuario) {
        log.info("Administrador creando usuario: {}", usuario.getUsername());
        return ResponseEntity.ok(usuarioRepository.save(usuario));
    }

    @GetMapping("/usuarios")
    public List<Usuario> listarUsuarios() {
        log.info("Consultando lista de usuarios");
        return usuarioRepository.findAll();
    }

    @PatchMapping("/usuarios/{id}/permisos")
    public ResponseEntity<Usuario> modificarPermisos(@PathVariable Long id, @RequestBody Set<String> permisos) {
        log.info("Modificando permisos para usuario ID: {}", id);
        return usuarioRepository.findById(id)
                .map(u -> {
                    u.setPermisos(permisos);
                    return ResponseEntity.ok(usuarioRepository.save(u));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/monitor")
    public ResponseEntity<String> monitorearEstado() {
        log.info("Monitoreando estado de la plataforma");
        return ResponseEntity.ok("Plataforma operativa - Todos los sistemas funcionando");
    }
}
