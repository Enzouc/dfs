package com.dfs.demo.service;

import com.dfs.demo.exception.ResourceNotFoundException;
import com.dfs.demo.model.dto.ItemPedidoDTO;
import com.dfs.demo.model.dto.PedidoRequestDTO;
import com.dfs.demo.model.dto.PedidoResponseDTO;
import com.dfs.demo.model.entity.ItemPedido;
import com.dfs.demo.model.entity.Pedido;
import com.dfs.demo.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final com.dfs.demo.client.ProductoClient productoClient;

    @Override
    @Transactional
    public PedidoResponseDTO crearPedido(PedidoRequestDTO pedidoRequest) {
        log.info("Iniciando creación de pedido para el cliente: {}", pedidoRequest.getClienteId());

        // Validar stock de cada producto
        for (ItemPedidoDTO itemDto : pedidoRequest.getItems()) {
            boolean hayStock = productoClient.validarStock(itemDto.getProductoId(), itemDto.getCantidad());
            if (!hayStock) {
                log.error("No hay stock suficiente para el producto ID: {}", itemDto.getProductoId());
                throw new RuntimeException("Stock insuficiente para el producto ID: " + itemDto.getProductoId());
            }
        }

        Pedido pedido = Pedido.builder()
                .clienteId(pedidoRequest.getClienteId())
                .direccionEnvio(pedidoRequest.getDireccionEnvio())
                .codigoCupon(pedidoRequest.getCodigoCupon())
                .build();

        double total = 0;
        for (ItemPedidoDTO itemDto : pedidoRequest.getItems()) {
            double subtotal = itemDto.getCantidad() * itemDto.getPrecioUnitario();
            ItemPedido item = ItemPedido.builder()
                    .productoId(itemDto.getProductoId())
                    .cantidad(itemDto.getCantidad())
                    .precioUnitario(itemDto.getPrecioUnitario())
                    .subtotal(subtotal)
                    .pedido(pedido)
                    .build();
            pedido.addItem(item);
            total += subtotal;
        }

        // Simulación de descuento por cupón
        double descuento = 0;
        if (pedidoRequest.getCodigoCupon() != null && !pedidoRequest.getCodigoCupon().isEmpty()) {
            descuento = total * 0.1; // 10% de descuento fijo para cualquier cupón por ahora
            log.info("Aplicando descuento de {} por cupón {}", descuento, pedidoRequest.getCodigoCupon());
        }
        
        pedido.setDescuento(descuento);
        pedido.setTotal(total - descuento);
        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        log.info("Pedido creado exitosamente con ID: {} y Total final: {}", pedidoGuardado.getId(), pedido.getTotal());
        return mapToResponseDTO(pedidoGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public PedidoResponseDTO obtenerPedidoPorId(Long id) {
        log.debug("Buscando pedido con ID: {}", id);
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Pedido no encontrado con ID: {}", id);
                    return new ResourceNotFoundException("Pedido no encontrado con ID: " + id);
                });
        return mapToResponseDTO(pedido);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> obtenerPedidosPorCliente(Long clienteId) {
        log.debug("Buscando pedidos para el cliente: {}", clienteId);
        return pedidoRepository.findByClienteId(clienteId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PedidoResponseDTO actualizarEstado(Long id, String nuevoEstado) {
        log.info("Actualizando estado del pedido {} a {}", id, nuevoEstado);
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con ID: " + id));
        
        pedido.setEstado(nuevoEstado);
        Pedido actualizado = pedidoRepository.save(pedido);
        log.info("Estado del pedido {} actualizado exitosamente", id);
        return mapToResponseDTO(actualizado);
    }

    private PedidoResponseDTO mapToResponseDTO(Pedido pedido) {
        return PedidoResponseDTO.builder()
                .id(pedido.getId())
                .fecha(pedido.getFecha())
                .clienteId(pedido.getClienteId())
                .estado(pedido.getEstado())
                .total(pedido.getTotal())
                .direccionEnvio(pedido.getDireccionEnvio())
                .codigoCupon(pedido.getCodigoCupon())
                .descuento(pedido.getDescuento())
                .items(pedido.getItems().stream()
                        .map(item -> ItemPedidoDTO.builder()
                                .productoId(item.getProductoId())
                                .cantidad(item.getCantidad())
                                .precioUnitario(item.getPrecioUnitario())
                                .subtotal(item.getSubtotal())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
