package com.dfs.bodegaservice.repository;

import com.dfs.bodegaservice.model.entity.Transferencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferenciaRepository extends JpaRepository<Transferencia, Long> {
}
