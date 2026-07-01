package com.dfs.inventarioservice.service;

import com.dfs.inventarioservice.model.entity.Producto;
import com.dfs.inventarioservice.repository.InventarioRepository;
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
class ProductoServiceTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @InjectMocks
    private ProductoService productoService;

    @Test
    @DisplayName("Test: obtener producto que existe")
    void obtenerProducto_cuandoExiste_devuelveOptional() {
        // Given
        Producto producto = Producto.builder()
                .id(1L)
                .nombre("Cien años de soledad")
                .descripcion("Novela")
                .precio(15000.0)
                .stockActual(50)
                .build();
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(producto));

        // When
        Optional<Producto> resultado = productoService.obtenerProductoPorId(1L);

        // Then
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(1L);
        assertThat(resultado.get().getNombre()).isEqualTo("Cien años de soledad");
        assertThat(resultado.get().getPrecio()).isEqualTo(15000.0);
    }

    @Test
    @DisplayName("Test: obtener producto que NO existe")
    void obtenerProducto_cuandoNoExiste_devuelveOptionalVacio() {
        // Given
        when(inventarioRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        Optional<Producto> resultado = productoService.obtenerProductoPorId(99L);

        // Then
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Test: guardar producto válido")
    void crearProducto_productoValido_devuelveProductoGuardado() {
        // Given
        Producto request = Producto.builder()
                .nombre("Clean Code")
                .descripcion("Libro")
                .precio(29990.0)
                .stockActual(100)
                .build();
        Producto productoGuardado = Producto.builder()
                .id(1L)
                .nombre("Clean Code")
                .descripcion("Libro")
                .precio(29990.0)
                .stockActual(100)
                .build();
        when(inventarioRepository.save(any(Producto.class))).thenReturn(productoGuardado);

        // When
        Producto resultado = productoService.crearProducto(request);

        // Then
        assertThat(resultado.getId()).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Clean Code");
        assertThat(resultado.getPrecio()).isEqualTo(29990.0);
        verify(inventarioRepository, times(1)).save(any(Producto.class));
    }

    @Test
    @DisplayName("Test: listar todos los productos")
    void listarProductos_devuelveListaNoVacia() {
        // Given
        Producto libro1 = Producto.builder()
                .id(1L)
                .nombre("Clean Code")
                .descripcion("Libro")
                .precio(29990.0)
                .stockActual(100)
                .build();
        Producto libro2 = Producto.builder()
                .id(2L)
                .nombre("Refactoring")
                .descripcion("Libro")
                .precio(34990.0)
                .stockActual(50)
                .build();
        when(inventarioRepository.findAll()).thenReturn(List.of(libro1, libro2));

        // When
        List<Producto> resultado = productoService.listarProductos();

        // Then
        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting("nombre").containsExactly("Clean Code", "Refactoring");
    }

    @Test
    @DisplayName("Test: validar stock - stock suficiente")
    void validarStock_stockSuficiente_devuelveTrue() {
        // Given
        Producto producto = Producto.builder()
                .id(1L)
                .nombre("Cien años de soledad")
                .descripcion("Novela")
                .precio(15000.0)
                .stockActual(20)
                .build();
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(producto));

        // When
        Boolean resultado = productoService.validarStock(1L, 10);

        // Then
        assertThat(resultado).isTrue();
    }

    @Test
    @DisplayName("Test: validar stock - stock insuficiente")
    void validarStock_stockInsuficiente_devuelveFalse() {
        // Given
        Producto producto = Producto.builder()
                .id(1L)
                .nombre("Cien años de soledad")
                .descripcion("Novela")
                .precio(15000.0)
                .stockActual(5)
                .build();
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(producto));

        // When
        Boolean resultado = productoService.validarStock(1L, 10);

        // Then
        assertThat(resultado).isFalse();
    }

    @Test
    @DisplayName("Test: ajustar stock producto existente")
    void ajustarStock_productoExiste_devuelveProductoActualizado() {
        // Given
        Producto producto = Producto.builder()
                .id(1L)
                .nombre("Cien años de soledad")
                .descripcion("Novela")
                .precio(15000.0)
                .stockActual(20)
                .build();
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(inventarioRepository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        Optional<Producto> resultado = productoService.ajustarStock(1L, 50);

        // Then
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getStockActual()).isEqualTo(50);
    }

    @Test
    @DisplayName("Test: eliminar producto que existe")
    void eliminarProducto_productoExiste_devuelveTrue() {
        // Given
        when(inventarioRepository.existsById(1L)).thenReturn(true);
        doNothing().when(inventarioRepository).deleteById(1L);

        // When
        boolean resultado = productoService.eliminarProducto(1L);

        // Then
        assertThat(resultado).isTrue();
        verify(inventarioRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Test: eliminar producto que NO existe")
    void eliminarProducto_productoNoExiste_devuelveFalse() {
        // Given
        when(inventarioRepository.existsById(99L)).thenReturn(false);

        // When
        boolean resultado = productoService.eliminarProducto(99L);

        // Then
        assertThat(resultado).isFalse();
        verify(inventarioRepository, never()).deleteById(99L);
    }
}
