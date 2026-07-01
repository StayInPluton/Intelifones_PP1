package br.com.ifpe.intelifones.model.pedido;

public enum StatusPedido {
    AGUARDANDO_PAGAMENTO, // Estoque reservado, aguardando confirmação (até 30 min)
    PAGO,
    ENVIADO,
    ENTREGUE,
    CANCELADO
}