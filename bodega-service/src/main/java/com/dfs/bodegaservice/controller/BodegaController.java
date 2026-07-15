package com.dfs.bodegaservice.controller;

import com.dfs.bodegaservice.model.entity.BodegaCentral;
import com.dfs.bodegaservice.model.entity.Transferencia;
import com.dfs.bodegaservice.service.BodegaService;
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

    private final BodegaService bodegaService;

    @PostMapping("/recepcion")
    public ResponseEntity<BodegaCentral> registrarIngreso(@RequestBody BodegaCentral ingreso) {
        return ResponseEntity.ok(bodegaService.registrarIngreso(ingreso));
    }

    @GetMapping("/alertas")
    public List<BodegaCentral> consultarAlertasReposicion() {
        return bodegaService.consultarAlertasReposicion();
    }

    @PostMapping("/salida")
    public ResponseEntity<String> gestionarSalidaASucursal(@RequestParam Long productoId, @RequestParam Integer cantidad) {
        log.info("Gestionando salida de bodega para producto ID: {} cantidad: {}", productoId, cantidad);
        return ResponseEntity.ok("Salida registrada y coordinada con despacho");
    }

    @PostMapping("/transferencias")
    public ResponseEntity<Transferencia> solicitarTransferencia(@RequestBody Transferencia transferencia) {
        return ResponseEntity.ok(bodegaService.solicitarTransferencia(transferencia));
    }

    @PatchMapping("/transferencias/{id}/estado")
    public ResponseEntity<Transferencia> procesarTransferencia(@PathVariable Long id, @RequestParam String estado) {
        return bodegaService.procesarTransferencia(id, estado)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarRegistroBodega(@PathVariable Long id) {
        if (bodegaService.eliminarRegistroBodega(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/transferencias/{id}")
    public ResponseEntity<Void> eliminarTransferencia(@PathVariable Long id) {
        if (bodegaService.eliminarTransferencia(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
