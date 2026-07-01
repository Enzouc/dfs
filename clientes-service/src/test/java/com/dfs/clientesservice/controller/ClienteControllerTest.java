package com.dfs.clientesservice.controller;

import com.dfs.clientesservice.controller.ClienteController;
import com.dfs.clientesservice.model.entity.Cliente;
import com.dfs.clientesservice.repository.ClienteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClienteRepository clienteRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testObtenerPerfilClienteExistente() throws Exception {
        Cliente cliente = Cliente.builder()
                .id(1L)
                .nombre("Juan Perez")
                .email("juan@test.com")
                .build();

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        mockMvc.perform(get("/api/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Juan Perez"));
    }

    @Test
    public void testObtenerPerfilClienteNoExistente() throws Exception {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/clientes/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testActualizarPerfilClienteExistente() throws Exception {
        Cliente existing = Cliente.builder()
                .id(1L)
                .nombre("Juan Perez")
                .build();

        Cliente updated = Cliente.builder()
                .id(1L)
                .nombre("Juan Pablo Perez")
                .email("jp@test.com")
                .build();

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(updated);

        mockMvc.perform(put("/api/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan Pablo Perez"));
    }

    @Test
    public void testActualizarPerfilClienteNoExistente() throws Exception {
        Cliente updated = Cliente.builder().nombre("Test").build();

        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/clientes/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testEliminarClienteExistente() throws Exception {
        when(clienteRepository.existsById(1L)).thenReturn(true);
        doNothing().when(clienteRepository).deleteById(1L);

        mockMvc.perform(delete("/api/clientes/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testEliminarClienteNoExistente() throws Exception {
        when(clienteRepository.existsById(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/clientes/99"))
                .andExpect(status().isNotFound());
    }
}