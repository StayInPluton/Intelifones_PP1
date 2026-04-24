package br.com.ifpe.intelifones.api.inventario;

import br.com.ifpe.intelifones.model.inventario.Inventario;
import br.com.ifpe.intelifones.model.produto.Produto;
import br.com.ifpe.intelifones.model.vendedor.Vendedor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventarioRequest {

    private Integer quantidade;
    private Double preco;
    private Long produto_id;
    private Long vendedor_id;

    public Inventario build() {
        return Inventario.builder()
                .quantidade(quantidade)
                .preco(preco)
                .produto(Produto.builder().id(produto_id).build())
                .vendedor(Vendedor.builder().id(vendedor_id).build())
                .build();
    }
}