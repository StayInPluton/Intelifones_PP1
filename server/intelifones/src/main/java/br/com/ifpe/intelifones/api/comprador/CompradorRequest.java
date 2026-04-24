package br.com.ifpe.intelifones.api.comprador;

import br.com.ifpe.intelifones.model.comprador.Comprador;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompradorRequest {

    private String nome;
    private String email;
    private String senha;
    private String telefone;
    private String endereco;
    private Boolean ativo;

    public Comprador build() {
        return Comprador.builder()
                .nome(nome)
                .email(email)
                .senha(senha)
                .telefone(telefone)
                .endereco(endereco)
                .ativo(ativo != null ? ativo : true)
                .build();
    }
}