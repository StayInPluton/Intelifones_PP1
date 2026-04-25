package br.com.ifpe.intelifones.api.inventario;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.ifpe.intelifones.model.inventario.Inventario;
import br.com.ifpe.intelifones.model.inventario.InventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/inventarios")
@Tag(name = "Inventário", description = "Endpoints para gerenciamento do inventário de produtos dos vendedores")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @Operation(summary = "Criar um novo item de inventário", description = "Cria um novo item de inventário com os dados fornecidos")
    @PostMapping
    public ResponseEntity<Inventario> create(@Valid @RequestBody InventarioRequest request) {
        Inventario inventario = request.build();
        Inventario savedInventario = inventarioService.save(inventario);
        return new ResponseEntity<>(savedInventario, HttpStatus.CREATED);
    }

    @Operation(summary = "Listar todos os itens de inventário", description = "Retorna uma lista de todos os itens de inventário cadastrados")
    @GetMapping
    public ResponseEntity<List<Inventario>> listAll() {
        List<Inventario> inventarios = inventarioService.listarTodos();
        return ResponseEntity.ok(inventarios);
    }

    @Operation(summary = "Obter item de inventário por ID", description = "Retorna os detalhes de um item de inventário específico usando seu ID")
    @GetMapping("/{id}")
    public ResponseEntity<Inventario> getById(@PathVariable Long id) {
        Inventario inventario = inventarioService.obterPorID(id);
        return ResponseEntity.ok(inventario);
    }

    @Operation(summary = "Listar itens de inventário por vendedor", description = "Retorna uma lista de itens de inventário associados a um vendedor específico usando seu ID")
    @GetMapping("/vendedor/{vendedorId}")
    public ResponseEntity<List<Inventario>> listByVendedor(@PathVariable Long vendedorId) {
        List<Inventario> inventarios = inventarioService.listarPorVendedor(vendedorId);
        return ResponseEntity.ok(inventarios);
    }

    @Operation(summary = "Listar itens de inventário por produto", description = "Retorna uma lista de itens de inventário associados a um produto específico usando seu ID")
    @GetMapping("/produto/{produtoId}")
    public ResponseEntity<List<Inventario>> listByProduto(@PathVariable Long produtoId) {
        List<Inventario> inventarios = inventarioService.listarPorProduto(produtoId);
        return ResponseEntity.ok(inventarios);
    }

    @Operation(summary = "Listar melhores preços por produto", description = "Retorna uma lista dos itens de inventário com os melhores preços para um produto específico usando seu ID")
    @GetMapping("/produto/{produtoId}/melhores-precos")
    public ResponseEntity<List<Inventario>> listMelhoresPrecosByProduto(@PathVariable Long produtoId) {
        List<Inventario> inventarios = inventarioService.listarMelhoresPrecosPorProduto(produtoId);
        return ResponseEntity.ok(inventarios);
    }

    @Operation(summary = "Obter item de inventário por produto e vendedor", description = "Retorna os detalhes de um item de inventário específico usando o ID do produto e do vendedor")
    @GetMapping("/produto/{produtoId}/vendedor/{vendedorId}")
    public ResponseEntity<Inventario> getByProdutoAndVendedor(
            @PathVariable Long produtoId,
            @PathVariable Long vendedorId) {
        Inventario inventario = inventarioService.obterPorProdutoEVendedor(produtoId, vendedorId);
        return ResponseEntity.ok(inventario);
    }

    @Operation(summary = "Obter quantidade total por vendedor", description = "Retorna a soma total da quantidade de itens de inventário habilitados para um vendedor específico usando seu ID")
    @GetMapping("/vendedor/{vendedorId}/quantidade-total")
    public ResponseEntity<Integer> getQuantidadeTotalByVendedor(@PathVariable Long vendedorId) {
        Integer quantidadeTotal = inventarioService.somaQuantidadePorVendedor(vendedorId);
        return ResponseEntity.ok(quantidadeTotal);
    }

    @Operation(summary = "Atualizar um item de inventário", description = "Atualiza os dados de um item de inventário existente usando seu ID")
    @PutMapping("/{id}")
    public ResponseEntity<Inventario> update(
            @PathVariable Long id,
            @Valid @RequestBody InventarioRequest request) {
        Inventario inventario = request.build();
        Inventario updatedInventario = inventarioService.update(id, inventario);
        return ResponseEntity.ok(updatedInventario);
    }

    @Operation(summary = "Diminuir a quantidade de um item de inventário", description = "Diminui a quantidade de um item de inventário específico usando seu ID e a quantidade a ser diminuída")
    @PatchMapping("/{id}/quantidade")
    public ResponseEntity<Void> updateQuantidade(
            @PathVariable Long id,
            @RequestParam Integer quantidade) {
        inventarioService.diminuirQuantidade(id, quantidade);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Adicionar estoque a um item de inventário", description = "Adiciona uma quantidade ao estoque de um item de inventário específico usando o ID do produto, o ID do vendedor e a quantidade a ser adicionada")
    @PatchMapping("/produto/{produtoId}/vendedor/{vendedorId}/adicionar")
    public ResponseEntity<Inventario> adicionarEstoque(
            @PathVariable Long produtoId,
            @PathVariable Long vendedorId,
            @RequestParam Integer quantidade) {
        Inventario inventario = inventarioService.atualizarEstoque(produtoId, vendedorId, quantidade);
        return ResponseEntity.ok(inventario);
    }

    @Operation(summary = "Deletar um item de inventário", description = "Remove um item de inventário do sistema usando seu ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        inventarioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}