package com.dfs.carritoservice.client;

import com.dfs.carritoservice.model.dto.ProductoDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ProductoClient {

    private final RestClient restClient;
    private static final String BASE_URL = "http://localhost:8083/api/productos";
    private static final String SERVICE_NAME = "inventario-service";

    public ProductoClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    @Retry(name = SERVICE_NAME, fallbackMethod = "fallbackObtenerProducto")
    @CircuitBreaker(name = SERVICE_NAME, fallbackMethod = "fallbackObtenerProducto")
    public ProductoDTO obtenerProducto(Long productoId) {
        ResponseEntity<ProductoDTO> response = restClient.get()
                .uri(BASE_URL + "/{id}", productoId)
                .retrieve()
                .toEntity(ProductoDTO.class);
        return response.getBody();
    }

    @Retry(name = SERVICE_NAME)
    @CircuitBreaker(name = SERVICE_NAME, fallbackMethod = "fallbackValidarStock")
    public Boolean validarStock(Long productoId, Integer cantidad) {
        ResponseEntity<Boolean> response = restClient.get()
                .uri(BASE_URL + "/{id}/stock?cantidad={cantidad}", productoId, cantidad)
                .retrieve()
                .toEntity(Boolean.class);
        return response.getBody();
    }

    private ProductoDTO fallbackObtenerProducto(Long productoId, Exception ex) {
        throw new RuntimeException("No se puede conectar al servicio de inventario para obtener el producto: " + productoId);
    }

    private Boolean fallbackValidarStock(Long productoId, Integer cantidad, Exception ex) {
        return false;
    }
}
