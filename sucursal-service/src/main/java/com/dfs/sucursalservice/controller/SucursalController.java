package com.dfs.sucursalservice.controller;

import com.dfs.sucursalservice.model.entity.Sucursal;
import com.dfs.sucursalservice.repository.SucursalRepository;
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

    private final SucursalRepository sucursalRepository;

    @GetMapping
    public List<Sucursal> listarSucursales() {
        log.info("Consultando todas las sucursales");
        return sucursalRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Sucursal> crearSucursal(@RequestBody Sucursal sucursal) {
        log.info("Creando/Actualizando datos de sucursal: {}", sucursal.getNombre());
        return ResponseEntity.ok(sucursalRepository.save(sucursal));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Sucursal> actualizarSucursal(@PathVariable Long id, @RequestBody Sucursal sucursal) {
        log.info("Actualizando sucursal ID: {}", id);
        return sucursalRepository.findById(id)
                .map(s -> {
                    s.setNombre(sucursal.getNombre());
                    s.setDireccion(sucursal.getDireccion());
                    s.setHorario(sucursal.getHorario());
                    s.setPersonalEncargado(sucursal.getPersonalEncargado());
                    return ResponseEntity.ok(sucursalRepository.save(s));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
