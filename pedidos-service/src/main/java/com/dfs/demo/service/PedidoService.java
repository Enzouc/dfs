package com.dfs.demo.service;

import com.dfs.demo.model.dto.PedidoRequestDTO;
import com.dfs.demo.model.dto.PedidoResponseDTO;

import java.util.List;

public interface PedidoService {
    PedidoResponseDTO crearPedido(PedidoRequestDTO pedidoRequest);
    PedidoResponseDTO obtenerPedidoPorId(Long id);
    List<PedidoResponseDTO> obtenerPedidosPorCliente(Long clienteId);
    PedidoResponseDTO actualizarEstado(Long id, String nuevoEstado);
}
