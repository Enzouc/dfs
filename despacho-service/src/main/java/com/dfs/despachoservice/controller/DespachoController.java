package com.dfs.despachoservice.controller;

import com.dfs.despachoservice.model.entity.Despacho;
import com.dfs.despachoservice.repository.DespachoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/despacho")
@RequiredArgsConstructor
@Slf4j
public class DespachoController {

    private final DespachoRepository despachoRepository;

    @PostMapping
    public ResponseEntity<Despacho> crearDespacho(@RequestBody Despacho despacho) {
        log.info("Logística coordinando nuevo envío para pedido ID: {}", despacho.getPedidoId());
        despacho.setEstado("PENDIENTE");
        return ResponseEntity.ok(despachoRepository.save(despacho));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Despacho> actualizarEstado(@PathVariable Long id, @RequestParam String estado) {
        log.info("Actualizando estado de envío ID: {} a {}", id, estado);
        return despachoRepository.findById(id)
                .map(d -> {
                    d.setEstado(estado);
                    return ResponseEntity.ok(despachoRepository.save(d));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/rutas")
    public ResponseEntity<String> optimizarRuta(@RequestParam Long despachoId) {
        log.info("Optimizando ruta para despacho ID: {}", despachoId);
        return ResponseEntity.ok("Ruta optimizada generada para el despacho " + despachoId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDespacho(@PathVariable Long id) {
        log.info("Eliminando despacho con ID: {}", id);
        if (despachoRepository.existsById(id)) {
            despachoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
