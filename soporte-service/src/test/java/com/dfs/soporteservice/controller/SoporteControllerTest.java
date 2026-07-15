package com.dfs.soporteservice.controller;

import com.dfs.soporteservice.model.entity.TicketSoporte;
import com.dfs.soporteservice.service.SoporteService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dfs.soporteservice.config.TestSecurityConfig;
import org.springframework.context.annotation.Import;

@WebMvcTest(SoporteController.class)
@Import(TestSecurityConfig.class)
@WithMockUser
class SoporteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SoporteService soporteService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Test: crear ticket - 200 OK")
    void testCrearTicket() throws Exception {
        TicketSoporte request = TicketSoporte.builder()
                .clienteId(1L)
                .mensaje("Problema con envío")
                .build();

        TicketSoporte saved = TicketSoporte.builder()
                .id(1L)
                .clienteId(1L)
                .mensaje("Problema con envío")
                .tipo("SOPORTE")
                .estado("ABIERTO")
                .build();

        when(soporteService.crearTicket(any(TicketSoporte.class))).thenReturn(saved);

        mockMvc.perform(post("/api/soporte/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.tipo").value("SOPORTE"));
    }

    @Test
    @DisplayName("Test: dejar reseña - 200 OK")
    void testDejarReseña() throws Exception {
        TicketSoporte request = TicketSoporte.builder()
                .clienteId(1L)
                .productoId(1L)
                .mensaje("Excelente producto!")
                .calificacion(5)
                .build();

        TicketSoporte saved = TicketSoporte.builder()
                .id(1L)
                .productoId(1L)
                .tipo("RESEÑA")
                .mensaje("Excelente producto!")
                .calificacion(5)
                .build();

        when(soporteService.dejarReseña(any(TicketSoporte.class))).thenReturn(saved);

        mockMvc.perform(post("/api/soporte/reseñas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("RESEÑA"));
    }

    @Test
    @DisplayName("Test: listar reseñas por producto - 200 OK")
    void testListarReseñas() throws Exception {
        TicketSoporte r1 = TicketSoporte.builder().id(1L).productoId(1L).tipo("RESEÑA").build();
        TicketSoporte r2 = TicketSoporte.builder().id(2L).productoId(1L).tipo("RESEÑA").build();

        when(soporteService.listarReseñas(1L)).thenReturn(List.of(r1, r2));

        mockMvc.perform(get("/api/soporte/reseñas/producto/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("Test: eliminar ticket existente - 204 No Content")
    void testEliminarTicketExistente() throws Exception {
        when(soporteService.eliminarTicket(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/soporte/tickets/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Test: eliminar ticket no existente - 404 Not Found")
    void testEliminarTicketNoExistente() throws Exception {
        when(soporteService.eliminarTicket(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/soporte/tickets/99"))
                .andExpect(status().isNotFound());
    }
}
