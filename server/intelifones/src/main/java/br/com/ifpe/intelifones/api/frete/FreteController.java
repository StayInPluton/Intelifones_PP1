package br.com.ifpe.intelifones.api.frete;

import br.com.ifpe.intelifones.model.frete.FreteResponse;
import br.com.ifpe.intelifones.model.frete.FreteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/frete")
@CrossOrigin(origins = {"*"})
@RequiredArgsConstructor
@Tag(name = "Frete", description = "Cálculo de frete via Google Maps Distance Matrix API")
public class FreteController {

    private final FreteService freteService;

    @Operation(summary = "Calcular frete por endereço de entrega",
               description = "Chame este endpoint antes de finalizar a compra para obter o valorFrete. " +
                             "Você pode usar GET /api/usuarios/enderecos/principal/texto para obter o endereço formatado.")
    @GetMapping("/calcular")
    public ResponseEntity<FreteResponse> calcularFrete(@RequestParam String endereco) {
        return ResponseEntity.ok(freteService.calcularFrete(endereco));
    }
}
