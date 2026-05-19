package com.dfs.bodegaservice.repository;

import com.dfs.bodegaservice.model.entity.BodegaCentral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BodegaRepository extends JpaRepository<BodegaCentral, Long> {
    List<BodegaCentral> findByStockCentralLessThan(Integer stockMinimo);
}
