package br.com.ifpe.intelifones.model.pedido;

import java.time.LocalDateTime;

public record PedidoResumoDTO(
        Long id,
        StatusPedido status,
        Double valorTotal,
        Double valorFrete,
        LocalDateTime dataFinalizacao
) {}
