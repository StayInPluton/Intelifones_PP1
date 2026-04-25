package br.com.ifpe.intelifones.api.produto;

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
    private String estadoConservacao;
    private Boolean ativo;
    private Long categoria_id;  // ← Volta a ser Long

    public Produto build() {
        return Produto.builder()
                .nome(nome)
                .descricao(descricao)
                .preco(preco)
                .usado(usado != null ? usado : false)
                .estadoConservacao(estadoConservacao)
                .ativo(ativo != null ? ativo : true)
                .build();
    }
}