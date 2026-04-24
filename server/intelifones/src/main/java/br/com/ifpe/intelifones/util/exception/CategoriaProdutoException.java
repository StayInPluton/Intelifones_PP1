package br.com.ifpe.intelifones.util.exception;

public class CategoriaProdutoException extends RuntimeException {

    public static final String MSG_NOME_OBRIGATORIO = "O nome da categoria é obrigatório";
    public static final String MSG_NOME_MINIMO_CARACTERES = "O nome da categoria deve ter pelo menos 3 caracteres";
    public static final String MSG_NOME_JA_EXISTE = "Já existe uma categoria com este nome";
    public static final String MSG_DESCRICAO_MUITO_LONGA = "A descrição não pode ter mais que 500 caracteres";
    public static final String MSG_CATEGORIA_NAO_ENCONTRADA = "Categoria com ID %d não encontrada";
    public static final String MSG_CATEGORIA_NAO_ENCONTRADA_POR_NOME = "Categoria com nome '%s' não encontrada";
    public static final String MSG_CATEGORIA_COM_PRODUTOS = "Não é possível excluir a categoria pois existem %d produto(s) associado(s) a ela";

    public CategoriaProdutoException(String message) {
        super(message);
    }
}