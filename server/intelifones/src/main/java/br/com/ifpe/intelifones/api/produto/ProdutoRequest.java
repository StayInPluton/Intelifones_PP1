package br.com.ifpe.intelifones.api.produto;

import br.com.ifpe.intelifones.model.categoria.Categoria;
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
    private Integer quantidade;
    private Boolean usado;
    private Long categoria_id;

    public Produto build() {
        return Produto.builder()
                .nome(nome)
                .descricao(descricao)
                .preco(preco)
                .quantidade(quantidade)
                .usado(usado != null ? usado : false)
                .categoria(Categoria.builder().id(categoria_id).build())
                .build();
    }
}