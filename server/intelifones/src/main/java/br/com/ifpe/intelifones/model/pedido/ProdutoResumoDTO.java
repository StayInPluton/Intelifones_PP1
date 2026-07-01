package br.com.ifpe.intelifones.model.pedido;

import br.com.ifpe.intelifones.model.produto.Produto;

public record ProdutoResumoDTO(
        Long id,
        String nome,
        String imagem,
        Boolean usado,
        Integer quantidade
) {
    public ProdutoResumoDTO(Produto produto) {
        this(
                produto.getId(),
                produto.getNome(),
                produto.getImagem(),
                produto.getUsado(),
                produto.getQuantidade()
        );
    }
}
