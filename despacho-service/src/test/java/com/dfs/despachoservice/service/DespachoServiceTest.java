package com.dfs.despachoservice.service;

import com.dfs.despachoservice.model.entity.Despacho;
import com.dfs.despachoservice.repository.DespachoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DespachoServiceTest {

    @Mock
    private DespachoRepository despachoRepository;

    @InjectMocks
    private DespachoService despachoService;

    @Test
    @DisplayName("Test: crear despacho")
    void crearDespacho_guardaConEstadoPendiente() {
        Despacho request = Despacho.builder()
                .pedidoId(1L)
                .direccionDestino("Calle Falsa 123")
                .build();
        Despacho guardado = Despacho.builder()
                .id(1L)
                .pedidoId(1L)
                .direccionDestino("Calle Falsa 123")
                .estado("PENDIENTE")
                .build();

        when(despachoRepository.save(any(Despacho.class))).thenReturn(guardado);

        Despacho resultado = despachoService.crearDespacho(request);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getEstado()).isEqualTo("PENDIENTE");
    }

    @Test
    @DisplayName("Test: actualizar estado despacho existente")
    void actualizarEstado_despachoExistente_retornaActualizado() {
        Despacho existente = Despacho.builder().id(1L).estado("PENDIENTE").build();
        String nuevoEstado = "EN_RUTA";
        Despacho actualizado = Despacho.builder().id(1L).estado(nuevoEstado).build();

        when(despachoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(despachoRepository.save(any(Despacho.class))).thenReturn(actualizado);

        Optional<Despacho> resultado = despachoService.actualizarEstado(1L, nuevoEstado);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getEstado()).isEqualTo(nuevoEstado);
    }

    @Test
    @DisplayName("Test: actualizar estado despacho no existente")
    void actualizarEstado_despachoNoExistente_retornaVacio() {
        when(despachoRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Despacho> resultado = despachoService.actualizarEstado(99L, "ENTREGADO");

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Test: eliminar despacho existente")
    void eliminarDespacho_despachoExistente_retornaTrue() {
        when(despachoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(despachoRepository).deleteById(1L);

        boolean resultado = despachoService.eliminarDespacho(1L);

        assertThat(resultado).isTrue();
        verify(despachoRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Test: eliminar despacho no existente")
    void eliminarDespacho_despachoNoExistente_retornaFalse() {
        when(despachoRepository.existsById(99L)).thenReturn(false);

        boolean resultado = despachoService.eliminarDespacho(99L);

        assertThat(resultado).isFalse();
        verify(despachoRepository, never()).deleteById(anyLong());
    }
}
