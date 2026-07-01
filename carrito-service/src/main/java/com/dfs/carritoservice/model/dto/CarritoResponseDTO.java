package com.dfs.carritoservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarritoResponseDTO {

    private Long id;
    private Long usuarioId;
    private String sessionId;
    private List<ItemCarritoResponseDTO> items;
    private Double subtotal;
    private Double impuestos;
    private Double total;
}
