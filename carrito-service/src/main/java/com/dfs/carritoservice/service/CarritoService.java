package com.dfs.carritoservice.service;

import com.dfs.carritoservice.model.dto.*;
import com.dfs.carritoservice.model.entity.Carrito;
import com.dfs.carritoservice.model.entity.ItemCarrito;
import com.dfs.carritoservice.repository.CarritoRepository;
import com.dfs.carritoservice.client.ProductoClient;
import com.dfs.carritoservice.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final ProductoClient productoClient;

    private static final Double TASA_IMPUESTO = 0.19; // 19% IVA

    public CarritoResponseDTO obtenerCarritoPorUsuario(Long usuarioId) {
        log.info("Obteniendo carrito para usuario ID: {}", usuarioId);
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> crearCarritoParaUsuario(usuarioId));
        return convertirCarritoAResponse(carrito);
    }

    public CarritoResponseDTO obtenerCarritoPorSession(String sessionId) {
        log.info("Obteniendo carrito para sesión: {}", sessionId);
        Carrito carrito = carritoRepository.findBySessionId(sessionId)
                .orElseGet(() -> crearCarritoTemporal(sessionId));
        return convertirCarritoAResponse(carrito);
    }

    @Transactional
    public CarritoResponseDTO agregarItemAlCarrito(Long usuarioId, String sessionId, ItemCarritoRequestDTO request) {
        log.info("Agregando item al carrito - Producto ID: {}, Cantidad: {}", request.getProductoId(), request.getCantidad());

        // Validar stock
        Boolean hayStock = productoClient.validarStock(request.getProductoId(), request.getCantidad());
        if (!hayStock) {
            throw new RuntimeException("No hay suficiente stock para el producto: " + request.getProductoId());
        }

        // Obtener datos del producto
        ProductoDTO producto = productoClient.obtenerProducto(request.getProductoId());
        if (producto == null || producto.getPrecio() == null) {
            throw new RuntimeException("No se puede obtener el producto o su precio es nulo");
        }

        // Obtener o crear carrito
        Carrito carrito = obtenerOCrearCarrito(usuarioId, sessionId);

        // Verificar si el producto ya está en el carrito
        Optional<ItemCarrito> itemExistente = carrito.getItems().stream()
                .filter(item -> item.getProductoId().equals(request.getProductoId()))
                .findFirst();

        if (itemExistente.isPresent()) {
            // Actualizar cantidad
            ItemCarrito item = itemExistente.get();
            item.setCantidad(item.getCantidad() + request.getCantidad());
            item.calcularSubtotal(); // Ensure subtotal is calculated
        } else {
            // Agregar nuevo item
            ItemCarrito nuevoItem = ItemCarrito.builder()
                    .productoId(producto.getId())
                    .nombreProducto(producto.getNombre())
                    .precioUnitario(producto.getPrecio())
                    .cantidad(request.getCantidad())
                    .build();
            nuevoItem.calcularSubtotal(); // Ensure subtotal is calculated
            carrito.addItem(nuevoItem);
        }

        // Recalcular totales
        calcularTotales(carrito);

        Carrito carritoGuardado = carritoRepository.save(carrito);
        return convertirCarritoAResponse(carritoGuardado);
    }

    @Transactional
    public CarritoResponseDTO actualizarCantidadItem(Long itemId, Integer nuevaCantidad) {
        log.info("Actualizando cantidad del item ID: {} a {}", itemId, nuevaCantidad);

        ItemCarrito item = carritoRepository.findAll().stream()
                .flatMap(c -> c.getItems().stream())
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Item del carrito no encontrado"));

        item.setCantidad(nuevaCantidad);
        item.calcularSubtotal(); // Ensure subtotal is recalculated
        calcularTotales(item.getCarrito());

        Carrito carritoActualizado = carritoRepository.save(item.getCarrito());
        return convertirCarritoAResponse(carritoActualizado);
    }

    @Transactional
    public void eliminarItem(Long itemId) {
        log.info("Eliminando item ID: {}", itemId);

        // Encontrar y eliminar el item
        carritoRepository.findAll().forEach(carrito -> {
            Optional<ItemCarrito> itemAEliminar = carrito.getItems().stream()
                    .filter(item -> item.getId().equals(itemId))
                    .findFirst();

            if (itemAEliminar.isPresent()) {
                carrito.removeItem(itemAEliminar.get());
                calcularTotales(carrito);
                carritoRepository.save(carrito);
            }
        });
    }

    @Transactional
    public void vaciarCarrito(Long usuarioId, String sessionId) {
        log.info("Vaciando carrito - Usuario ID: {}, Sesión: {}", usuarioId, sessionId);
        Carrito carrito = obtenerOCrearCarrito(usuarioId, sessionId);
        carrito.getItems().clear();
        carrito.setSubtotal(0.0);
        carrito.setImpuestos(0.0);
        carrito.setTotal(0.0);
        carritoRepository.save(carrito);
    }

    private Carrito obtenerOCrearCarrito(Long usuarioId, String sessionId) {
        if (usuarioId != null) {
            return carritoRepository.findByUsuarioId(usuarioId)
                    .orElseGet(() -> crearCarritoParaUsuario(usuarioId));
        } else if (sessionId != null) {
            return carritoRepository.findBySessionId(sessionId)
                    .orElseGet(() -> crearCarritoTemporal(sessionId));
        }
        throw new RuntimeException("Se requiere ID de usuario o ID de sesión");
    }

    private Carrito crearCarritoParaUsuario(Long usuarioId) {
        Carrito carrito = Carrito.builder()
                .usuarioId(usuarioId)
                .subtotal(0.0)
                .impuestos(0.0)
                .total(0.0)
                .items(new ArrayList<>())
                .build();
        return carritoRepository.save(carrito);
    }

    private Carrito crearCarritoTemporal(String sessionId) {
        Carrito carrito = Carrito.builder()
                .sessionId(sessionId)
                .subtotal(0.0)
                .impuestos(0.0)
                .total(0.0)
                .items(new ArrayList<>())
                .build();
        return carritoRepository.save(carrito);
    }

    private void calcularTotales(Carrito carrito) {
        if (carrito.getItems() == null || carrito.getItems().isEmpty()) {
            carrito.setSubtotal(0.0);
            carrito.setImpuestos(0.0);
            carrito.setTotal(0.0);
            return;
        }

        Double subtotal = carrito.getItems().stream()
                .map(ItemCarrito::getSubtotal)
                .filter(sub -> sub != null)
                .reduce(0.0, Double::sum);

        Double impuestos = subtotal * TASA_IMPUESTO;
        Double total = subtotal + impuestos;

        carrito.setSubtotal(subtotal);
        carrito.setImpuestos(impuestos);
        carrito.setTotal(total);
    }

    private CarritoResponseDTO convertirCarritoAResponse(Carrito carrito) {
        return CarritoResponseDTO.builder()
                .id(carrito.getId())
                .usuarioId(carrito.getUsuarioId())
                .sessionId(carrito.getSessionId())
                .items(carrito.getItems().stream()
                        .map(this::convertirItemAResponse)
                        .collect(Collectors.toList()))
                .subtotal(carrito.getSubtotal())
                .impuestos(carrito.getImpuestos())
                .total(carrito.getTotal())
                .build();
    }

    private ItemCarritoResponseDTO convertirItemAResponse(ItemCarrito item) {
        return ItemCarritoResponseDTO.builder()
                .id(item.getId())
                .productoId(item.getProductoId())
                .nombreProducto(item.getNombreProducto())
                .precioUnitario(item.getPrecioUnitario())
                .cantidad(item.getCantidad())
                .subtotal(item.getSubtotal())
                .build();
    }
}
