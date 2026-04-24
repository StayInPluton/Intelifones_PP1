package br.com.ifpe.intelifones.api.produto;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.ifpe.intelifones.model.produto.CategoriaProduto;
import br.com.ifpe.intelifones.model.produto.CategoriaProdutoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaProdutoController {

    @Autowired
    private CategoriaProdutoService categoriaService;

    @PostMapping
    public ResponseEntity<CategoriaProduto> create(@Valid @RequestBody CategoriaProduto categoria) {
        CategoriaProduto savedCategoria = categoriaService.save(categoria);
        return new ResponseEntity<>(savedCategoria, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CategoriaProduto>> listAll(
            @RequestParam(required = false) String nome) {
        List<CategoriaProduto> categorias = categoriaService.buscarPorNome(nome);
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/ordenadas")
    public ResponseEntity<List<CategoriaProduto>> listAllOrdered() {
        return ResponseEntity.ok(categoriaService.listarTodosOrdenadosPorNome());
    }

    @GetMapping("/com-produtos")
    public ResponseEntity<List<CategoriaProduto>> listCategoriasComProdutos() {
        return ResponseEntity.ok(categoriaService.listarCategoriasComProdutos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaProduto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.obterPorID(id));
    }

    @GetMapping("/nome/{nome}")
    public ResponseEntity<CategoriaProduto> getByNome(@PathVariable String nome) {
        return ResponseEntity.ok(categoriaService.obterPorNome(nome));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaProduto> update(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaProduto categoria) {
        CategoriaProduto updatedCategoria = categoriaService.update(id, categoria);
        return ResponseEntity.ok(updatedCategoria);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoriaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/force")
    public ResponseEntity<Void> deleteForce(@PathVariable Long id) {
        categoriaService.deleteForce(id);
        return ResponseEntity.noContent().build();
    }
}