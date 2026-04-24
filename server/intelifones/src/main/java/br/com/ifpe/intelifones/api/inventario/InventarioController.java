package br.com.ifpe.intelifones.api.inventario;

import br.com.ifpe.intelifones.model.inventario.Inventario;
import br.com.ifpe.intelifones.model.inventario.InventarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventarios")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @PostMapping
    public ResponseEntity<Inventario> create(@Valid @RequestBody InventarioRequest request) {
        Inventario inventario = request.build();
        Inventario savedInventario = inventarioService.save(inventario);
        return new ResponseEntity<>(savedInventario, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Inventario>> listAll() {
        List<Inventario> inventarios = inventarioService.listarTodos();
        return ResponseEntity.ok(inventarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inventario> getById(@PathVariable Long id) {
        Inventario inventario = inventarioService.obterPorID(id);
        return ResponseEntity.ok(inventario);
    }

    @GetMapping("/vendedor/{vendedorId}")
    public ResponseEntity<List<Inventario>> listByVendedor(@PathVariable Long vendedorId) {
        List<Inventario> inventarios = inventarioService.listarPorVendedor(vendedorId);
        return ResponseEntity.ok(inventarios);
    }

    @GetMapping("/produto/{produtoId}")
    public ResponseEntity<List<Inventario>> listByProduto(@PathVariable Long produtoId) {
        List<Inventario> inventarios = inventarioService.listarPorProduto(produtoId);
        return ResponseEntity.ok(inventarios);
    }

    @GetMapping("/produto/{produtoId}/melhores-precos")
    public ResponseEntity<List<Inventario>> listMelhoresPrecosByProduto(@PathVariable Long produtoId) {
        List<Inventario> inventarios = inventarioService.listarMelhoresPrecosPorProduto(produtoId);
        return ResponseEntity.ok(inventarios);
    }

    @GetMapping("/produto/{produtoId}/vendedor/{vendedorId}")
    public ResponseEntity<Inventario> getByProdutoAndVendedor(
            @PathVariable Long produtoId,
            @PathVariable Long vendedorId) {
        Inventario inventario = inventarioService.obterPorProdutoEVendedor(produtoId, vendedorId);
        return ResponseEntity.ok(inventario);
    }

    @GetMapping("/vendedor/{vendedorId}/quantidade-total")
    public ResponseEntity<Integer> getQuantidadeTotalByVendedor(@PathVariable Long vendedorId) {
        Integer quantidadeTotal = inventarioService.somaQuantidadePorVendedor(vendedorId);
        return ResponseEntity.ok(quantidadeTotal);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Inventario> update(
            @PathVariable Long id,
            @Valid @RequestBody InventarioRequest request) {
        Inventario inventario = request.build();
        Inventario updatedInventario = inventarioService.update(id, inventario);
        return ResponseEntity.ok(updatedInventario);
    }

    @PatchMapping("/{id}/quantidade")
    public ResponseEntity<Void> updateQuantidade(
            @PathVariable Long id,
            @RequestParam Integer quantidade) {
        inventarioService.diminuirQuantidade(id, quantidade);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/produto/{produtoId}/vendedor/{vendedorId}/adicionar")
    public ResponseEntity<Inventario> adicionarEstoque(
            @PathVariable Long produtoId,
            @PathVariable Long vendedorId,
            @RequestParam Integer quantidade) {
        Inventario inventario = inventarioService.atualizarEstoque(produtoId, vendedorId, quantidade);
        return ResponseEntity.ok(inventario);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        inventarioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}