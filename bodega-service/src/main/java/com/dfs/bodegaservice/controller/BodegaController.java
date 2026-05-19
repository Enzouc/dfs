package com.dfs.bodegaservice.controller;

import com.dfs.bodegaservice.model.entity.BodegaCentral;
import com.dfs.bodegaservice.repository.BodegaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bodega")
@RequiredArgsConstructor
@Slf4j
public class BodegaController {

    private final BodegaRepository bodegaRepository;
    private final com.dfs.bodegaservice.repository.TransferenciaRepository transferenciaRepository;

    @PostMapping("/recepcion")
    public ResponseEntity<BodegaCentral> registrarIngreso(@RequestBody BodegaCentral ingreso) {
        log.info("Encargado de Bodega registrando ingreso de producto ID: {}", ingreso.getProductoId());
        return bodegaRepository.findById(ingreso.getProductoId())
                .map(b -> {
                    b.setStockCentral(b.getStockCentral() + ingreso.getStockCentral());
                    return ResponseEntity.ok(bodegaRepository.save(b));
                })
                .orElse(ResponseEntity.ok(bodegaRepository.save(ingreso)));
    }

    @GetMapping("/alertas")
    public List<BodegaCentral> consultarAlertasReposicion() {
        log.info("Consultando alertas de stock mínimo");
        return bodegaRepository.findAll().stream()
                .filter(b -> b.getStockCentral() <= b.getStockMinimo())
                .toList();
    }

    @PostMapping("/salida")
    public ResponseEntity<String> gestionarSalidaASucursal(@RequestParam Long productoId, @RequestParam Integer cantidad) {
        log.info("Gestionando salida de bodega para producto ID: {} cantidad: {}", productoId, cantidad);
        return ResponseEntity.ok("Salida registrada y coordinada con despacho");
    }

    @PostMapping("/transferencias")
    public ResponseEntity<com.dfs.bodegaservice.model.entity.Transferencia> solicitarTransferencia(@RequestBody com.dfs.bodegaservice.model.entity.Transferencia transferencia) {
        log.info("Solicitando transferencia interna de stock");
        transferencia.setEstado("SOLICITADA");
        return ResponseEntity.ok(transferenciaRepository.save(transferencia));
    }

    @PatchMapping("/transferencias/{id}/estado")
    public ResponseEntity<com.dfs.bodegaservice.model.entity.Transferencia> procesarTransferencia(@PathVariable Long id, @RequestParam String estado) {
        log.info("Administrador/Bodega procesando transferencia ID: {} a estado: {}", id, estado);
        return transferenciaRepository.findById(id)
                .map(t -> {
                    t.setEstado(estado);
                    return ResponseEntity.ok(transferenciaRepository.save(t));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
