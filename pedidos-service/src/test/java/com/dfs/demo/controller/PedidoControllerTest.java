package com.dfs.demo.controller;

import com.dfs.demo.model.dto.ItemPedidoDTO;
import com.dfs.demo.model.dto.PedidoRequestDTO;
import com.dfs.demo.model.dto.PedidoResponseDTO;
import com.dfs.demo.service.PedidoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
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
}
