package com.dfs.ventasservice.controller;

import com.dfs.ventasservice.model.entity.Venta;
import com.dfs.ventasservice.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
@Slf4j
public class VentaController {

    private final VentaRepository ventaRepository;

    @PostMapping
    public ResponseEntity<Venta> registrarVenta(@RequestBody Venta venta) {
        log.info("Asistente de Ventas registrando venta para cliente: {}", venta.getClienteId());
        venta.setFecha(LocalDateTime.now());
        venta.setEstado("COMPLETADA");
        // Aquí se aplicaría la lógica de descuentos y emisión de boleta
        return ResponseEntity.ok(ventaRepository.save(venta));
    }

    @GetMapping("/reporte")
    public List<Venta> generarReporte() {
        log.info("Generando reporte de ventas para Jefe de Sucursal");
        return ventaRepository.findAll();
    }

    @PostMapping("/{id}/devolucion")
    public ResponseEntity<Venta> gestionarDevolucion(@PathVariable Long id) {
        log.info("Gestionando devolución para venta ID: {}", id);
        return ventaRepository.findById(id)
                .map(v -> {
                    v.setEstado("DEVUELTA");
                    return ResponseEntity.ok(ventaRepository.save(v));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarVenta(@PathVariable Long id) {
        log.info("Eliminando venta con ID: {}", id);
        if (ventaRepository.existsById(id)) {
            ventaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
