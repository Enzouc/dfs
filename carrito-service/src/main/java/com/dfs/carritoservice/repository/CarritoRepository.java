package com.dfs.carritoservice.repository;

import com.dfs.carritoservice.model.entity.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    Optional<Carrito> findByUsuarioId(Long usuarioId);

    Optional<Carrito> findBySessionId(String sessionId);

    boolean existsByUsuarioId(Long usuarioId);

    boolean existsBySessionId(String sessionId);
}
