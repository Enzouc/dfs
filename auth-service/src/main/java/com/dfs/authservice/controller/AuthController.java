package com.dfs.authservice.controller;

import com.dfs.authservice.model.entity.Usuario;
import com.dfs.authservice.service.UsuarioService;
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

    private final UsuarioService usuarioService;

    @PostMapping("/usuarios")
    public ResponseEntity<Usuario> crearUsuario(@jakarta.validation.Valid @RequestBody Usuario usuario) {
        return ResponseEntity.ok(usuarioService.crearUsuario(usuario));
    }

    @GetMapping("/usuarios")
    public List<Usuario> listarUsuarios() {
        return usuarioService.listarUsuarios();
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        if (usuarioService.eliminarUsuario(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/usuarios/{id}/permisos")
    public ResponseEntity<Usuario> modificarPermisos(@PathVariable Long id, @RequestBody Set<String> permisos) {
        return usuarioService.modificarPermisos(id, permisos)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/monitor")
    public ResponseEntity<String> monitorearEstado() {
        log.info("Monitoreando estado de la plataforma");
        return ResponseEntity.ok("Plataforma operativa - Todos los sistemas funcionando");
    }

    
}
