package com.dfs.carritoservice.service;

import com.dfs.carritoservice.client.ProductoClient;
import com.dfs.carritoservice.exception.ResourceNotFoundException;
import com.dfs.carritoservice.model.dto.ItemCarritoRequestDTO;
import com.dfs.carritoservice.model.dto.ProductoDTO;
import com.dfs.carritoservice.model.dto.CarritoResponseDTO;
import com.dfs.carritoservice.model.entity.Carrito;
import com.dfs.carritoservice.model.entity.ItemCarrito;
import com.dfs.carritoservice.repository.CarritoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarritoServiceTest {

    @Mock
    private CarritoRepository carritoRepository;

    @Mock
    private ProductoClient productoClient;

    @InjectMocks
    private CarritoService carritoService;

    @Test
    @DisplayName("Test: obtener carrito para usuario nuevo")
    void obtenerCarritoPorUsuario_usuarioNuevo_creaCarritoVacio() {
        // Given
        when(carritoRepository.findByUsuarioId(1L)).thenReturn(Optional.empty());
        when(carritoRepository.save(any(Carrito.class))).thenAnswer(inv -> {
            Carrito c = inv.getArgument(0);
            c.setId(1L);
            return c;
        });

        // When
        CarritoResponseDTO resultado = carritoService.obtenerCarritoPorUsuario(1L);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getUsuarioId()).isEqualTo(1L);
        assertThat(resultado.getItems()).isEmpty();
        assertThat(resultado.getSubtotal()).isEqualTo(0.0);
        verify(carritoRepository, times(1)).findByUsuarioId(1L);
        verify(carritoRepository, times(1)).save(any(Carrito.class));
    }

    @Test
    @DisplayName("Test: agregar item al carrito - producto nuevo")
    void agregarItemAlCarrito_productoNuevo_itemAgregado() {
        // Given
        ItemCarritoRequestDTO request = ItemCarritoRequestDTO.builder()
                .productoId(1L)
                .cantidad(2)
                .build();
        ProductoDTO producto = ProductoDTO.builder()
                .id(1L)
                .nombre("Cien años de soledad")
                .descripcion("Novela")
                .precio(15000.0)
                .stockActual(50)
                .build();
        Carrito carritoVacio = Carrito.builder()
                .id(1L)
                .usuarioId(1L)
                .items(new ArrayList<>())
                .subtotal(0.0)
                .impuestos(0.0)
                .total(0.0)
                .build();

        when(productoClient.validarStock(1L, 2)).thenReturn(true);
        when(productoClient.obtenerProducto(1L)).thenReturn(producto);
        when(carritoRepository.findByUsuarioId(1L)).thenReturn(Optional.of(carritoVacio));
        when(carritoRepository.save(any(Carrito.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        CarritoResponseDTO resultado = carritoService.agregarItemAlCarrito(1L, null, request);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getItems()).hasSize(1);
        assertThat(resultado.getItems().get(0).getProductoId()).isEqualTo(1L);
        assertThat(resultado.getItems().get(0).getCantidad()).isEqualTo(2);
        assertThat(resultado.getSubtotal()).isEqualTo(30000.0);
        assertThat(resultado.getImpuestos()).isEqualTo(5700.0);
        assertThat(resultado.getTotal()).isEqualTo(35700.0);
    }

    @Test
    @DisplayName("Test: agregar item al carrito - producto existente")
    void agregarItemAlCarrito_productoExistente_actualizaCantidad() {
        // Given
        ItemCarritoRequestDTO request = ItemCarritoRequestDTO.builder()
                .productoId(1L)
                .cantidad(2)
                .build();
        ProductoDTO producto = ProductoDTO.builder()
                .id(1L)
                .nombre("Cien años de soledad")
                .descripcion("Novela")
                .precio(15000.0)
                .stockActual(50)
                .build();
        
        ItemCarrito itemExistente = ItemCarrito.builder()
                .id(1L)
                .productoId(1L)
                .nombreProducto("Cien años de soledad")
                .precioUnitario(15000.0)
                .cantidad(2)
                .build();
        itemExistente.calcularSubtotal(); // Llamar manualmente para el test
        
        Carrito carritoExistente = Carrito.builder()
                .id(1L)
                .usuarioId(1L)
                .items(new ArrayList<>(List.of(itemExistente)))
                .subtotal(30000.0)
                .impuestos(5700.0)
                .total(35700.0)
                .build();
        itemExistente.setCarrito(carritoExistente);

        when(productoClient.validarStock(1L, 2)).thenReturn(true);
        when(productoClient.obtenerProducto(1L)).thenReturn(producto);
        when(carritoRepository.findByUsuarioId(1L)).thenReturn(Optional.of(carritoExistente));
        when(carritoRepository.save(any(Carrito.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        CarritoResponseDTO resultado = carritoService.agregarItemAlCarrito(1L, null, request);

        // Then
        assertThat(resultado.getItems()).hasSize(1);
        assertThat(resultado.getItems().get(0).getCantidad()).isEqualTo(4);
    }

    @Test
    @DisplayName("Test: agregar item - sin stock")
    void agregarItemAlCarrito_sinStock_lanzaExcepcion() {
        // Given
        ItemCarritoRequestDTO request = ItemCarritoRequestDTO.builder()
                .productoId(1L)
                .cantidad(2)
                .build();
        when(productoClient.validarStock(1L, 2)).thenReturn(false);

        // When - Then
        assertThatThrownBy(() -> carritoService.agregarItemAlCarrito(1L, null, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No hay suficiente stock");
        verify(productoClient, never()).obtenerProducto(anyLong());
    }

    @Test
    @DisplayName("Test: actualizar cantidad de item")
    void actualizarCantidadItem_itemExiste_actualizaCantidad() {
        // Given
        ItemCarrito item = ItemCarrito.builder()
                .id(1L)
                .productoId(1L)
                .nombreProducto("Cien años de soledad")
                .precioUnitario(15000.0)
                .cantidad(2)
                .build();
        item.calcularSubtotal(); // Llamar manualmente para el test
        
        Carrito carrito = Carrito.builder()
                .id(1L)
                .usuarioId(1L)
                .items(new ArrayList<>(List.of(item)))
                .subtotal(30000.0)
                .impuestos(5700.0)
                .total(35700.0)
                .build();
        item.setCarrito(carrito);

        when(carritoRepository.findAll()).thenReturn(List.of(carrito));
        when(carritoRepository.save(any(Carrito.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        CarritoResponseDTO resultado = carritoService.actualizarCantidadItem(1L, 5);

        // Then
        assertThat(resultado.getItems().get(0).getCantidad()).isEqualTo(5);
        assertThat(resultado.getSubtotal()).isEqualTo(75000.0);
    }

    @Test
    @DisplayName("Test: actualizar cantidad - item no existe")
    void actualizarCantidadItem_itemNoExiste_lanzaExcepcion() {
        // Given
        when(carritoRepository.findAll()).thenReturn(List.of());

        // When - Then
        assertThatThrownBy(() -> carritoService.actualizarCantidadItem(99L, 5))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Item del carrito no encontrado");
    }

    @Test
    @DisplayName("Test: vaciar carrito")
    void vaciarCarrito_carritoConItems_quitaTodo() {
        // Given
        ItemCarrito item = ItemCarrito.builder().id(1L).productoId(1L).nombreProducto("Cien años de soledad").precioUnitario(15000.0).cantidad(2).build();
        Carrito carrito = Carrito.builder().id(1L).usuarioId(1L).items(new ArrayList<>(List.of(item))).subtotal(30000.0).impuestos(5700.0).total(35700.0).build();
        item.setCarrito(carrito);

        when(carritoRepository.findByUsuarioId(1L)).thenReturn(Optional.of(carrito));
        when(carritoRepository.save(any(Carrito.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        carritoService.vaciarCarrito(1L, null);

        // Then
        verify(carritoRepository, times(1)).save(any(Carrito.class));
    }
}
