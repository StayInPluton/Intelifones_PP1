package br.com.ifpe.intelifones.api.categoria;

import br.com.ifpe.intelifones.model.categoria.Categoria;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaRequest {

    private String nome;
    private String descricao;

    public Categoria build() {
        return Categoria.builder()
                .nome(nome)
                .descricao(descricao)
                .build();
    }
}