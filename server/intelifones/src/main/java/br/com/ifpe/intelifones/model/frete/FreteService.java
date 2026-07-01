package br.com.ifpe.intelifones.model.frete;

import br.com.ifpe.intelifones.model.produto.Produto;
import br.com.ifpe.intelifones.model.produto.ProdutoRepository;
import br.com.ifpe.intelifones.model.usuario.Endereco;
import br.com.ifpe.intelifones.model.usuario.EnderecoRepository;
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

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final ProdutoRepository produtoRepository;
    private final EnderecoRepository enderecoRepository;

    /**
     * Calcula o frete de um produto específico até o endereço do comprador.
     * A ORIGEM é o endereço principal do VENDEDOR do produto — correto para marketplace.
     * O DESTINO é o endereço informado pelo comprador.
     */
    public FreteResponse calcularFretePorProduto(Long produtoId, String enderecoDestino) {
        if (enderecoDestino == null || enderecoDestino.trim().isEmpty()) {
            throw new BusinessException("Endereço de entrega é obrigatório");
        }

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new BusinessException("Produto não encontrado"));

        // Busca o endereço principal do VENDEDOR deste produto
        Endereco enderecoVendedor = enderecoRepository
                .findByUsuarioIdAndPrincipalTrue(produto.getVendedor().getId())
                .orElseThrow(() -> new BusinessException(
                        "O vendedor deste produto ainda não cadastrou um endereço de envio"));

        String enderecoOrigem = enderecoVendedor.toEnderecoCompleto();

        return calcular(enderecoOrigem, enderecoDestino,
                produto.getVendedor().getNome(), produto.getNome());
    }

    /**
     * Calcula o frete de uma origem genérica (string) para um destino.
     * Mantido para compatibilidade com o endpoint público de simulação.
     */
    public FreteResponse calcularFrete(String enderecoOrigem, String enderecoDestino) {
        if (enderecoDestino == null || enderecoDestino.trim().isEmpty()) {
            throw new BusinessException("Endereço de entrega é obrigatório");
        }
        if (enderecoOrigem == null || enderecoOrigem.trim().isEmpty()) {
            throw new BusinessException("Endereço de origem é obrigatório");
        }
        return calcular(enderecoOrigem, enderecoDestino, null, null);
    }

    private FreteResponse calcular(String enderecoOrigem, String enderecoDestino,
                                    String nomeVendedor, String nomeProduto) {
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

            log.info("Google Maps API response: {}", response);

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
                    .nomeVendedor(nomeVendedor)
                    .nomeProduto(nomeProduto)
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