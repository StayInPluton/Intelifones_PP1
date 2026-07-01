package br.com.ifpe.intelifones.model.frete;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FreteResponse {
    private String enderecoOrigem;
    private String enderecoDestino;
    private String nomeVendedor;   // quem vai enviar
    private String nomeProduto;    // produto que gerou o cálculo
    private String distanciaTexto;
    private Double distanciaKm;
    private String duracaoEstimada;
    private Double valorFrete;
}