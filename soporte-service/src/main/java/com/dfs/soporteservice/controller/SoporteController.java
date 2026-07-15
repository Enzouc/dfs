package com.dfs.soporteservice.controller;

import com.dfs.soporteservice.model.entity.TicketSoporte;
import com.dfs.soporteservice.service.SoporteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/soporte")
@RequiredArgsConstructor
@Slf4j
public class SoporteController {

    private final SoporteService soporteService;

    @PostMapping("/tickets")
    public ResponseEntity<TicketSoporte> crearTicket(@RequestBody TicketSoporte ticket) {
        return ResponseEntity.ok(soporteService.crearTicket(ticket));
    }

    @PostMapping("/reseñas")
    public ResponseEntity<TicketSoporte> dejarReseña(@RequestBody TicketSoporte reseña) {
        return ResponseEntity.ok(soporteService.dejarReseña(reseña));
    }

    @GetMapping("/reseñas/producto/{productoId}")
    public List<TicketSoporte> listarReseñas(@PathVariable Long productoId) {
        return soporteService.listarReseñas(productoId);
    }

    @DeleteMapping("/tickets/{id}")
    public ResponseEntity<Void> eliminarTicket(@PathVariable Long id) {
        if (soporteService.eliminarTicket(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
