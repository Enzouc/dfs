package com.dfs.demo.controller;

import com.dfs.demo.model.dto.PedidoRequestDTO;
import com.dfs.demo.model.dto.PedidoResponseDTO;
import com.dfs.demo.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
@Slf4j
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<PedidoResponseDTO> crearPedido(@Valid @RequestBody PedidoRequestDTO pedidoRequest) {
        log.info("Recibida solicitud para crear un nuevo pedido");
        PedidoResponseDTO nuevoPedido = pedidoService.crearPedido(pedidoRequest);
        return new ResponseEntity<>(nuevoPedido, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> obtenerPedido(@PathVariable Long id) {
        log.info("Recibida solicitud para obtener pedido con ID: {}", id);
        PedidoResponseDTO pedido = pedidoService.obtenerPedidoPorId(id);
        return ResponseEntity.ok(pedido);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<PedidoResponseDTO>> obtenerPedidosCliente(@PathVariable Long clienteId) {
        log.info("Recibida solicitud para obtener pedidos del cliente: {}", clienteId);
        List<PedidoResponseDTO> pedidos = pedidoService.obtenerPedidosPorCliente(clienteId);
        return ResponseEntity.ok(pedidos);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<PedidoResponseDTO> actualizarEstado(@PathVariable Long id, @RequestParam String estado) {
        log.info("Recibida solicitud para actualizar estado del pedido {} a {}", id, estado);
        PedidoResponseDTO actualizado = pedidoService.actualizarEstado(id, estado);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPedido(@PathVariable Long id) {
        log.info("Eliminando pedido con ID: {}", id);
        pedidoService.eliminarPedido(id);
        return ResponseEntity.noContent().build();
    }
}
