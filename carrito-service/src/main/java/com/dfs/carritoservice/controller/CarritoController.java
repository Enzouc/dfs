package com.dfs.carritoservice.controller;

import com.dfs.carritoservice.model.dto.CarritoResponseDTO;
import com.dfs.carritoservice.model.dto.ItemCarritoRequestDTO;
import com.dfs.carritoservice.service.CarritoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrito")
@RequiredArgsConstructor
@Slf4j
public class CarritoController {

    private final CarritoService carritoService;

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<CarritoResponseDTO> obtenerCarritoPorUsuario(@PathVariable Long usuarioId) {
        log.info("Solicitando carrito para usuario ID: {}", usuarioId);
        return ResponseEntity.ok(carritoService.obtenerCarritoPorUsuario(usuarioId));
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<CarritoResponseDTO> obtenerCarritoPorSesion(@PathVariable String sessionId) {
        log.info("Solicitando carrito para sesión: {}", sessionId);
        return ResponseEntity.ok(carritoService.obtenerCarritoPorSession(sessionId));
    }

    @PostMapping("/items")
    public ResponseEntity<CarritoResponseDTO> agregarItem(@RequestParam(required = false) Long usuarioId,
                                                          @RequestParam(required = false) String sessionId,
                                                          @Valid @RequestBody ItemCarritoRequestDTO request) {
        log.info("Agregando item al carrito");
        return ResponseEntity.ok(carritoService.agregarItemAlCarrito(usuarioId, sessionId, request));
    }

    @PutMapping("/items/{itemId}/cantidad")
    public ResponseEntity<CarritoResponseDTO> actualizarCantidad(@PathVariable Long itemId,
                                                                 @RequestParam Integer cantidad) {
        log.info("Actualizando cantidad del item ID: {}", itemId);
        return ResponseEntity.ok(carritoService.actualizarCantidadItem(itemId, cantidad));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> eliminarItem(@PathVariable Long itemId) {
        log.info("Eliminando item del carrito ID: {}", itemId);
        carritoService.eliminarItem(itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/vaciar")
    public ResponseEntity<Void> vaciarCarrito(@RequestParam(required = false) Long usuarioId,
                                               @RequestParam(required = false) String sessionId) {
        log.info("Vaciando carrito");
        carritoService.vaciarCarrito(usuarioId, sessionId);
        return ResponseEntity.noContent().build();
    }
}
