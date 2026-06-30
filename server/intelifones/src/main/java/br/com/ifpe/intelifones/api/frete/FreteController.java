package br.com.ifpe.intelifones.api.frete;

import br.com.ifpe.intelifones.model.frete.FreteResponse;
import br.com.ifpe.intelifones.model.frete.FreteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/frete")
@CrossOrigin(origins = {"*"})
@RequiredArgsConstructor
@Tag(name = "Frete", description = "Cálculo de frete via Google Maps Distance Matrix API")
public class FreteController {

    private final FreteService freteService;

@Operation(summary = "Calcular frete por endereço de entrega")
@PostMapping("/calcular") 
public ResponseEntity<FreteResponse> calcularFrete(@RequestBody Map<String, String> payload) {
    String endereco = payload.get("endereco");
    return ResponseEntity.ok(freteService.calcularFrete(endereco));
}
}
