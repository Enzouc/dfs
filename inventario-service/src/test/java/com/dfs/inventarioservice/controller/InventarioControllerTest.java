package com.dfs.inventarioservice.controller;

import com.dfs.inventarioservice.controller.InventarioController;
import com.dfs.inventarioservice.model.entity.Producto;
import com.dfs.inventarioservice.repository.InventarioRepository;
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

import com.dfs.inventarioservice.config.TestSecurityConfig;
import org.springframework.context.annotation.Import;

@WebMvcTest(InventarioController.class)
@Import(TestSecurityConfig.class)
@WithMockUser
public class InventarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InventarioRepository inventarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testListarProductos() throws Exception {
        Producto p1 = Producto.builder().id(1L).nombre("Libro 1").precio(100.0).build();
        Producto p2 = Producto.builder().id(2L).nombre("Libro 2").precio(200.0).build();

        when(inventarioRepository.findAll()).thenReturn(List.of(p1, p2));

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    public void testCrearProducto() throws Exception {
        Producto request = Producto.builder()
                .nombre("Nuevo Libro")
                .descripcion("Descripción")
                .precio(150.0)
                .stockActual(50)
                .build();

        Producto saved = Producto.builder()
                .id(1L)
                .nombre("Nuevo Libro")
                .descripcion("Descripción")
                .precio(150.0)
                .stockActual(50)
                .build();

        when(inventarioRepository.save(any(Producto.class))).thenReturn(saved);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    public void testObtenerProductoExistente() throws Exception {
        Producto producto = Producto.builder().id(1L).nombre("Libro").build();

        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(producto));

        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    public void testObtenerProductoNoExistente() throws Exception {
        when(inventarioRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/productos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testValidarStock() throws Exception {
        Producto producto = Producto.builder().id(1L).stockActual(10).build();
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(producto));

        mockMvc.perform(get("/api/productos/1/stock")
                        .param("cantidad", "5"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    public void testAjustarStock() throws Exception {
        Producto producto = Producto.builder().id(1L).stockActual(50).build();
        Producto updated = Producto.builder().id(1L).stockActual(100).build();

        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(inventarioRepository.save(any(Producto.class))).thenReturn(updated);

        mockMvc.perform(patch("/api/productos/1/ajuste")
                        .param("nuevoStock", "100"))
                .andExpect(status().isOk());
    }

    @Test
    public void testEliminarProductoExistente() throws Exception {
        when(inventarioRepository.existsById(1L)).thenReturn(true);
        doNothing().when(inventarioRepository).deleteById(1L);

        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testEliminarProductoNoExistente() throws Exception {
        when(inventarioRepository.existsById(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/productos/99"))
                .andExpect(status().isNotFound());
    }
}
