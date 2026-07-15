package com.dfs.soporteservice.service;

import com.dfs.soporteservice.model.entity.TicketSoporte;
import com.dfs.soporteservice.repository.SoporteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SoporteServiceTest {

    @Mock
    private SoporteRepository soporteRepository;

    @InjectMocks
    private SoporteService soporteService;

    @Test
    @DisplayName("Test: crear ticket de soporte")
    void crearTicket_ticketValido_guardaCorrectamente() {
        TicketSoporte request = TicketSoporte.builder()
                .clienteId(1L)
                .mensaje("Problema con envío")
                .build();
        TicketSoporte saved = TicketSoporte.builder()
                .id(1L)
                .clienteId(1L)
                .mensaje("Problema con envío")
                .tipo("SOPORTE")
                .estado("ABIERTO")
                .fecha(LocalDateTime.now())
                .build();

        when(soporteRepository.save(any(TicketSoporte.class))).thenReturn(saved);

        TicketSoporte result = soporteService.crearTicket(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTipo()).isEqualTo("SOPORTE");
        verify(soporteRepository, times(1)).save(any(TicketSoporte.class));
    }

    @Test
    @DisplayName("Test: dejar reseña")
    void dejarReseña_reseñaValida_guardaCorrectamente() {
        TicketSoporte request = TicketSoporte.builder()
                .clienteId(1L)
                .productoId(1L)
                .mensaje("Excelente producto!")
                .calificacion(5)
                .build();
        TicketSoporte saved = TicketSoporte.builder()
                .id(1L)
                .productoId(1L)
                .tipo("RESEÑA")
                .mensaje("Excelente producto!")
                .calificacion(5)
                .fecha(LocalDateTime.now())
                .build();

        when(soporteRepository.save(any(TicketSoporte.class))).thenReturn(saved);

        TicketSoporte result = soporteService.dejarReseña(request);

        assertThat(result).isNotNull();
        assertThat(result.getTipo()).isEqualTo("RESEÑA");
    }

    @Test
    @DisplayName("Test: listar reseñas por producto")
    void listarReseñas_productoExistente_devuelveLista() {
        TicketSoporte r1 = TicketSoporte.builder().id(1L).productoId(1L).tipo("RESEÑA").build();
        TicketSoporte r2 = TicketSoporte.builder().id(2L).productoId(1L).tipo("RESEÑA").build();

        when(soporteRepository.findByProductoIdAndTipo(1L, "RESEÑA")).thenReturn(List.of(r1, r2));

        List<TicketSoporte> results = soporteService.listarReseñas(1L);

        assertThat(results).hasSize(2);
    }

    @Test
    @DisplayName("Test: eliminar ticket existente")
    void eliminarTicket_ticketExistente_devuelveTrue() {
        when(soporteRepository.existsById(1L)).thenReturn(true);
        doNothing().when(soporteRepository).deleteById(1L);

        boolean result = soporteService.eliminarTicket(1L);

        assertThat(result).isTrue();
        verify(soporteRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Test: eliminar ticket no existente")
    void eliminarTicket_ticketNoExistente_devuelveFalse() {
        when(soporteRepository.existsById(99L)).thenReturn(false);

        boolean result = soporteService.eliminarTicket(99L);

        assertThat(result).isFalse();
        verify(soporteRepository, never()).deleteById(anyLong());
    }
}
