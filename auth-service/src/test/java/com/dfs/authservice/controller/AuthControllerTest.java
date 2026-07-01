package com.dfs.authservice.controller;

import com.dfs.authservice.controller.AuthController;
import com.dfs.authservice.model.entity.Usuario;
import com.dfs.authservice.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dfs.authservice.config.TestSecurityConfig;
import org.springframework.context.annotation.Import;

@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
@WithMockUser
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCrearUsuario() throws Exception {
        Usuario request = Usuario.builder()
                .username("testuser")
                .password("password123")
                .rol("USER")
                .permisos(Set.of("LEER"))
                .build();

        Usuario saved = Usuario.builder()
                .id(1L)
                .username("testuser")
                .password("password123")
                .rol("USER")
                .permisos(Set.of("LEER"))
                .build();

        when(usuarioRepository.save(any(Usuario.class))).thenReturn(saved);

        mockMvc.perform(post("/api/auth/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    public void testListarUsuarios() throws Exception {
        Usuario u1 = Usuario.builder().id(1L).username("user1").build();
        Usuario u2 = Usuario.builder().id(2L).username("user2").build();

        when(usuarioRepository.findAll()).thenReturn(List.of(u1, u2));

        mockMvc.perform(get("/api/auth/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    public void testEliminarUsuarioExistente() throws Exception {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        doNothing().when(usuarioRepository).deleteById(1L);

        mockMvc.perform(delete("/api/auth/usuarios/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testEliminarUsuarioNoExistente() throws Exception {
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/auth/usuarios/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testModificarPermisosUsuarioExistente() throws Exception {
        Usuario existing = Usuario.builder().id(1L).permisos(Set.of("LEER")).build();
        Usuario updated = Usuario.builder().id(1L).permisos(Set.of("LEER", "ESCRIBIR")).build();

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(updated);

        mockMvc.perform(patch("/api/auth/usuarios/1/permisos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Set.of("LEER", "ESCRIBIR"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.permisos.length()").value(2));
    }

    @Test
    public void testModificarPermisosUsuarioNoExistente() throws Exception {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(patch("/api/auth/usuarios/99/permisos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Set.of("LEER"))))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testMonitorearEstado() throws Exception {
        mockMvc.perform(get("/api/auth/monitor"))
                .andExpect(status().isOk())
                .andExpect(content().string("Plataforma operativa - Todos los sistemas funcionando"));
    }
}