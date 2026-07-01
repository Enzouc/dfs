package com.dfs.inventarioservice.controller;

import com.dfs.inventarioservice.model.entity.Producto;
import com.dfs.inventarioservice.repository.InventarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Slf4j
public class InventarioController {

    private final InventarioRepository inventarioRepository;

    @GetMapping
    public List<Producto> listarProductos() {
        log.info("Consultando todos los productos");
        return inventarioRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Producto> crearProducto(@jakarta.validation.Valid @RequestBody Producto producto) {
        log.info("Creando nuevo producto: {}", producto.getNombre());
        return ResponseEntity.ok(inventarioRepository.save(producto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerProducto(@PathVariable Long id) {
        log.info("Consultando producto con ID: {}", id);
        return inventarioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/stock")
    public ResponseEntity<Boolean> validarStock(@PathVariable Long id, @RequestParam Integer cantidad) {
        log.info("Validando stock para producto ID: {} con cantidad: {}", id, cantidad);
        return inventarioRepository.findById(id)
                .map(p -> ResponseEntity.ok(p.getStockActual() >= cantidad))
                .orElse(ResponseEntity.ok(false));
    }

    @PatchMapping("/{id}/ajuste")
    public ResponseEntity<Producto> ajustarStock(@PathVariable Long id, @RequestParam Integer nuevoStock) {
        log.info("Jefe de Sucursal/Bodega ajustando stock para producto ID: {} a {}", id, nuevoStock);
        return inventarioRepository.findById(id)
                .map(p -> {
                    p.setStockActual(nuevoStock);
                    return ResponseEntity.ok(inventarioRepository.save(p));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        log.info("Eliminando producto con ID: {}", id);
        if (inventarioRepository.existsById(id)) {
            inventarioRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
