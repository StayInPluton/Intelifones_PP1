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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categorias")
@Tag(name = "Categorias de Produto", description = "Endpoints para gerenciamento de categorias de produtos")
public class CategoriaProdutoController {

    @Autowired
    private CategoriaProdutoService categoriaService;

    @Operation(summary = "Criar uma nova categoria de produto", description = "Cria uma nova categoria de produto com os dados fornecidos")
    @PostMapping
    public ResponseEntity<CategoriaProduto> create(@Valid @RequestBody CategoriaProduto categoria) {
        CategoriaProduto savedCategoria = categoriaService.save(categoria);
        return new ResponseEntity<>(savedCategoria, HttpStatus.CREATED);
    }

    @Operation(summary = "Listar todas as categorias de produto", description = "Retorna uma lista de todas as categorias de produto cadastradas, com opção de filtro por nome")
    @GetMapping
    public ResponseEntity<List<CategoriaProduto>> listAll(
            @RequestParam(required = false) String nome) {
        List<CategoriaProduto> categorias = categoriaService.buscarPorNome(nome);
        return ResponseEntity.ok(categorias);
    }

    @Operation(summary = "Listar categorias de produto ordenadas por nome", description = "Retorna uma lista de todas as categorias de produto cadastradas, ordenadas por nome")
    @GetMapping("/ordenadas")
    public ResponseEntity<List<CategoriaProduto>> listAllOrdered() {
        return ResponseEntity.ok(categoriaService.listarTodosOrdenadosPorNome());
    }

    @Operation(summary = "Listar categorias de produto com produtos associados", description = "Retorna uma lista de categorias de produto que possuem produtos associados, incluindo os detalhes dos produtos")
    @GetMapping("/com-produtos")
    public ResponseEntity<List<CategoriaProduto>> listCategoriasComProdutos() {
        return ResponseEntity.ok(categoriaService.listarCategoriasComProdutos());
    }

    @Operation(summary = "Obter categoria de produto por ID", description = "Retorna os detalhes de uma categoria de produto específica usando seu ID")
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaProduto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.obterPorID(id));
    }

    @Operation(summary = "Obter categoria de produto por nome", description = "Retorna os detalhes de uma categoria de produto específica usando seu nome")
    @GetMapping("/nome/{nome}")
    public ResponseEntity<CategoriaProduto> getByNome(@PathVariable String nome) {
        return ResponseEntity.ok(categoriaService.obterPorNome(nome));
    }

    @Operation(summary = "Atualizar uma categoria de produto", description = "Atualiza os dados de uma categoria de produto existente usando seu ID")
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaProduto> update(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaProduto categoria) {
        CategoriaProduto updatedCategoria = categoriaService.update(id, categoria);
        return ResponseEntity.ok(updatedCategoria);
    }

    @Operation(summary = "Desabilitar uma categoria de produto", description = "Desabilita uma categoria de produto existente usando seu ID, tornando-a inativa mas mantendo os dados no sistema")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoriaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Deletar uma categoria de produto permanentemente", description = "Remove completamente uma categoria de produto do sistema usando seu ID, excluindo todos os dados relacionados")
    @DeleteMapping("/{id}/force")
    public ResponseEntity<Void> deleteForce(@PathVariable Long id) {
        categoriaService.deleteForce(id);
        return ResponseEntity.noContent().build();
    }
}