package com.dfs.despachoservice.controller;

import com.dfs.despachoservice.model.entity.Despacho;
import com.dfs.despachoservice.service.DespachoService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dfs.despachoservice.config.TestSecurityConfig;
import org.springframework.context.annotation.Import;

@WebMvcTest(DespachoController.class)
@Import(TestSecurityConfig.class)
@WithMockUser
class DespachoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DespachoService despachoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Test: crear despacho - 200 OK")
    void testCrearDespacho() throws Exception {
        Despacho request = Despacho.builder()
                .pedidoId(1L)
                .direccionDestino("Calle Falsa 123")
                .build();

        Despacho saved = Despacho.builder()
                .id(1L)
                .pedidoId(1L)
                .direccionDestino("Calle Falsa 123")
                .estado("PENDIENTE")
                .build();

        when(despachoService.crearDespacho(any(Despacho.class))).thenReturn(saved);

        mockMvc.perform(post("/api/despacho")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    @DisplayName("Test: actualizar estado despacho existente - 200 OK")
    void testActualizarEstadoDespachoExistente() throws Exception {
        Despacho updated = Despacho.builder().id(1L).estado("EN_RUTA").build();

        when(despachoService.actualizarEstado(eq(1L), eq("EN_RUTA"))).thenReturn(Optional.of(updated));

        mockMvc.perform(patch("/api/despacho/1/estado")
                        .param("estado", "EN_RUTA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("EN_RUTA"));
    }

    @Test
    @DisplayName("Test: actualizar estado despacho no existente - 404 Not Found")
    void testActualizarEstadoDespachoNoExistente() throws Exception {
        when(despachoService.actualizarEstado(eq(99L), anyString())).thenReturn(Optional.empty());

        mockMvc.perform(patch("/api/despacho/99/estado")
                        .param("estado", "EN_RUTA"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test: optimizar ruta - 200 OK")
    void testOptimizarRuta() throws Exception {
        mockMvc.perform(get("/api/despacho/rutas")
                        .param("despachoId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Ruta optimizada generada para el despacho 1"));
    }

    @Test
    @DisplayName("Test: eliminar despacho existente - 204 No Content")
    void testEliminarDespachoExistente() throws Exception {
        when(despachoService.eliminarDespacho(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/despacho/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Test: eliminar despacho no existente - 404 Not Found")
    void testEliminarDespachoNoExistente() throws Exception {
        when(despachoService.eliminarDespacho(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/despacho/99"))
                .andExpect(status().isNotFound());
    }
}