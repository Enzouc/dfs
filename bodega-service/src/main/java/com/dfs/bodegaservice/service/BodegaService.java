package com.dfs.bodegaservice.service;

import com.dfs.bodegaservice.model.entity.BodegaCentral;
import com.dfs.bodegaservice.model.entity.Transferencia;
import com.dfs.bodegaservice.repository.BodegaRepository;
import com.dfs.bodegaservice.repository.TransferenciaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BodegaService {

    private final BodegaRepository bodegaRepository;
    private final TransferenciaRepository transferenciaRepository;

    public BodegaCentral registrarIngreso(BodegaCentral ingreso) {
        log.info("Encargado de Bodega registrando ingreso de producto ID: {}", ingreso.getProductoId());
        return bodegaRepository.findById(ingreso.getProductoId())
                .map(b -> {
                    b.setStockCentral(b.getStockCentral() + ingreso.getStockCentral());
                    return bodegaRepository.save(b);
                })
                .orElse(bodegaRepository.save(ingreso));
    }

    public List<BodegaCentral> consultarAlertasReposicion() {
        log.info("Consultando alertas de stock mínimo");
        return bodegaRepository.findAll().stream()
                .filter(b -> b.getStockCentral() <= b.getStockMinimo())
                .toList();
    }

    public Transferencia solicitarTransferencia(Transferencia transferencia) {
        log.info("Solicitando transferencia interna de stock");
        transferencia.setEstado("SOLICITADA");
        return transferenciaRepository.save(transferencia);
    }

    public Optional<Transferencia> procesarTransferencia(Long id, String estado) {
        log.info("Administrador/Bodega procesando transferencia ID: {} a estado: {}", id, estado);
        return transferenciaRepository.findById(id)
                .map(t -> {
                    t.setEstado(estado);
                    return transferenciaRepository.save(t);
                });
    }

    public boolean eliminarRegistroBodega(Long id) {
        log.info("Eliminando registro de bodega con ID: {}", id);
        if (bodegaRepository.existsById(id)) {
            bodegaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean eliminarTransferencia(Long id) {
        log.info("Eliminando transferencia con ID: {}", id);
        if (transferenciaRepository.existsById(id)) {
            transferenciaRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
