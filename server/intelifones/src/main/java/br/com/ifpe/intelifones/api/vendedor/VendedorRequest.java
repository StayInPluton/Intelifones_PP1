package br.com.ifpe.intelifones.api.vendedor;

import br.com.ifpe.intelifones.model.vendedor.Vendedor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendedorRequest {

    private String nome;
    private String email;
    private String senha;
    private String telefone;
    private String cpfCnpj;
    private Boolean ativo;

    public Vendedor build() {
        return Vendedor.builder()
                .nome(nome)
                .email(email)
                .senha(senha)
                .telefone(telefone)
                .cpfCnpj(cpfCnpj)
                .ativo(ativo != null ? ativo : true)
                .build();
    }
}