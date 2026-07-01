package com.dfs.carritoservice.controller;

import com.dfs.carritoservice.controller.CarritoController;
import com.dfs.carritoservice.model.dto.CarritoResponseDTO;
import com.dfs.carritoservice.model.dto.ItemCarritoRequestDTO;
import com.dfs.carritoservice.service.CarritoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dfs.carritoservice.config.TestSecurityConfig;
import org.springframework.context.annotation.Import;

@WebMvcTest(CarritoController.class)
@Import(TestSecurityConfig.class)
@WithMockUser
public class CarritoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarritoService carritoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testObtenerCarritoPorUsuario() throws Exception {
        CarritoResponseDTO carrito = CarritoResponseDTO.builder()
                .usuarioId(1L)
                .subtotal(0.0)
                .total(0.0)
                .build();

        when(carritoService.obtenerCarritoPorUsuario(1L)).thenReturn(carrito);

        mockMvc.perform(get("/api/carrito/usuario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuarioId").value(1));
    }

    @Test
    public void testAgregarItem() throws Exception {
        ItemCarritoRequestDTO request = ItemCarritoRequestDTO.builder()
                .productoId(1L)
                .cantidad(2)
                .build();

        CarritoResponseDTO carrito = CarritoResponseDTO.builder()
                .usuarioId(1L)
                .subtotal(200.0)
                .total(238.0)
                .build();

        when(carritoService.agregarItemAlCarrito(eq(1L), eq(null), any(ItemCarritoRequestDTO.class))).thenReturn(carrito);

        mockMvc.perform(post("/api/carrito/items")
                        .param("usuarioId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    public void testActualizarCantidad() throws Exception {
        CarritoResponseDTO carrito = CarritoResponseDTO.builder().build();

        when(carritoService.actualizarCantidadItem(1L, 5)).thenReturn(carrito);

        mockMvc.perform(put("/api/carrito/items/1/cantidad")
                        .param("cantidad", "5"))
                .andExpect(status().isOk());
    }

    @Test
    public void testEliminarItem() throws Exception {
        doNothing().when(carritoService).eliminarItem(1L);

        mockMvc.perform(delete("/api/carrito/items/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testVaciarCarrito() throws Exception {
        doNothing().when(carritoService).vaciarCarrito(1L, null);

        mockMvc.perform(delete("/api/carrito/vaciar")
                        .param("usuarioId", "1"))
                .andExpect(status().isNoContent());
    }
}
