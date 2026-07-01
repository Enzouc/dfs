package com.dfs.demo.controller;

import com.dfs.demo.controller.PedidoController;
import com.dfs.demo.model.dto.ItemPedidoDTO;
import com.dfs.demo.model.dto.PedidoRequestDTO;
import com.dfs.demo.model.dto.PedidoResponseDTO;
import com.dfs.demo.service.PedidoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dfs.demo.config.TestSecurityConfig;
import org.springframework.context.annotation.Import;

@WebMvcTest(PedidoController.class)
@Import(TestSecurityConfig.class)
@WithMockUser
public class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PedidoService pedidoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCrearPedido() throws Exception {
        ItemPedidoDTO item = ItemPedidoDTO.builder()
                .productoId(1L)
                .cantidad(2)
                .precioUnitario(100.0)
                .build();

        PedidoRequestDTO request = PedidoRequestDTO.builder()
                .clienteId(1L)
                .items(Collections.singletonList(item))
                .direccionEnvio("Calle Falsa 123")
                .build();

        PedidoResponseDTO response = PedidoResponseDTO.builder()
                .id(1L)
                .clienteId(1L)
                .total(200.0)
                .estado("PENDIENTE")
                .build();

        when(pedidoService.crearPedido(any(PedidoRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.total").value(200.0))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    public void testObtenerPedidoExistente() throws Exception {
        PedidoResponseDTO response = PedidoResponseDTO.builder()
                .id(1L)
                .clienteId(1L)
                .total(200.0)
                .estado("PENDIENTE")
                .build();

        when(pedidoService.obtenerPedidoPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/pedidos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.total").value(200.0));
    }

    @Test
    public void testObtenerPedidosPorCliente() throws Exception {
        PedidoResponseDTO pedido1 = PedidoResponseDTO.builder().id(1L).clienteId(1L).total(200.0).build();
        PedidoResponseDTO pedido2 = PedidoResponseDTO.builder().id(2L).clienteId(1L).total(300.0).build();

        when(pedidoService.obtenerPedidosPorCliente(1L)).thenReturn(List.of(pedido1, pedido2));

        mockMvc.perform(get("/api/pedidos/cliente/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    public void testActualizarEstado() throws Exception {
        PedidoResponseDTO response = PedidoResponseDTO.builder()
                .id(1L)
                .clienteId(1L)
                .total(200.0)
                .estado("CONFIRMADO")
                .build();

        when(pedidoService.actualizarEstado(anyLong(), anyString())).thenReturn(response);

        mockMvc.perform(patch("/api/pedidos/1/estado")
                        .param("estado", "CONFIRMADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CONFIRMADO"));
    }

    @Test
    public void testEliminarPedidoExistente() throws Exception {
        doNothing().when(pedidoService).eliminarPedido(1L);

        mockMvc.perform(delete("/api/pedidos/1"))
                .andExpect(status().isNoContent());
    }
}
