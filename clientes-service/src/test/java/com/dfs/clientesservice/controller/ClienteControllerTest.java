package com.dfs.clientesservice.controller;

import com.dfs.clientesservice.model.entity.Cliente;
import com.dfs.clientesservice.service.ClienteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dfs.clientesservice.config.TestSecurityConfig;
import org.springframework.context.annotation.Import;

@WebMvcTest(ClienteController.class)
@Import(TestSecurityConfig.class)
@WithMockUser
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClienteService clienteService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Test: obtener perfil cliente existente - 200 OK")
    void testObtenerPerfilClienteExistente() throws Exception {
        Cliente cliente = Cliente.builder()
                .id(1L)
                .nombre("Juan Perez")
                .email("juan@test.com")
                .build();

        when(clienteService.obtenerPerfil(1L)).thenReturn(Optional.of(cliente));

        mockMvc.perform(get("/api/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Juan Perez"));
    }

    @Test
    @DisplayName("Test: obtener perfil cliente no existente - 404 Not Found")
    void testObtenerPerfilClienteNoExistente() throws Exception {
        when(clienteService.obtenerPerfil(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/clientes/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test: actualizar perfil cliente existente - 200 OK")
    void testActualizarPerfilClienteExistente() throws Exception {
        Cliente updated = Cliente.builder()
                .id(1L)
                .nombre("Juan Pablo Perez")
                .email("jp@test.com")
                .build();

        when(clienteService.actualizarPerfil(eq(1L), any(Cliente.class))).thenReturn(Optional.of(updated));

        mockMvc.perform(put("/api/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan Pablo Perez"));
    }

    @Test
    @DisplayName("Test: actualizar perfil cliente no existente - 404 Not Found")
    void testActualizarPerfilClienteNoExistente() throws Exception {
        Cliente updated = Cliente.builder().nombre("Test").build();

        when(clienteService.actualizarPerfil(eq(99L), any(Cliente.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/clientes/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test: eliminar cliente existente - 204 No Content")
    void testEliminarClienteExistente() throws Exception {
        when(clienteService.eliminarCliente(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/clientes/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Test: eliminar cliente no existente - 404 Not Found")
    void testEliminarClienteNoExistente() throws Exception {
        when(clienteService.eliminarCliente(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/clientes/99"))
                .andExpect(status().isNotFound());
    }
}