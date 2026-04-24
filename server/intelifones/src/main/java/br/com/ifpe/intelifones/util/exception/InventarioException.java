package br.com.ifpe.intelifones.util.exception;

public class InventarioException extends RuntimeException {

    public static final String MSG_QUANTIDADE_INVALIDA = "Quantidade deve ser maior que zero";
    public static final String MSG_PRECO_INVALIDO = "Preço deve ser maior que zero";
    public static final String MSG_PRODUTO_OBRIGATORIO = "Produto é obrigatório";
    public static final String MSG_VENDEDOR_OBRIGATORIO = "Vendedor é obrigatório";
    public static final String MSG_INVENTARIO_JA_EXISTE = "Já existe um inventário para este produto com este vendedor";
    public static final String MSG_INVENTARIO_NAO_ENCONTRADO = "Inventário não encontrado para este produto e vendedor";
    public static final String MSG_INVENTARIO_NAO_ENCONTRADO_ID = "Inventário com ID %d não encontrado";
    public static final String MSG_ESTOQUE_INSUFICIENTE = "Estoque insuficiente para esta operação";

    public InventarioException(String message) {
        super(message);
    }
}