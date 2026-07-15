package com.dfs.soporteservice.service;

import com.dfs.soporteservice.model.entity.TicketSoporte;
import com.dfs.soporteservice.repository.SoporteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SoporteService {

    private final SoporteRepository soporteRepository;

    public TicketSoporte crearTicket(TicketSoporte ticket) {
        log.info("Cliente solicitando soporte técnico");
        ticket.setTipo("SOPORTE");
        ticket.setEstado("ABIERTO");
        ticket.setFecha(LocalDateTime.now());
        return soporteRepository.save(ticket);
    }

    public TicketSoporte dejarReseña(TicketSoporte reseña) {
        log.info("Cliente dejando reseña para producto ID: {}", reseña.getProductoId());
        reseña.setTipo("RESEÑA");
        reseña.setFecha(LocalDateTime.now());
        return soporteRepository.save(reseña);
    }

    public List<TicketSoporte> listarReseñas(Long productoId) {
        log.info("Consultando reseñas para producto ID: {}", productoId);
        return soporteRepository.findByProductoIdAndTipo(productoId, "RESEÑA");
    }

    public boolean eliminarTicket(Long id) {
        log.info("Eliminando ticket/reseña con ID: {}", id);
        if (soporteRepository.existsById(id)) {
            soporteRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
