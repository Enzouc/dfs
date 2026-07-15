package com.dfs.sucursalservice.service;

import com.dfs.sucursalservice.model.entity.Sucursal;
import com.dfs.sucursalservice.repository.SucursalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SucursalService {

    private final SucursalRepository sucursalRepository;

    public List<Sucursal> listarSucursales() {
        log.info("Consultando todas las sucursales");
        return sucursalRepository.findAll();
    }

    public Sucursal crearSucursal(Sucursal sucursal) {
        log.info("Creando/Actualizando datos de sucursal: {}", sucursal.getNombre());
        return sucursalRepository.save(sucursal);
    }

    public Optional<Sucursal> actualizarSucursal(Long id, Sucursal sucursal) {
        log.info("Actualizando sucursal ID: {}", id);
        return sucursalRepository.findById(id)
                .map(s -> {
                    s.setNombre(sucursal.getNombre());
                    s.setDireccion(sucursal.getDireccion());
                    s.setHorario(sucursal.getHorario());
                    s.setPersonalEncargado(sucursal.getPersonalEncargado());
                    return sucursalRepository.save(s);
                });
    }

    public boolean eliminarSucursal(Long id) {
        log.info("Eliminando sucursal con ID: {}", id);
        if (sucursalRepository.existsById(id)) {
            sucursalRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
