package com.dfs.despachoservice.controller;

import com.dfs.despachoservice.controller.DespachoController;
import com.dfs.despachoservice.model.entity.Despacho;
import com.dfs.despachoservice.repository.DespachoRepository;
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

import com.dfs.despachoservice.config.TestSecurityConfig;
import org.springframework.context.annotation.Import;

@WebMvcTest(DespachoController.class)
@Import(TestSecurityConfig.class)
@WithMockUser
public class DespachoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DespachoRepository despachoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCrearDespacho() throws Exception {
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

        when(despachoRepository.save(any(Despacho.class))).thenReturn(saved);

        mockMvc.perform(post("/api/despacho")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    public void testActualizarEstadoDespachoExistente() throws Exception {
        Despacho existing = Despacho.builder().id(1L).estado("PENDIENTE").build();
        Despacho updated = Despacho.builder().id(1L).estado("EN RUTA").build();

        when(despachoRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(despachoRepository.save(any(Despacho.class))).thenReturn(updated);

        mockMvc.perform(patch("/api/despacho/1/estado")
                        .param("estado", "EN RUTA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("EN RUTA"));
    }

    @Test
    public void testActualizarEstadoDespachoNoExistente() throws Exception {
        when(despachoRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(patch("/api/despacho/99/estado")
                        .param("estado", "EN RUTA"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testOptimizarRuta() throws Exception {
        mockMvc.perform(get("/api/despacho/rutas")
                        .param("despachoId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Ruta optimizada generada para el despacho 1"));
    }

    @Test
    public void testEliminarDespachoExistente() throws Exception {
        when(despachoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(despachoRepository).deleteById(1L);

        mockMvc.perform(delete("/api/despacho/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testEliminarDespachoNoExistente() throws Exception {
        when(despachoRepository.existsById(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/despacho/99"))
                .andExpect(status().isNotFound());
    }
}