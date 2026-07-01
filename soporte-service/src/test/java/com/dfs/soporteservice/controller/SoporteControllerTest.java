package com.dfs.soporteservice.controller;

import com.dfs.soporteservice.controller.SoporteController;
import com.dfs.soporteservice.model.entity.TicketSoporte;
import com.dfs.soporteservice.repository.SoporteRepository;
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

import com.dfs.soporteservice.config.TestSecurityConfig;
import org.springframework.context.annotation.Import;

@WebMvcTest(SoporteController.class)
@Import(TestSecurityConfig.class)
@WithMockUser
public class SoporteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SoporteRepository soporteRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCrearTicket() throws Exception {
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

        when(soporteRepository.save(any(TicketSoporte.class))).thenReturn(saved);

        mockMvc.perform(post("/api/soporte/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.tipo").value("SOPORTE"));
    }

    @Test
    public void testDejarReseña() throws Exception {
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

        when(soporteRepository.save(any(TicketSoporte.class))).thenReturn(saved);

        mockMvc.perform(post("/api/soporte/reseñas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("RESEÑA"));
    }

    @Test
    public void testListarReseñas() throws Exception {
        TicketSoporte r1 = TicketSoporte.builder().id(1L).productoId(1L).tipo("RESEÑA").build();
        TicketSoporte r2 = TicketSoporte.builder().id(2L).productoId(1L).tipo("RESEÑA").build();

        when(soporteRepository.findByProductoIdAndTipo(1L, "RESEÑA")).thenReturn(List.of(r1, r2));

        mockMvc.perform(get("/api/soporte/reseñas/producto/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    public void testEliminarTicketExistente() throws Exception {
        when(soporteRepository.existsById(1L)).thenReturn(true);
        doNothing().when(soporteRepository).deleteById(1L);

        mockMvc.perform(delete("/api/soporte/tickets/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testEliminarTicketNoExistente() throws Exception {
        when(soporteRepository.existsById(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/soporte/tickets/99"))
                .andExpect(status().isNotFound());
    }
}