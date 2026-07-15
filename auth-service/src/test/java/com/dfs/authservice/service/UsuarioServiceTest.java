package com.dfs.authservice.service;

import com.dfs.authservice.model.entity.Usuario;
import com.dfs.authservice.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    @DisplayName("Test: crear usuario válido")
    void crearUsuario_usuarioValido_retornaUsuarioCreado() {
        Usuario usuario = Usuario.builder()
                .username("testuser")
                .password("pass123")
                .rol("ADMIN")
                .permisos(Set.of("LEER", "ESCRIBIR"))
                .build();
        Usuario usuarioGuardado = Usuario.builder()
                .id(1L)
                .username("testuser")
                .password("pass123")
                .rol("ADMIN")
                .permisos(Set.of("LEER", "ESCRIBIR"))
                .build();

        when(usuarioRepository.save(usuario)).thenReturn(usuarioGuardado);

        Usuario resultado = usuarioService.crearUsuario(usuario);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getUsername()).isEqualTo("testuser");
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    @DisplayName("Test: listar todos los usuarios")
    void listarUsuarios_retornaListaDeUsuarios() {
        Usuario usuario1 = Usuario.builder().id(1L).username("user1").build();
        Usuario usuario2 = Usuario.builder().id(2L).username("user2").build();

        when(usuarioRepository.findAll()).thenReturn(List.of(usuario1, usuario2));

        List<Usuario> resultado = usuarioService.listarUsuarios();

        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting("username").containsExactly("user1", "user2");
    }

    @Test
    @DisplayName("Test: eliminar usuario que existe")
    void eliminarUsuario_usuarioExiste_retornaTrue() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        doNothing().when(usuarioRepository).deleteById(1L);

        boolean resultado = usuarioService.eliminarUsuario(1L);

        assertThat(resultado).isTrue();
        verify(usuarioRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Test: eliminar usuario que no existe")
    void eliminarUsuario_usuarioNoExiste_retornaFalse() {
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        boolean resultado = usuarioService.eliminarUsuario(99L);

        assertThat(resultado).isFalse();
        verify(usuarioRepository, never()).deleteById(99L);
    }

    @Test
    @DisplayName("Test: modificar permisos de usuario existente")
    void modificarPermisos_usuarioExiste_retornaUsuarioActualizado() {
        Usuario usuario = Usuario.builder().id(1L).username("user").permisos(Set.of("LEER")).build();
        Set<String> nuevosPermisos = Set.of("LEER", "ESCRIBIR", "ELIMINAR");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> {
            Usuario u = inv.getArgument(0);
            u.setPermisos(nuevosPermisos);
            return u;
        });

        Optional<Usuario> resultado = usuarioService.modificarPermisos(1L, nuevosPermisos);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getPermisos()).isEqualTo(nuevosPermisos);
    }

    @Test
    @DisplayName("Test: modificar permisos de usuario que no existe")
    void modificarPermisos_usuarioNoExiste_retornaVacio() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Usuario> resultado = usuarioService.modificarPermisos(99L, Set.of("LEER"));

        assertThat(resultado).isEmpty();
    }
}
