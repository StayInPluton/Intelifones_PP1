package br.com.ifpe.intelifones.api.vendedor;

import br.com.ifpe.intelifones.model.vendedor.Vendedor;
import br.com.ifpe.intelifones.model.vendedor.VendedorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendedores")
public class VendedorController {

    @Autowired
    private VendedorService vendedorService;

    @PostMapping
    public ResponseEntity<Vendedor> create(@Valid @RequestBody VendedorRequest request) {
        Vendedor vendedor = request.build();
        Vendedor savedVendedor = vendedorService.save(vendedor);
        return new ResponseEntity<>(savedVendedor, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Vendedor>> listAll() {
        List<Vendedor> vendedores = vendedorService.listarTodos();
        return ResponseEntity.ok(vendedores);
    }

    @GetMapping("/ativos")
    public ResponseEntity<List<Vendedor>> listAtivos() {
        List<Vendedor> vendedores = vendedorService.listarVendedoresAtivos();
        return ResponseEntity.ok(vendedores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vendedor> getById(@PathVariable Long id) {
        Vendedor vendedor = vendedorService.obterPorID(id);
        return ResponseEntity.ok(vendedor);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Vendedor> getByEmail(@PathVariable String email) {
        Vendedor vendedor = vendedorService.obterPorEmail(email);
        return ResponseEntity.ok(vendedor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vendedor> update(
            @PathVariable Long id,
            @Valid @RequestBody VendedorRequest request) {
        Vendedor vendedor = request.build();
        Vendedor updatedVendedor = vendedorService.update(id, vendedor);
        return ResponseEntity.ok(updatedVendedor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        vendedorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}