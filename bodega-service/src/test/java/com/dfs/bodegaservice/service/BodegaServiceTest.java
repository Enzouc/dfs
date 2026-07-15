package com.dfs.bodegaservice.service;

import com.dfs.bodegaservice.model.entity.BodegaCentral;
import com.dfs.bodegaservice.model.entity.Transferencia;
import com.dfs.bodegaservice.repository.BodegaRepository;
import com.dfs.bodegaservice.repository.TransferenciaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BodegaServiceTest {

    @Mock
    private BodegaRepository bodegaRepository;

    @Mock
    private TransferenciaRepository transferenciaRepository;

    @InjectMocks
    private BodegaService bodegaService;

    @Test
    @DisplayName("Test: registrar ingreso producto nuevo")
    void registrarIngreso_productoNuevo_retornaProductoGuardado() {
        BodegaCentral ingreso = BodegaCentral.builder()
                .productoId(1L)
                .stockCentral(50)
                .stockMinimo(10)
                .ubicacion("A1")
                .build();
        BodegaCentral guardado = BodegaCentral.builder()
                .id(1L)
                .productoId(1L)
                .stockCentral(50)
                .stockMinimo(10)
                .ubicacion("A1")
                .build();

        when(bodegaRepository.findById(1L)).thenReturn(Optional.empty());
        when(bodegaRepository.save(ingreso)).thenReturn(guardado);

        BodegaCentral resultado = bodegaService.registrarIngreso(ingreso);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getStockCentral()).isEqualTo(50);
        verify(bodegaRepository, times(1)).findById(1L);
        verify(bodegaRepository, times(1)).save(ingreso);
    }

    @Test
    @DisplayName("Test: registrar ingreso producto existente")
    void registrarIngreso_productoExistente_incrementaStock() {
        BodegaCentral existente = BodegaCentral.builder()
                .id(1L)
                .productoId(1L)
                .stockCentral(30)
                .stockMinimo(10)
                .ubicacion("A1")
                .build();
        BodegaCentral ingreso = BodegaCentral.builder()
                .productoId(1L)
                .stockCentral(20)
                .build();
        BodegaCentral actualizado = BodegaCentral.builder()
                .id(1L)
                .productoId(1L)
                .stockCentral(50)
                .stockMinimo(10)
                .ubicacion("A1")
                .build();

        when(bodegaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(bodegaRepository.save(any(BodegaCentral.class))).thenReturn(actualizado);

        BodegaCentral resultado = bodegaService.registrarIngreso(ingreso);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getStockCentral()).isEqualTo(50);
        verify(bodegaRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Test: consultar alertas de reposición")
    void consultarAlertasReposicion_retornaProductosBajoMinimo() {
        BodegaCentral bajoMinimo = BodegaCentral.builder()
                .id(1L)
                .productoId(1L)
                .stockCentral(5)
                .stockMinimo(10)
                .build();
        BodegaCentral suficiente = BodegaCentral.builder()
                .id(2L)
                .productoId(2L)
                .stockCentral(20)
                .stockMinimo(10)
                .build();

        when(bodegaRepository.findAll()).thenReturn(List.of(bajoMinimo, suficiente));

        List<BodegaCentral> resultado = bodegaService.consultarAlertasReposicion();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Test: solicitar transferencia")
    void solicitarTransferencia_guardaConEstadoSolicitada() {
        Transferencia transferencia = Transferencia.builder()
                .productoId(1L)
                .cantidad(10)
                .sucursalOrigenId(1L)
                .sucursalDestinoId(2L)
                .build();
        Transferencia guardada = Transferencia.builder()
                .id(1L)
                .productoId(1L)
                .cantidad(10)
                .sucursalOrigenId(1L)
                .sucursalDestinoId(2L)
                .estado("SOLICITADA")
                .build();

        when(transferenciaRepository.save(any(Transferencia.class))).thenReturn(guardada);

        Transferencia resultado = bodegaService.solicitarTransferencia(transferencia);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getEstado()).isEqualTo("SOLICITADA");
        verify(transferenciaRepository, times(1)).save(any(Transferencia.class));
    }

    @Test
    @DisplayName("Test: procesar transferencia existente")
    void procesarTransferencia_existente_actualizaEstado() {
        Transferencia transferencia = Transferencia.builder()
                .id(1L)
                .estado("SOLICITADA")
                .build();
        String nuevoEstado = "APROBADA";
        Transferencia actualizada = Transferencia.builder()
                .id(1L)
                .estado(nuevoEstado)
                .build();

        when(transferenciaRepository.findById(1L)).thenReturn(Optional.of(transferencia));
        when(transferenciaRepository.save(any(Transferencia.class))).thenReturn(actualizada);

        Optional<Transferencia> resultado = bodegaService.procesarTransferencia(1L, nuevoEstado);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getEstado()).isEqualTo(nuevoEstado);
    }

    @Test
    @DisplayName("Test: procesar transferencia no existente")
    void procesarTransferencia_noExistente_retornaVacio() {
        when(transferenciaRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Transferencia> resultado = bodegaService.procesarTransferencia(99L, "APROBADA");

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Test: eliminar registro de bodega existente")
    void eliminarRegistroBodega_existente_retornaTrue() {
        when(bodegaRepository.existsById(1L)).thenReturn(true);
        doNothing().when(bodegaRepository).deleteById(1L);

        boolean resultado = bodegaService.eliminarRegistroBodega(1L);

        assertThat(resultado).isTrue();
        verify(bodegaRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Test: eliminar registro de bodega no existente")
    void eliminarRegistroBodega_noExistente_retornaFalse() {
        when(bodegaRepository.existsById(99L)).thenReturn(false);

        boolean resultado = bodegaService.eliminarRegistroBodega(99L);

        assertThat(resultado).isFalse();
        verify(bodegaRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Test: eliminar transferencia existente")
    void eliminarTransferencia_existente_retornaTrue() {
        when(transferenciaRepository.existsById(1L)).thenReturn(true);
        doNothing().when(transferenciaRepository).deleteById(1L);

        boolean resultado = bodegaService.eliminarTransferencia(1L);

        assertThat(resultado).isTrue();
        verify(transferenciaRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Test: eliminar transferencia no existente")
    void eliminarTransferencia_noExistente_retornaFalse() {
        when(transferenciaRepository.existsById(99L)).thenReturn(false);

        boolean resultado = bodegaService.eliminarTransferencia(99L);

        assertThat(resultado).isFalse();
        verify(transferenciaRepository, never()).deleteById(anyLong());
    }
}
