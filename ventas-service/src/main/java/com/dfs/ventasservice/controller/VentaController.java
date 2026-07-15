package com.dfs.ventasservice.controller;

import com.dfs.ventasservice.model.entity.Venta;
import com.dfs.ventasservice.service.VentaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
@Slf4j
public class VentaController {

    private final VentaService ventaService;

    @PostMapping
    public ResponseEntity<Venta> registrarVenta(@RequestBody Venta venta) {
        return ResponseEntity.ok(ventaService.registrarVenta(venta));
    }

    @GetMapping("/reporte")
    public List<Venta> generarReporte() {
        return ventaService.generarReporte();
    }

    @PostMapping("/{id}/devolucion")
    public ResponseEntity<Venta> gestionarDevolucion(@PathVariable Long id) {
        return ventaService.gestionarDevolucion(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarVenta(@PathVariable Long id) {
        if (ventaService.eliminarVenta(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
