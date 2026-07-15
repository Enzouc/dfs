package com.dfs.sucursalservice.service;

import com.dfs.sucursalservice.model.entity.Sucursal;
import com.dfs.sucursalservice.repository.SucursalRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SucursalServiceTest {

    @Mock
    private SucursalRepository sucursalRepository;

    @InjectMocks
    private SucursalService sucursalService;

    @Test
    @DisplayName("Test: listar sucursales")
    void listarSucursales_devuelveTodasLasSucursales() {
        Sucursal s1 = Sucursal.builder().id(1L).nombre("Sucursal Centro").build();
        Sucursal s2 = Sucursal.builder().id(2L).nombre("Sucursal Sur").build();

        when(sucursalRepository.findAll()).thenReturn(List.of(s1, s2));

        List<Sucursal> results = sucursalService.listarSucursales();

        assertThat(results).hasSize(2);
    }

    @Test
    @DisplayName("Test: crear sucursal")
    void crearSucursal_sucursalValida_guardaCorrectamente() {
        Sucursal request = Sucursal.builder()
                .nombre("Nueva Sucursal")
                .direccion("Calle 123")
                .build();
        Sucursal saved = Sucursal.builder()
                .id(1L)
                .nombre("Nueva Sucursal")
                .direccion("Calle 123")
                .build();

        when(sucursalRepository.save(any(Sucursal.class))).thenReturn(saved);

        Sucursal result = sucursalService.crearSucursal(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Test: actualizar sucursal existente")
    void actualizarSucursal_existente_devuelveActualizada() {
        Sucursal existing = Sucursal.builder().id(1L).nombre("Sucursal Vieja").build();
        Sucursal data = Sucursal.builder().nombre("Sucursal Nueva").build();
        Sucursal updated = Sucursal.builder().id(1L).nombre("Sucursal Nueva").build();

        when(sucursalRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(sucursalRepository.save(any(Sucursal.class))).thenReturn(updated);

        Optional<Sucursal> result = sucursalService.actualizarSucursal(1L, data);

        assertThat(result).isPresent();
        assertThat(result.get().getNombre()).isEqualTo("Sucursal Nueva");
    }

    @Test
    @DisplayName("Test: actualizar sucursal no existente")
    void actualizarSucursal_noExistente_devuelveVacio() {
        Sucursal data = Sucursal.builder().nombre("Test").build();

        when(sucursalRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Sucursal> result = sucursalService.actualizarSucursal(99L, data);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Test: eliminar sucursal existente")
    void eliminarSucursal_existente_devuelveTrue() {
        when(sucursalRepository.existsById(1L)).thenReturn(true);
        doNothing().when(sucursalRepository).deleteById(1L);

        boolean result = sucursalService.eliminarSucursal(1L);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Test: eliminar sucursal no existente")
    void eliminarSucursal_noExistente_devuelveFalse() {
        when(sucursalRepository.existsById(99L)).thenReturn(false);

        boolean result = sucursalService.eliminarSucursal(99L);

        assertThat(result).isFalse();
    }
}
