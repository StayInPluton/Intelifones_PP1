package br.com.ifpe.intelifones.api.comprador;

import br.com.ifpe.intelifones.model.comprador.Comprador;
import br.com.ifpe.intelifones.model.comprador.CompradorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/compradores")
public class CompradorController {

    @Autowired
    private CompradorService compradorService;

    @PostMapping
    public ResponseEntity<Comprador> create(@Valid @RequestBody CompradorRequest request) {
        Comprador comprador = request.build();
        Comprador savedComprador = compradorService.save(comprador);
        return new ResponseEntity<>(savedComprador, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Comprador>> listAll() {
        List<Comprador> compradores = compradorService.listarTodos();
        return ResponseEntity.ok(compradores);
    }

    @GetMapping("/ativos")
    public ResponseEntity<List<Comprador>> listAtivos() {
        List<Comprador> compradores = compradorService.listarCompradoresAtivos();
        return ResponseEntity.ok(compradores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comprador> getById(@PathVariable Long id) {
        Comprador comprador = compradorService.obterPorID(id);
        return ResponseEntity.ok(comprador);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Comprador> getByEmail(@PathVariable String email) {
        Comprador comprador = compradorService.obterPorEmail(email);
        return ResponseEntity.ok(comprador);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Comprador> update(
            @PathVariable Long id,
            @Valid @RequestBody CompradorRequest request) {
        Comprador comprador = request.build();
        Comprador updatedComprador = compradorService.update(id, comprador);
        return ResponseEntity.ok(updatedComprador);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        compradorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}