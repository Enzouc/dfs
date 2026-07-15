package com.dfs.authservice.controller;

import com.dfs.authservice.model.entity.Usuario;
import com.dfs.authservice.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioService usuarioService;

    @Test
    @DisplayName("Test: crear usuario - 200 OK")
    void crearUsuario_usuarioValido_retorna200() throws Exception {
        Usuario usuarioRequest = Usuario.builder()
                .username("testuser")
                .password("pass123")
                .rol("ADMIN")
                .permisos(Set.of("LEER", "ESCRIBIR"))
                .build();
        Usuario usuarioResponse = Usuario.builder()
                .id(1L)
                .username("testuser")
                .password("pass123")
                .rol("ADMIN")
                .permisos(Set.of("LEER", "ESCRIBIR"))
                .build();

        when(usuarioService.crearUsuario(any(Usuario.class))).thenReturn(usuarioResponse);

        mockMvc.perform(post("/api/auth/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @DisplayName("Test: listar usuarios - 200 OK")
    void listarUsuarios_retornaLista() throws Exception {
        Usuario usuario1 = Usuario.builder().id(1L).username("user1").build();
        Usuario usuario2 = Usuario.builder().id(2L).username("user2").build();

        when(usuarioService.listarUsuarios()).thenReturn(List.of(usuario1, usuario2));

        mockMvc.perform(get("/api/auth/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("Test: eliminar usuario existente - 204 No Content")
    void eliminarUsuario_existente_retorna204() throws Exception {
        when(usuarioService.eliminarUsuario(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/auth/usuarios/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Test: eliminar usuario no existente - 404 Not Found")
    void eliminarUsuario_noExistente_retorna404() throws Exception {
        when(usuarioService.eliminarUsuario(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/auth/usuarios/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test: modificar permisos usuario existente - 200 OK")
    void modificarPermisos_existente_retorna200() throws Exception {
        Set<String> permisos = Set.of("LEER", "ESCRIBIR");
        Usuario usuarioResponse = Usuario.builder()
                .id(1L)
                .username("user")
                .permisos(permisos)
                .build();

        when(usuarioService.modificarPermisos(eq(1L), anySet())).thenReturn(Optional.of(usuarioResponse));

        mockMvc.perform(patch("/api/auth/usuarios/1/permisos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(permisos)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.permisos").isArray());
    }

    @Test
    @DisplayName("Test: monitor - 200 OK")
    void monitorearEstado_retorna200() throws Exception {
        mockMvc.perform(get("/api/auth/monitor"))
                .andExpect(status().isOk())
                .andExpect(content().string("Plataforma operativa - Todos los sistemas funcionando"));
    }
}
