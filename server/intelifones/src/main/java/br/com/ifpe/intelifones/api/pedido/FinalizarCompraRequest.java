package br.com.ifpe.intelifones.api.pedido;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FinalizarCompraRequest {

    @NotBlank(message = "Endereço é obrigatório")
    private String endereco;

    @NotBlank(message = "CEP é obrigatório")
    private String cep;

    @NotBlank(message = "Número é obrigatório")
    private String numero;

    private String complemento;

    private String telefoneContato;

    @NotBlank(message = "Forma de pagamento é obrigatória")
    private String formaPagamento;

    // Calculado previamente chamando GET /api/frete/calcular?endereco=...
    // antes de chamar este endpoint. Se não informado, assume 0.
    @NotNull(message = "Valor do frete é obrigatório. Calcule em /api/frete/calcular antes de finalizar")
    private Double valorFrete;
}
