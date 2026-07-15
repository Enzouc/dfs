package com.dfs.sucursalservice.controller;

import com.dfs.sucursalservice.model.entity.Sucursal;
import com.dfs.sucursalservice.service.SucursalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sucursales")
@RequiredArgsConstructor
@Slf4j
public class SucursalController {

    private final SucursalService sucursalService;

    @GetMapping
    public List<Sucursal> listarSucursales() {
        return sucursalService.listarSucursales();
    }

    @PostMapping
    public ResponseEntity<Sucursal> crearSucursal(@RequestBody Sucursal sucursal) {
        return ResponseEntity.ok(sucursalService.crearSucursal(sucursal));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Sucursal> actualizarSucursal(@PathVariable Long id, @RequestBody Sucursal sucursal) {
        return sucursalService.actualizarSucursal(id, sucursal)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarSucursal(@PathVariable Long id) {
        if (sucursalService.eliminarSucursal(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
