package com.dfs.inventarioservice.service;

import com.dfs.inventarioservice.model.entity.Producto;
import com.dfs.inventarioservice.repository.InventarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductoService {

    private final InventarioRepository inventarioRepository;

    public List<Producto> listarProductos() {
        log.info("Consultando todos los productos");
        return inventarioRepository.findAll();
    }

    public Producto crearProducto(Producto producto) {
        log.info("Creando nuevo producto: {}", producto.getNombre());
        return inventarioRepository.save(producto);
    }

    public Optional<Producto> obtenerProductoPorId(Long id) {
        log.info("Consultando producto con ID: {}", id);
        return inventarioRepository.findById(id);
    }

    public Boolean validarStock(Long id, Integer cantidad) {
        log.info("Validando stock para producto ID: {} con cantidad: {}", id, cantidad);
        return inventarioRepository.findById(id)
                .map(p -> p.getStockActual() >= cantidad)
                .orElse(false);
    }

    public Optional<Producto> ajustarStock(Long id, Integer nuevoStock) {
        log.info("Jefe de Sucursal/Bodega ajustando stock para producto ID: {} a {}", id, nuevoStock);
        return inventarioRepository.findById(id)
                .map(p -> {
                    p.setStockActual(nuevoStock);
                    return inventarioRepository.save(p);
                });
    }

    public boolean eliminarProducto(Long id) {
        log.info("Eliminando producto con ID: {}", id);
        if (inventarioRepository.existsById(id)) {
            inventarioRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
