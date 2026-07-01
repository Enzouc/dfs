package com.dfs.ventasservice.controller;

import com.dfs.ventasservice.controller.VentaController;
import com.dfs.ventasservice.model.entity.Venta;
import com.dfs.ventasservice.repository.VentaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dfs.ventasservice.config.TestSecurityConfig;
import org.springframework.context.annotation.Import;

@WebMvcTest(VentaController.class)
@Import(TestSecurityConfig.class)
@WithMockUser
public class VentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VentaRepository ventaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testRegistrarVenta() throws Exception {
        Venta request = Venta.builder()
                .clienteId(1L)
                .total(50000.0)
                .tipoDocumento("BOLETA")
                .build();

        Venta saved = Venta.builder()
                .id(1L)
                .clienteId(1L)
                .total(50000.0)
                .tipoDocumento("BOLETA")
                .fecha(LocalDateTime.now())
                .estado("COMPLETADA")
                .build();

        when(ventaRepository.save(any(Venta.class))).thenReturn(saved);

        mockMvc.perform(post("/api/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("COMPLETADA"));
    }

    @Test
    public void testGenerarReporte() throws Exception {
        Venta v1 = Venta.builder().id(1L).clienteId(1L).total(50000.0).build();
        Venta v2 = Venta.builder().id(2L).clienteId(2L).total(75000.0).build();

        when(ventaRepository.findAll()).thenReturn(List.of(v1, v2));

        mockMvc.perform(get("/api/ventas/reporte"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    public void testGestionarDevolucionVentaExistente() throws Exception {
        Venta existing = Venta.builder().id(1L).estado("COMPLETADA").build();
        Venta updated = Venta.builder().id(1L).estado("DEVUELTA").build();

        when(ventaRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(ventaRepository.save(any(Venta.class))).thenReturn(updated);

        mockMvc.perform(post("/api/ventas/1/devolucion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("DEVUELTA"));
    }

    @Test
    public void testGestionarDevolucionVentaNoExistente() throws Exception {
        when(ventaRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/ventas/99/devolucion"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testEliminarVentaExistente() throws Exception {
        when(ventaRepository.existsById(1L)).thenReturn(true);
        doNothing().when(ventaRepository).deleteById(1L);

        mockMvc.perform(delete("/api/ventas/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testEliminarVentaNoExistente() throws Exception {
        when(ventaRepository.existsById(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/ventas/99"))
                .andExpect(status().isNotFound());
    }
}