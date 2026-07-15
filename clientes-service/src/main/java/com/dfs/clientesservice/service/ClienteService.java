package com.dfs.clientesservice.service;

import com.dfs.clientesservice.model.entity.Cliente;
import com.dfs.clientesservice.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public Optional<Cliente> obtenerPerfil(Long id) {
        log.info("Cliente consultando su perfil personal ID: {}", id);
        return clienteRepository.findById(id);
    }

    public Optional<Cliente> actualizarPerfil(Long id, Cliente cliente) {
        log.info("Cliente actualizando datos de perfil ID: {}", id);
        return clienteRepository.findById(id)
                .map(c -> {
                    c.setNombre(cliente.getNombre());
                    c.setEmail(cliente.getEmail());
                    c.setTelefono(cliente.getTelefono());
                    c.setDirecciones(cliente.getDirecciones());
                    return clienteRepository.save(c);
                });
    }

    public boolean eliminarCliente(Long id) {
        log.info("Eliminando cliente con ID: {}", id);
        if (clienteRepository.existsById(id)) {
            clienteRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
