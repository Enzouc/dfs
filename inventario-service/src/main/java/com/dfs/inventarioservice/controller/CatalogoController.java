package com.dfs.inventarioservice.controller;

import com.dfs.inventarioservice.model.entity.CatalogoLibro;
import com.dfs.inventarioservice.repository.CatalogoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogo")
@RequiredArgsConstructor
@Slf4j
public class CatalogoController {

    private final CatalogoRepository catalogoRepository;

    @GetMapping
    public List<CatalogoLibro> listarTodo() {
        log.info("Cliente navegando por el catálogo completo");
        return catalogoRepository.findAll();
    }

    @GetMapping("/buscar")
    public List<CatalogoLibro> buscar(
            @RequestParam(required = false) String autor,
            @RequestParam(required = false) String editorial,
            @RequestParam(required = false) String genero) {
        log.info("Cliente filtrando catálogo por: autor={}, editorial={}, genero={}", autor, editorial, genero);
        if (autor != null) return catalogoRepository.findByAutorContainingIgnoreCase(autor);
        if (editorial != null) return catalogoRepository.findByEditorialContainingIgnoreCase(editorial);
        if (genero != null) return catalogoRepository.findByGeneroContainingIgnoreCase(genero);
        return catalogoRepository.findAll();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarLibro(@PathVariable Long id) {
        log.info("Eliminando libro del catálogo con ID: {}", id);
        if (catalogoRepository.existsById(id)) {
            catalogoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
