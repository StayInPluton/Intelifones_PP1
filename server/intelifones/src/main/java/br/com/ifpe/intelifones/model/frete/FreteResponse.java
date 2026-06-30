package br.com.ifpe.intelifones.model.frete;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FreteResponse {
    private String enderecoOrigem;
    private String enderecoDestino;
    private String distanciaTexto;
    private Double distanciaKm;
    private String duracaoEstimada;
    private Double valorFrete;
}
