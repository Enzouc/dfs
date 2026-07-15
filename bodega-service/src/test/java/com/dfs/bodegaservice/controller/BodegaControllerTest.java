package com.dfs.bodegaservice.controller;

import com.dfs.bodegaservice.model.entity.BodegaCentral;
import com.dfs.bodegaservice.model.entity.Transferencia;
import com.dfs.bodegaservice.service.BodegaService;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dfs.bodegaservice.config.TestSecurityConfig;
import org.springframework.context.annotation.Import;

@WebMvcTest(BodegaController.class)
@Import(TestSecurityConfig.class)
@WithMockUser
class BodegaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BodegaService bodegaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Test: registrar ingreso producto existente - 200 OK")
    void testRegistrarIngresoProductoExistente() throws Exception {
        BodegaCentral ingreso = BodegaCentral.builder()
                .productoId(1L)
                .stockCentral(5)
                .build();
        BodegaCentral updated = BodegaCentral.builder()
                .productoId(1L)
                .stockCentral(15)
                .build();

        when(bodegaService.registrarIngreso(any(BodegaCentral.class))).thenReturn(updated);

        mockMvc.perform(post("/api/bodega/recepcion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ingreso)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockCentral").value(15));
    }

    @Test
    @DisplayName("Test: registrar ingreso producto nuevo - 200 OK")
    void testRegistrarIngresoProductoNuevo() throws Exception {
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

        when(bodegaService.registrarIngreso(any(BodegaCentral.class))).thenReturn(saved);

        mockMvc.perform(post("/api/bodega/recepcion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ingreso)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productoId").value(2));
    }

    @Test
    @DisplayName("Test: consultar alertas reposicion - 200 OK")
    void testConsultarAlertasReposicion() throws Exception {
        BodegaCentral b1 = BodegaCentral.builder().productoId(1L).stockCentral(3).stockMinimo(5).build();

        when(bodegaService.consultarAlertasReposicion()).thenReturn(List.of(b1));

        mockMvc.perform(get("/api/bodega/alertas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("Test: gestionar salida a sucursal - 200 OK")
    void testGestionarSalidaASucursal() throws Exception {
        mockMvc.perform(post("/api/bodega/salida")
                        .param("productoId", "1")
                        .param("cantidad", "10"))
                .andExpect(status().isOk())
                .andExpect(content().string("Salida registrada y coordinada con despacho"));
    }

    @Test
    @DisplayName("Test: solicitar transferencia - 200 OK")
    void testSolicitarTransferencia() throws Exception {
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

        when(bodegaService.solicitarTransferencia(any(Transferencia.class))).thenReturn(saved);

        mockMvc.perform(post("/api/bodega/transferencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("SOLICITADA"));
    }

    @Test
    @DisplayName("Test: procesar transferencia existente - 200 OK")
    void testProcesarTransferenciaExistente() throws Exception {
        Transferencia updated = Transferencia.builder().id(1L).estado("APROBADA").build();

        when(bodegaService.procesarTransferencia(eq(1L), eq("APROBADA"))).thenReturn(Optional.of(updated));

        mockMvc.perform(patch("/api/bodega/transferencias/1/estado")
                        .param("estado", "APROBADA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("APROBADA"));
    }

    @Test
    @DisplayName("Test: procesar transferencia no existente - 404 Not Found")
    void testProcesarTransferenciaNoExistente() throws Exception {
        when(bodegaService.procesarTransferencia(eq(99L), anyString())).thenReturn(Optional.empty());

        mockMvc.perform(patch("/api/bodega/transferencias/99/estado")
                        .param("estado", "APROBADA"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test: eliminar registro bodega existente - 204 No Content")
    void testEliminarRegistroBodegaExistente() throws Exception {
        when(bodegaService.eliminarRegistroBodega(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/bodega/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Test: eliminar registro bodega no existente - 404 Not Found")
    void testEliminarRegistroBodegaNoExistente() throws Exception {
        when(bodegaService.eliminarRegistroBodega(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/bodega/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test: eliminar transferencia existente - 204 No Content")
    void testEliminarTransferenciaExistente() throws Exception {
        when(bodegaService.eliminarTransferencia(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/bodega/transferencias/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Test: eliminar transferencia no existente - 404 Not Found")
    void testEliminarTransferenciaNoExistente() throws Exception {
        when(bodegaService.eliminarTransferencia(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/bodega/transferencias/99"))
                .andExpect(status().isNotFound());
    }
}