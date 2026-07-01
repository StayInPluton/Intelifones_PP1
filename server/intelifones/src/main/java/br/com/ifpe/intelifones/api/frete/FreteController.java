package br.com.ifpe.intelifones.api.frete;

import br.com.ifpe.intelifones.model.frete.FreteResponse;
import br.com.ifpe.intelifones.model.frete.FreteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/frete")
@CrossOrigin(origins = {"*"})
@RequiredArgsConstructor
@Tag(name = "Frete", description = "Cálculo de frete via Google Maps Distance Matrix API")
public class FreteController {

    private final FreteService freteService;

    /**
     * Calcula o frete de um produto específico até o endereço do comprador.
     * A origem é o endereço do VENDEDOR deste produto (correto para marketplace).
     * Use este endpoint no Checkout.
     */
    @Operation(summary = "Calcular frete de um produto até o endereço de entrega",
               description = "A origem é o endereço cadastrado pelo vendedor do produto. " +
                             "Passe o produtoId e o endereço de destino do comprador.")
    @PostMapping("/calcular")
    public ResponseEntity<FreteResponse> calcularFrete(@RequestBody Map<String, String> payload) {
        String enderecoDestino = payload.get("endereco");
        String produtoIdStr = payload.get("produtoId");

        if (produtoIdStr != null && !produtoIdStr.isBlank()) {
            Long produtoId = Long.parseLong(produtoIdStr);
            return ResponseEntity.ok(freteService.calcularFretePorProduto(produtoId, enderecoDestino));
        }

        // Fallback: origem genérica (para simulação/testes)
        String enderecoOrigem = payload.get("enderecoOrigem");
        return ResponseEntity.ok(freteService.calcularFrete(enderecoOrigem, enderecoDestino));
    }
}