package br.com.ifpe.intelifones.model.frete;

import br.com.ifpe.intelifones.util.exception.BusinessException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class FreteService {

    @Value("${google.maps.api.key}")
    private String googleMapsApiKey;

    @Value("${loja.endereco.origem}")
    private String enderecoOrigem;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public FreteResponse calcularFrete(String enderecoDestino) {
        if (enderecoDestino == null || enderecoDestino.trim().isEmpty()) {
            throw new BusinessException("Endereço de entrega é obrigatório");
        }

        String url = UriComponentsBuilder
                .fromHttpUrl("https://maps.googleapis.com/maps/api/distancematrix/json")
                .queryParam("origins", enderecoOrigem)
                .queryParam("destinations", enderecoDestino)
                .queryParam("key", googleMapsApiKey)
                .queryParam("language", "pt-BR")
                .queryParam("units", "metric")
                .build()
                .toUriString();

        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);

            if (!"OK".equals(root.path("status").asText())) {
                throw new BusinessException("Não foi possível calcular o frete. Verifique o endereço informado.");
            }

            JsonNode element = root.path("rows").get(0).path("elements").get(0);

            if (!"OK".equals(element.path("status").asText())) {
                throw new BusinessException("Endereço de destino não encontrado.");
            }

            long distanciaMetros = element.path("distance").path("value").asLong();
            String distanciaTexto = element.path("distance").path("text").asText();
            String duracaoTexto = element.path("duration").path("text").asText();
            double distanciaKm = distanciaMetros / 1000.0;

            return FreteResponse.builder()
                    .enderecoOrigem(enderecoOrigem)
                    .enderecoDestino(enderecoDestino)
                    .distanciaTexto(distanciaTexto)
                    .distanciaKm(distanciaKm)
                    .duracaoEstimada(duracaoTexto)
                    .valorFrete(calcularValorPorDistancia(distanciaKm))
                    .build();

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao chamar Google Maps API: {}", e.getMessage(), e);
            throw new BusinessException("Erro ao calcular frete. Tente novamente.");
        }
    }

    private double calcularValorPorDistancia(double distanciaKm) {
        if (distanciaKm <= 10)       return 10.0;
        else if (distanciaKm <= 30)  return 20.0;
        else if (distanciaKm <= 60)  return 35.0;
        else if (distanciaKm <= 100) return 50.0;
        else if (distanciaKm <= 300) return 80.0;
        else return 80.0 + ((distanciaKm - 300) * 0.30);
    }
}
