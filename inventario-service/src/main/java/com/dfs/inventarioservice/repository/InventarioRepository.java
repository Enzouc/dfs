package com.dfs.inventarioservice.repository;

import com.dfs.inventarioservice.model.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventarioRepository extends JpaRepository<Producto, Long> {
}
