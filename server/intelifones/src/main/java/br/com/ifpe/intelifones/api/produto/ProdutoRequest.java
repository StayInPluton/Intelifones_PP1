package br.com.ifpe.intelifones.api.produto;

import br.com.ifpe.intelifones.model.produto.CategoriaProduto;
import br.com.ifpe.intelifones.model.produto.Produto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoRequest {

    private String nome;
    private String descricao;
    private Double preco;
    private Boolean usado;
    private String estadoConservacao; // novo, seminovo, etc
    private Boolean ativo;
    private CategoriaProduto categoria;

    public Produto build() {

        return Produto.builder()
                .nome(nome)
                .descricao(descricao)
                .preco(preco)
                .usado(usado)
                .estadoConservacao(estadoConservacao)
                .ativo(ativo)
                .categoria(categoria)
                .build();
    }
}