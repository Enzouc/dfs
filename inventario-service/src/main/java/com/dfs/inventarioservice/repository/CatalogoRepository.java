package com.dfs.inventarioservice.repository;

import com.dfs.inventarioservice.model.entity.CatalogoLibro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CatalogoRepository extends JpaRepository<CatalogoLibro, Long> {
    List<CatalogoLibro> findByAutorContainingIgnoreCase(String autor);
    List<CatalogoLibro> findByEditorialContainingIgnoreCase(String editorial);
    List<CatalogoLibro> findByGeneroContainingIgnoreCase(String genero);
}
