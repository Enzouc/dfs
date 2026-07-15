package com.dfs.ventasservice.service;

import com.dfs.ventasservice.model.entity.Venta;
import com.dfs.ventasservice.repository.VentaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VentaServiceTest {

    @Mock
    private VentaRepository ventaRepository;

    @InjectMocks
    private VentaService ventaService;

    @Test
    @DisplayName("Test: registrar venta")
    void registrarVenta_ventaValida_guardaCorrectamente() {
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

        when(ventaRepository.save(any(Venta.class))).thenReturn(saved);

        Venta result = ventaService.registrarVenta(request);

        assertThat(result).isNotNull();
        assertThat(result.getEstado()).isEqualTo("COMPLETADA");
    }

    @Test
    @DisplayName("Test: generar reporte")
    void generarReporte_devuelveTodasLasVentas() {
        Venta v1 = Venta.builder().id(1L).clienteId(1L).total(50000.0).build();
        Venta v2 = Venta.builder().id(2L).clienteId(2L).total(75000.0).build();

        when(ventaRepository.findAll()).thenReturn(List.of(v1, v2));

        List<Venta> results = ventaService.generarReporte();

        assertThat(results).hasSize(2);
    }

    @Test
    @DisplayName("Test: gestionar devolucion venta existente")
    void gestionarDevolucion_existente_devuelveActualizada() {
        Venta existing = Venta.builder().id(1L).estado("COMPLETADA").build();
        Venta updated = Venta.builder().id(1L).estado("DEVUELTA").build();

        when(ventaRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(ventaRepository.save(any(Venta.class))).thenReturn(updated);

        Optional<Venta> result = ventaService.gestionarDevolucion(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getEstado()).isEqualTo("DEVUELTA");
    }

    @Test
    @DisplayName("Test: gestionar devolucion venta no existente")
    void gestionarDevolucion_noExistente_devuelveVacio() {
        when(ventaRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Venta> result = ventaService.gestionarDevolucion(99L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Test: eliminar venta existente")
    void eliminarVenta_existente_devuelveTrue() {
        when(ventaRepository.existsById(1L)).thenReturn(true);
        doNothing().when(ventaRepository).deleteById(1L);

        boolean result = ventaService.eliminarVenta(1L);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Test: eliminar venta no existente")
    void eliminarVenta_noExistente_devuelveFalse() {
        when(ventaRepository.existsById(99L)).thenReturn(false);

        boolean result = ventaService.eliminarVenta(99L);

        assertThat(result).isFalse();
    }
}
