package com.dfs.bodegaservice.controller;

import com.dfs.bodegaservice.controller.BodegaController;
import com.dfs.bodegaservice.model.entity.BodegaCentral;
import com.dfs.bodegaservice.model.entity.Transferencia;
import com.dfs.bodegaservice.repository.BodegaRepository;
import com.dfs.bodegaservice.repository.TransferenciaRepository;
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

import com.dfs.bodegaservice.config.TestSecurityConfig;
import org.springframework.context.annotation.Import;

@WebMvcTest(BodegaController.class)
@Import(TestSecurityConfig.class)
@WithMockUser
public class BodegaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BodegaRepository bodegaRepository;

    @MockBean
    private TransferenciaRepository transferenciaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testRegistrarIngresoProductoExistente() throws Exception {
        BodegaCentral existing = BodegaCentral.builder()
                .productoId(1L)
                .stockCentral(10)
                .stockMinimo(5)
                .build();

        BodegaCentral ingreso = BodegaCentral.builder()
                .productoId(1L)
                .stockCentral(5)
                .build();

        BodegaCentral updated = BodegaCentral.builder()
                .productoId(1L)
                .stockCentral(15)
                .build();

        when(bodegaRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(bodegaRepository.save(any(BodegaCentral.class))).thenReturn(updated);

        mockMvc.perform(post("/api/bodega/recepcion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ingreso)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockCentral").value(15));
    }

    @Test
    public void testRegistrarIngresoProductoNuevo() throws Exception {
        BodegaCentral ingreso = BodegaCentral.builder()
                .productoId(2L)
                .stockCentral(50)
                .stockMinimo(10)
                .build();

        BodegaCentral saved = BodegaCentral.builder()
                .productoId(2L)
                .stockCentral(50)
                .stockMinimo(10)
                .build();

        when(bodegaRepository.findById(2L)).thenReturn(Optional.empty());
        when(bodegaRepository.save(any(BodegaCentral.class))).thenReturn(saved);

        mockMvc.perform(post("/api/bodega/recepcion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ingreso)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productoId").value(2));
    }

    @Test
    public void testConsultarAlertasReposicion() throws Exception {
        BodegaCentral b1 = BodegaCentral.builder().productoId(1L).stockCentral(3).stockMinimo(5).build();
        BodegaCentral b2 = BodegaCentral.builder().productoId(2L).stockCentral(20).stockMinimo(5).build();

        when(bodegaRepository.findAll()).thenReturn(List.of(b1, b2));

        mockMvc.perform(get("/api/bodega/alertas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    public void testGestionarSalidaASucursal() throws Exception {
        mockMvc.perform(post("/api/bodega/salida")
                        .param("productoId", "1")
                        .param("cantidad", "10"))
                .andExpect(status().isOk())
                .andExpect(content().string("Salida registrada y coordinada con despacho"));
    }

    @Test
    public void testSolicitarTransferencia() throws Exception {
        Transferencia request = Transferencia.builder()
                .productoId(1L)
                .cantidad(10)
                .sucursalDestinoId(1L)
                .build();

        Transferencia saved = Transferencia.builder()
                .id(1L)
                .productoId(1L)
                .cantidad(10)
                .sucursalDestinoId(1L)
                .estado("SOLICITADA")
                .build();

        when(transferenciaRepository.save(any(Transferencia.class))).thenReturn(saved);

        mockMvc.perform(post("/api/bodega/transferencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("SOLICITADA"));
    }

    @Test
    public void testProcesarTransferenciaExistente() throws Exception {
        Transferencia existing = Transferencia.builder().id(1L).estado("SOLICITADA").build();
        Transferencia updated = Transferencia.builder().id(1L).estado("APROBADA").build();

        when(transferenciaRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(transferenciaRepository.save(any(Transferencia.class))).thenReturn(updated);

        mockMvc.perform(patch("/api/bodega/transferencias/1/estado")
                        .param("estado", "APROBADA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("APROBADA"));
    }

    @Test
    public void testProcesarTransferenciaNoExistente() throws Exception {
        when(transferenciaRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(patch("/api/bodega/transferencias/99/estado")
                        .param("estado", "APROBADA"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testEliminarRegistroBodegaExistente() throws Exception {
        when(bodegaRepository.existsById(1L)).thenReturn(true);
        doNothing().when(bodegaRepository).deleteById(1L);

        mockMvc.perform(delete("/api/bodega/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testEliminarRegistroBodegaNoExistente() throws Exception {
        when(bodegaRepository.existsById(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/bodega/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testEliminarTransferenciaExistente() throws Exception {
        when(transferenciaRepository.existsById(1L)).thenReturn(true);
        doNothing().when(transferenciaRepository).deleteById(1L);

        mockMvc.perform(delete("/api/bodega/transferencias/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testEliminarTransferenciaNoExistente() throws Exception {
        when(transferenciaRepository.existsById(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/bodega/transferencias/99"))
                .andExpect(status().isNotFound());
    }
}