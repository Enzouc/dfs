package com.dfs.sucursalservice.controller;

import com.dfs.sucursalservice.controller.SucursalController;
import com.dfs.sucursalservice.model.entity.Sucursal;
import com.dfs.sucursalservice.repository.SucursalRepository;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dfs.sucursalservice.config.TestSecurityConfig;
import org.springframework.context.annotation.Import;

@WebMvcTest(SucursalController.class)
@Import(TestSecurityConfig.class)
@WithMockUser
public class SucursalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SucursalRepository sucursalRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testListarSucursales() throws Exception {
        Sucursal s1 = Sucursal.builder().id(1L).nombre("Sucursal Centro").build();
        Sucursal s2 = Sucursal.builder().id(2L).nombre("Sucursal Sur").build();

        when(sucursalRepository.findAll()).thenReturn(List.of(s1, s2));

        mockMvc.perform(get("/api/sucursales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    public void testCrearSucursal() throws Exception {
        Sucursal request = Sucursal.builder()
                .nombre("Nueva Sucursal")
                .direccion("Calle 123")
                .build();

        Sucursal saved = Sucursal.builder()
                .id(1L)
                .nombre("Nueva Sucursal")
                .direccion("Calle 123")
                .build();

        when(sucursalRepository.save(any(Sucursal.class))).thenReturn(saved);

        mockMvc.perform(post("/api/sucursales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    public void testActualizarSucursalExistente() throws Exception {
        Sucursal existing = Sucursal.builder().id(1L).nombre("Sucursal Vieja").build();
        Sucursal updated = Sucursal.builder().id(1L).nombre("Sucursal Nueva").build();

        when(sucursalRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(sucursalRepository.save(any(Sucursal.class))).thenReturn(updated);

        mockMvc.perform(put("/api/sucursales/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Sucursal Nueva"));
    }

    @Test
    public void testActualizarSucursalNoExistente() throws Exception {
        Sucursal updated = Sucursal.builder().nombre("Test").build();

        when(sucursalRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/sucursales/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testEliminarSucursalExistente() throws Exception {
        when(sucursalRepository.existsById(1L)).thenReturn(true);
        doNothing().when(sucursalRepository).deleteById(1L);

        mockMvc.perform(delete("/api/sucursales/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testEliminarSucursalNoExistente() throws Exception {
        when(sucursalRepository.existsById(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/sucursales/99"))
                .andExpect(status().isNotFound());
    }
}