package br.com.ifpe.intelifones.model.usuario;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Entity
@Table(name = "endereco")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnore
    private Usuario usuario;

    @Column(nullable = false, length = 150)
    @NotBlank(message = "Logradouro é obrigatório")
    private String logradouro;

    @Column(nullable = false, length = 10)
    @NotBlank(message = "Número é obrigatório")
    private String numero;

    @Column(length = 100)
    private String complemento;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Bairro é obrigatório")
    private String bairro;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Cidade é obrigatória")
    private String cidade;

    @Column(nullable = false, length = 2)
    @NotBlank(message = "UF é obrigatória")
    private String uf;

    @Column(nullable = false, length = 9)
    @NotBlank(message = "CEP é obrigatório")
    @Pattern(regexp = "\\d{5}-\\d{3}", message = "CEP deve estar no formato 00000-000")
    private String cep;

    @Column(nullable = false)
    @Builder.Default
    private Boolean principal = false;

    /**
     * Monta a string de endereço completo para enviar à API do Google Maps (frete).
     * Exemplo: "Av. Paulista, 1000, Bela Vista, São Paulo - SP, 01310-100, Brasil"
     */
    public String toEnderecoCompleto() {
        StringBuilder sb = new StringBuilder();
        sb.append(logradouro).append(", ").append(numero);
        if (complemento != null && !complemento.isBlank()) {
            sb.append(", ").append(complemento);
        }
        sb.append(", ").append(bairro);
        sb.append(", ").append(cidade);
        sb.append(" - ").append(uf);
        sb.append(", ").append(cep);
        sb.append(", Brasil");
        return sb.toString();
    }
}
