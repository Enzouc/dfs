package com.dfs.despachoservice.controller;

import com.dfs.despachoservice.model.entity.Despacho;
import com.dfs.despachoservice.service.DespachoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/despacho")
@RequiredArgsConstructor
@Slf4j
public class DespachoController {

    private final DespachoService despachoService;

    @PostMapping
    public ResponseEntity<Despacho> crearDespacho(@RequestBody Despacho despacho) {
        return ResponseEntity.ok(despachoService.crearDespacho(despacho));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Despacho> actualizarEstado(@PathVariable Long id, @RequestParam String estado) {
        return despachoService.actualizarEstado(id, estado)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/rutas")
    public ResponseEntity<String> optimizarRuta(@RequestParam Long despachoId) {
        log.info("Optimizando ruta para despacho ID: {}", despachoId);
        return ResponseEntity.ok("Ruta optimizada generada para el despacho " + despachoId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDespacho(@PathVariable Long id) {
        if (despachoService.eliminarDespacho(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
