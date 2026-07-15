package com.dfs.pedidosservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoResponseDTO {
    private Long id;
    private LocalDateTime fecha;
    private Long clienteId;
    private String estado;
    private Double total;
    private String direccionEnvio;
    private String codigoCupon;
    private Double descuento;
    private List<ItemPedidoDTO> items;
}
