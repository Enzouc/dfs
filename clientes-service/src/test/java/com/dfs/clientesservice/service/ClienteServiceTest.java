package com.dfs.clientesservice.service;

import com.dfs.clientesservice.model.entity.Cliente;
import com.dfs.clientesservice.repository.ClienteRepository;
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
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    @DisplayName("Test: obtener perfil cliente existente")
    void obtenerPerfil_clienteExistente_retornaCliente() {
        Cliente cliente = Cliente.builder()
                .id(1L)
                .nombre("Juan Perez")
                .email("juan@test.com")
                .telefono("123456789")
                .direcciones(List.of("Calle 1 #1-1"))
                .build();

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        Optional<Cliente> resultado = clienteService.obtenerPerfil(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo("Juan Perez");
    }

    @Test
    @DisplayName("Test: obtener perfil cliente no existente")
    void obtenerPerfil_clienteNoExistente_retornaVacio() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Cliente> resultado = clienteService.obtenerPerfil(99L);

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Test: actualizar perfil cliente existente")
    void actualizarPerfil_clienteExistente_retornaActualizado() {
        Cliente existente = Cliente.builder()
                .id(1L)
                .nombre("Juan Perez")
                .email("juan@test.com")
                .build();
        Cliente datosActualizados = Cliente.builder()
                .nombre("Juan Pablo Perez")
                .email("jp@test.com")
                .telefono("987654321")
                .direcciones(List.of("Calle 2 #2-2"))
                .build();
        Cliente guardado = Cliente.builder()
                .id(1L)
                .nombre("Juan Pablo Perez")
                .email("jp@test.com")
                .telefono("987654321")
                .direcciones(List.of("Calle 2 #2-2"))
                .build();

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(guardado);

        Optional<Cliente> resultado = clienteService.actualizarPerfil(1L, datosActualizados);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo("Juan Pablo Perez");
    }

    @Test
    @DisplayName("Test: actualizar perfil cliente no existente")
    void actualizarPerfil_clienteNoExistente_retornaVacio() {
        Cliente datosActualizados = Cliente.builder().nombre("Test").build();

        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Cliente> resultado = clienteService.actualizarPerfil(99L, datosActualizados);

        assertThat(resultado).isEmpty();
        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test: eliminar cliente existente")
    void eliminarCliente_clienteExistente_retornaTrue() {
        when(clienteRepository.existsById(1L)).thenReturn(true);
        doNothing().when(clienteRepository).deleteById(1L);

        boolean resultado = clienteService.eliminarCliente(1L);

        assertThat(resultado).isTrue();
        verify(clienteRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Test: eliminar cliente no existente")
    void eliminarCliente_clienteNoExistente_retornaFalse() {
        when(clienteRepository.existsById(99L)).thenReturn(false);

        boolean resultado = clienteService.eliminarCliente(99L);

        assertThat(resultado).isFalse();
        verify(clienteRepository, never()).deleteById(anyLong());
    }
}
