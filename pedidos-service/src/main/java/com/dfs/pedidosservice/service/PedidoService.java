package com.dfs.pedidosservice.service;

import com.dfs.pedidosservice.model.dto.PedidoRequestDTO;
import com.dfs.pedidosservice.model.dto.PedidoResponseDTO;

import java.util.List;

public interface PedidoService {
    PedidoResponseDTO crearPedido(PedidoRequestDTO pedidoRequest);
    PedidoResponseDTO obtenerPedidoPorId(Long id);
    List<PedidoResponseDTO> obtenerPedidosPorCliente(Long clienteId);
    PedidoResponseDTO actualizarEstado(Long id, String nuevoEstado);
    void eliminarPedido(Long id);
}
