package com.dfs.ventasservice.controller;

import com.dfs.ventasservice.model.entity.Venta;
import com.dfs.ventasservice.service.VentaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
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
class VentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VentaService ventaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Test: registrar venta - 200 OK")
    void testRegistrarVenta() throws Exception {
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

        when(ventaService.registrarVenta(any(Venta.class))).thenReturn(saved);

        mockMvc.perform(post("/api/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("COMPLETADA"));
    }

    @Test
    @DisplayName("Test: generar reporte - 200 OK")
    void testGenerarReporte() throws Exception {
        Venta v1 = Venta.builder().id(1L).clienteId(1L).total(50000.0).build();
        Venta v2 = Venta.builder().id(2L).clienteId(2L).total(75000.0).build();

        when(ventaService.generarReporte()).thenReturn(List.of(v1, v2));

        mockMvc.perform(get("/api/ventas/reporte"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("Test: gestionar devolucion venta existente - 200 OK")
    void testGestionarDevolucionVentaExistente() throws Exception {
        Venta updated = Venta.builder().id(1L).estado("DEVUELTA").build();

        when(ventaService.gestionarDevolucion(1L)).thenReturn(Optional.of(updated));

        mockMvc.perform(post("/api/ventas/1/devolucion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("DEVUELTA"));
    }

    @Test
    @DisplayName("Test: gestionar devolucion venta no existente - 404 Not Found")
    void testGestionarDevolucionVentaNoExistente() throws Exception {
        when(ventaService.gestionarDevolucion(99L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/ventas/99/devolucion"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test: eliminar venta existente - 204 No Content")
    void testEliminarVentaExistente() throws Exception {
        when(ventaService.eliminarVenta(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/ventas/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Test: eliminar venta no existente - 404 Not Found")
    void testEliminarVentaNoExistente() throws Exception {
        when(ventaService.eliminarVenta(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/ventas/99"))
                .andExpect(status().isNotFound());
    }
}
