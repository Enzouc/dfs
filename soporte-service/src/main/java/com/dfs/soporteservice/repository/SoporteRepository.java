package com.dfs.soporteservice.repository;

import com.dfs.soporteservice.model.entity.TicketSoporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SoporteRepository extends JpaRepository<TicketSoporte, Long> {
    List<TicketSoporte> findByProductoIdAndTipo(Long productoId, String tipo);
}
