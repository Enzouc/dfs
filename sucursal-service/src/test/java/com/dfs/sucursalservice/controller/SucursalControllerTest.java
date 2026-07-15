package com.dfs.sucursalservice.controller;

import com.dfs.sucursalservice.model.entity.Sucursal;
import com.dfs.sucursalservice.service.SucursalService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
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
class SucursalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SucursalService sucursalService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Test: listar sucursales - 200 OK")
    void testListarSucursales() throws Exception {
        Sucursal s1 = Sucursal.builder().id(1L).nombre("Sucursal Centro").build();
        Sucursal s2 = Sucursal.builder().id(2L).nombre("Sucursal Sur").build();

        when(sucursalService.listarSucursales()).thenReturn(List.of(s1, s2));

        mockMvc.perform(get("/api/sucursales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("Test: crear sucursal - 200 OK")
    void testCrearSucursal() throws Exception {
        Sucursal request = Sucursal.builder()
                .nombre("Nueva Sucursal")
                .direccion("Calle 123")
                .build();

        Sucursal saved = Sucursal.builder()
                .id(1L)
                .nombre("Nueva Sucursal")
                .direccion("Calle 123")
                .build();

        when(sucursalService.crearSucursal(any(Sucursal.class))).thenReturn(saved);

        mockMvc.perform(post("/api/sucursales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("Test: actualizar sucursal existente - 200 OK")
    void testActualizarSucursalExistente() throws Exception {
        Sucursal updated = Sucursal.builder().id(1L).nombre("Sucursal Nueva").build();

        when(sucursalService.actualizarSucursal(eq(1L), any(Sucursal.class))).thenReturn(Optional.of(updated));

        mockMvc.perform(put("/api/sucursales/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Sucursal Nueva"));
    }

    @Test
    @DisplayName("Test: actualizar sucursal no existente - 404 Not Found")
    void testActualizarSucursalNoExistente() throws Exception {
        Sucursal updated = Sucursal.builder().nombre("Test").build();

        when(sucursalService.actualizarSucursal(eq(99L), any(Sucursal.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/sucursales/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test: eliminar sucursal existente - 204 No Content")
    void testEliminarSucursalExistente() throws Exception {
        when(sucursalService.eliminarSucursal(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/sucursales/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Test: eliminar sucursal no existente - 404 Not Found")
    void testEliminarSucursalNoExistente() throws Exception {
        when(sucursalService.eliminarSucursal(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/sucursales/99"))
                .andExpect(status().isNotFound());
    }
}
