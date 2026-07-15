package com.dfs.pedidosservice.service;

import com.dfs.pedidosservice.client.ProductoClient;
import com.dfs.pedidosservice.exception.ResourceNotFoundException;
import com.dfs.pedidosservice.model.dto.ItemPedidoDTO;
import com.dfs.pedidosservice.model.dto.PedidoRequestDTO;
import com.dfs.pedidosservice.model.dto.PedidoResponseDTO;
import com.dfs.pedidosservice.model.entity.Pedido;
import com.dfs.pedidosservice.repository.PedidoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceImplTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ProductoClient productoClient;

    @InjectMocks
    private PedidoServiceImpl pedidoService;

    @Test
    @DisplayName("Test: crear pedido exitoso sin cupón")
    void crearPedido_datosValidos_devuelvePedidoCreado() {
        // Given
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

        Pedido pedidoGuardado = Pedido.builder()
                .id(1L)
                .clienteId(1L)
                .direccionEnvio("Calle Falsa 123")
                .total(200.0)
                .estado("PENDIENTE")
                .fecha(LocalDateTime.now())
                .build();

        when(productoClient.validarStock(1L, 2)).thenReturn(true);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoGuardado);

        // When
        PedidoResponseDTO result = pedidoService.crearPedido(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTotal()).isEqualTo(200.0);
        assertThat(result.getEstado()).isEqualTo("PENDIENTE");
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Test: crear pedido exitoso con cupón de descuento")
    void crearPedido_conCupon_aplicaDescuento() {
        // Given
        ItemPedidoDTO item = ItemPedidoDTO.builder()
                .productoId(1L)
                .cantidad(2)
                .precioUnitario(100.0)
                .build();
        PedidoRequestDTO request = PedidoRequestDTO.builder()
                .clienteId(1L)
                .items(Collections.singletonList(item))
                .direccionEnvio("Calle Falsa 123")
                .codigoCupon("DESCUENTO10")
                .build();

        Pedido pedidoGuardado = Pedido.builder()
                .id(1L)
                .clienteId(1L)
                .direccionEnvio("Calle Falsa 123")
                .total(180.0)
                .descuento(20.0)
                .codigoCupon("DESCUENTO10")
                .estado("PENDIENTE")
                .fecha(LocalDateTime.now())
                .build();

        when(productoClient.validarStock(1L, 2)).thenReturn(true);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoGuardado);

        // When
        PedidoResponseDTO result = pedidoService.crearPedido(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotal()).isEqualTo(180.0);
        assertThat(result.getDescuento()).isEqualTo(20.0);
    }

    @Test
    @DisplayName("Test: crear pedido sin stock lanza excepción")
    void crearPedido_sinStock_lanzaExcepcion() {
        // Given
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

        when(productoClient.validarStock(1L, 2)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> pedidoService.crearPedido(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Stock insuficiente");
    }

    @Test
    @DisplayName("Test: obtener pedido por ID existente")
    void obtenerPedidoPorId_existe_devuelvePedido() {
        // Given
        Pedido pedido = Pedido.builder()
                .id(1L)
                .clienteId(1L)
                .total(200.0)
                .estado("PENDIENTE")
                .fecha(LocalDateTime.now())
                .build();

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        // When
        PedidoResponseDTO result = pedidoService.obtenerPedidoPorId(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Test: obtener pedido por ID no existente lanza excepción")
    void obtenerPedidoPorId_noExiste_lanzaExcepcion() {
        // Given
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> pedidoService.obtenerPedidoPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Test: obtener pedidos por cliente")
    void obtenerPedidosPorCliente_devuelveLista() {
        // Given
        Pedido pedido1 = Pedido.builder().id(1L).clienteId(1L).total(200.0).build();
        Pedido pedido2 = Pedido.builder().id(2L).clienteId(1L).total(300.0).build();

        when(pedidoRepository.findByClienteId(1L)).thenReturn(List.of(pedido1, pedido2));

        // When
        List<PedidoResponseDTO> results = pedidoService.obtenerPedidosPorCliente(1L);

        // Then
        assertThat(results).hasSize(2);
    }

    @Test
    @DisplayName("Test: actualizar estado de pedido")
    void actualizarEstado_pedidoExiste_devuelveActualizado() {
        // Given
        Pedido pedido = Pedido.builder()
                .id(1L)
                .clienteId(1L)
                .estado("PENDIENTE")
                .fecha(LocalDateTime.now())
                .build();

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        PedidoResponseDTO result = pedidoService.actualizarEstado(1L, "ENVIADO");

        // Then
        assertThat(result.getEstado()).isEqualTo("ENVIADO");
    }

    @Test
    @DisplayName("Test: eliminar pedido existente")
    void eliminarPedido_existe_eliminaCorrectamente() {
        // Given
        when(pedidoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(pedidoRepository).deleteById(1L);

        // When
        pedidoService.eliminarPedido(1L);

        // Then
        verify(pedidoRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Test: eliminar pedido no existente lanza excepción")
    void eliminarPedido_noExiste_lanzaExcepcion() {
        // Given
        when(pedidoRepository.existsById(99L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> pedidoService.eliminarPedido(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
