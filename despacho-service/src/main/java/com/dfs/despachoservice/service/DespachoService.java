package com.dfs.despachoservice.service;

import com.dfs.despachoservice.model.entity.Despacho;
import com.dfs.despachoservice.repository.DespachoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DespachoService {

    private final DespachoRepository despachoRepository;

    public Despacho crearDespacho(Despacho despacho) {
        log.info("Logística coordinando nuevo envío para pedido ID: {}", despacho.getPedidoId());
        despacho.setEstado("PENDIENTE");
        return despachoRepository.save(despacho);
    }

    public Optional<Despacho> actualizarEstado(Long id, String estado) {
        log.info("Actualizando estado de envío ID: {} a {}", id, estado);
        return despachoRepository.findById(id)
                .map(d -> {
                    d.setEstado(estado);
                    return despachoRepository.save(d);
                });
    }

    public boolean eliminarDespacho(Long id) {
        log.info("Eliminando despacho con ID: {}", id);
        if (despachoRepository.existsById(id)) {
            despachoRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
