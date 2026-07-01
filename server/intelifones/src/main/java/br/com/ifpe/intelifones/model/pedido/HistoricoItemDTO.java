package br.com.ifpe.intelifones.model.pedido;

public record HistoricoItemDTO(
        Long itemId,
        Integer quantidade,
        Double precoUnitario,
        ProdutoResumoDTO produto,
        PedidoResumoDTO pedido
) {}
