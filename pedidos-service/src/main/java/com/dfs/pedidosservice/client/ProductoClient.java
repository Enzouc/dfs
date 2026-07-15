package com.dfs.pedidosservice.client;

import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;

@Component
@Slf4j
public class ProductoClient {

    private final RestClient restClient;

    public ProductoClient(RestClient.Builder restClientBuilder) {
        // En un escenario real, la URL vendría de un Config Server o Eureka
        // Inventario service corre en el puerto 8083
        this.restClient = restClientBuilder.baseUrl("http://localhost:8083/api/productos").build();
    }

    @Retry(name = "inventarioRetry", fallbackMethod = "validarStockFallback")
    @CircuitBreaker(name = "inventarioCB", fallbackMethod = "validarStockFallback")
    public boolean validarStock(Long productoId, Integer cantidad) {
        log.info("Consultando stock para producto {} en microservicio de inventario", productoId);
        
        Boolean tieneStock = restClient.get()
                .uri("/{id}/stock?cantidad={cant}", productoId, cantidad)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(Boolean.class);
        
        return Boolean.TRUE.equals(tieneStock);
    }

    public boolean validarStockFallback(Long productoId, Integer cantidad, Throwable t) {
        log.error("Error al comunicar con el microservicio de inventario para producto {}: {}. Aplicando fallback.", 
                productoId, t.getMessage());
        // Fallback: por seguridad ante fallo de comunicación, podríamos denegar o permitir según política de negocio
        return false; 
    }
}
