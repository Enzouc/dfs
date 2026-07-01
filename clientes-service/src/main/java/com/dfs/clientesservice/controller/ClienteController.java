package com.dfs.clientesservice.controller;

import com.dfs.clientesservice.model.entity.Cliente;
import com.dfs.clientesservice.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@Slf4j
public class ClienteController {

    private final ClienteRepository clienteRepository;

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtenerPerfil(@PathVariable Long id) {
        log.info("Cliente consultando su perfil personal ID: {}", id);
        return clienteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizarPerfil(@PathVariable Long id, @RequestBody Cliente cliente) {
        log.info("Cliente actualizando datos de perfil ID: {}", id);
        return clienteRepository.findById(id)
                .map(c -> {
                    c.setNombre(cliente.getNombre());
                    c.setEmail(cliente.getEmail());
                    c.setTelefono(cliente.getTelefono());
                    c.setDirecciones(cliente.getDirecciones());
                    return ResponseEntity.ok(clienteRepository.save(c));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        log.info("Eliminando cliente con ID: {}", id);
        if (clienteRepository.existsById(id)) {
            clienteRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
