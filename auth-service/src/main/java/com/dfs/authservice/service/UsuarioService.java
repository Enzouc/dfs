package com.dfs.authservice.service;

import com.dfs.authservice.model.entity.Usuario;
import com.dfs.authservice.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public Usuario crearUsuario(Usuario usuario) {
        log.info("Administrador creando usuario: {}", usuario.getUsername());
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarUsuarios() {
        log.info("Consultando lista de usuarios");
        return usuarioRepository.findAll();
    }

    public boolean eliminarUsuario(Long id) {
        log.info("Eliminando usuario con ID: {}", id);
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<Usuario> modificarPermisos(Long id, Set<String> permisos) {
        log.info("Modificando permisos para usuario ID: {}", id);
        return usuarioRepository.findById(id)
                .map(u -> {
                    u.setPermisos(permisos);
                    return usuarioRepository.save(u);
                });
    }
}
