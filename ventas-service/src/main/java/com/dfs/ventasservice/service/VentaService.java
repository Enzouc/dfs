package com.dfs.ventasservice.service;

import com.dfs.ventasservice.model.entity.Venta;
import com.dfs.ventasservice.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class VentaService {

    private final VentaRepository ventaRepository;

    public Venta registrarVenta(Venta venta) {
        log.info("Asistente de Ventas registrando venta para cliente: {}", venta.getClienteId());
        venta.setFecha(LocalDateTime.now());
        venta.setEstado("COMPLETADA");
        return ventaRepository.save(venta);
    }

    public List<Venta> generarReporte() {
        log.info("Generando reporte de ventas para Jefe de Sucursal");
        return ventaRepository.findAll();
    }

    public Optional<Venta> gestionarDevolucion(Long id) {
        log.info("Gestionando devolución para venta ID: {}", id);
        return ventaRepository.findById(id)
                .map(v -> {
                    v.setEstado("DEVUELTA");
                    return ventaRepository.save(v);
                });
    }

    public boolean eliminarVenta(Long id) {
        log.info("Eliminando venta con ID: {}", id);
        if (ventaRepository.existsById(id)) {
            ventaRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
