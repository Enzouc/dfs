package com.dfs.despachoservice.repository;

import com.dfs.despachoservice.model.entity.Despacho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DespachoRepository extends JpaRepository<Despacho, Long> {
}
