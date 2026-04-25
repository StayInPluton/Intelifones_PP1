package br.com.ifpe.intelifones.api.produto;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ifpe.intelifones.model.produto.CategoriaProduto;
import br.com.ifpe.intelifones.model.produto.CategoriaProdutoService;
import br.com.ifpe.intelifones.model.produto.Produto;
import br.com.ifpe.intelifones.model.produto.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/produto")
@CrossOrigin
@Tag(name = "Produtos", description = "Endpoints para gerenciamento de produtos disponíveis para venda pelos vendedores")
public class ProdutoController {
    @Autowired
    private ProdutoService produtoService;

     @Autowired
    private CategoriaProdutoService categoriaService;

    @Operation(summary = "Criar um novo produto", description = "Cria um novo produto com os dados fornecidos")
    @PostMapping
     public ResponseEntity<Produto> save(@RequestBody ProdutoRequest request) {
        
        CategoriaProduto categoria = categoriaService.obterPorID(request.getCategoria_id());
        
        // Cria o produto
        Produto produto = request.build();
        
        produto.setCategoria(categoria);
        
        Produto savedProduto = produtoService.save(produto);
        return new ResponseEntity<>(savedProduto, HttpStatus.CREATED);
    }

    @Operation(summary = "Listar todos os produtos", description = "Retorna uma lista de todos os produtos cadastrados no sistema")
    @GetMapping
    public List<Produto> listarTodos() {
        return produtoService.listarTodos();
    }

    @Operation(summary = "Obter produto por ID", description = "Retorna os detalhes de um produto específico usando seu ID")
    @GetMapping("/{id}")
    public Produto obterPorID(@PathVariable Long id) {
        return produtoService.obterPorID(id);
    }

    @Operation(summary = "Obter produto por nome", description = "Retorna os detalhes de um produto específico usando seu nome")
    @PutMapping("/{id}")
     public ResponseEntity<Produto> update(@PathVariable("id") Long id, @RequestBody ProdutoRequest request) {
        
        // Busca a categoria
        CategoriaProduto categoria = categoriaService.obterPorID(request.getCategoria_id());
        
        // Prepara o produto
        Produto produtoAtualizado = request.build();
        produtoAtualizado.setCategoria(categoria);
        
        produtoService.update(id, produtoAtualizado);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Deletar produto por nome", description = "Remove um produto do sistema usando seu ID, excluindo todos os dados relacionados")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        produtoService.delete(id);
        return ResponseEntity.ok().build();
    }
}