package com.dfs.soporteservice.controller;

import com.dfs.soporteservice.model.entity.TicketSoporte;
import com.dfs.soporteservice.repository.SoporteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/soporte")
@RequiredArgsConstructor
@Slf4j
public class SoporteController {

    private final SoporteRepository soporteRepository;

    @PostMapping("/tickets")
    public ResponseEntity<TicketSoporte> crearTicket(@RequestBody TicketSoporte ticket) {
        log.info("Cliente solicitando soporte técnico");
        ticket.setTipo("SOPORTE");
        ticket.setEstado("ABIERTO");
        ticket.setFecha(LocalDateTime.now());
        return ResponseEntity.ok(soporteRepository.save(ticket));
    }

    @PostMapping("/reseñas")
    public ResponseEntity<TicketSoporte> dejarReseña(@RequestBody TicketSoporte reseña) {
        log.info("Cliente dejando reseña para producto ID: {}", reseña.getProductoId());
        reseña.setTipo("RESEÑA");
        reseña.setFecha(LocalDateTime.now());
        return ResponseEntity.ok(soporteRepository.save(reseña));
    }

    @GetMapping("/reseñas/producto/{productoId}")
    public List<TicketSoporte> listarReseñas(@PathVariable Long productoId) {
        log.info("Consultando reseñas para producto ID: {}", productoId);
        return soporteRepository.findByProductoIdAndTipo(productoId, "RESEÑA");
    }
}
