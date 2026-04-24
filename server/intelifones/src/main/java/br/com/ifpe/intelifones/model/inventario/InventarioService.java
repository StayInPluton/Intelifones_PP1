package br.com.ifpe.intelifones.model.inventario;

import br.com.ifpe.intelifones.model.produto.Produto;
import br.com.ifpe.intelifones.model.produto.ProdutoService;
import br.com.ifpe.intelifones.model.vendedor.Vendedor;
import br.com.ifpe.intelifones.model.vendedor.VendedorService;
import br.com.ifpe.intelifones.util.exception.InventarioException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventarioService {

    @Autowired
    private InventarioRepository repository;

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private VendedorService vendedorService;

    @Transactional
    public Inventario save(Inventario inventario) {

        // Validações
        if (inventario.getQuantidade() == null || inventario.getQuantidade() <= 0) {
            throw new InventarioException(InventarioException.MSG_QUANTIDADE_INVALIDA);
        }

        if (inventario.getPreco() == null || inventario.getPreco() <= 0) {
            throw new InventarioException(InventarioException.MSG_PRECO_INVALIDO);
        }

        if (inventario.getProduto() == null || inventario.getProduto().getId() == null) {
            throw new InventarioException(InventarioException.MSG_PRODUTO_OBRIGATORIO);
        }

        if (inventario.getVendedor() == null || inventario.getVendedor().getId() == null) {
            throw new InventarioException(InventarioException.MSG_VENDEDOR_OBRIGATORIO);
        }

        // Verifica se o produto existe
        Produto produto = produtoService.obterPorID(inventario.getProduto().getId());
        inventario.setProduto(produto);

        // Verifica se o vendedor existe
        Vendedor vendedor = vendedorService.obterPorID(inventario.getVendedor().getId());
        inventario.setVendedor(vendedor);

        // Verifica se já existe inventário para este produto e vendedor
        if (repository.existsByProdutoIdAndVendedorId(produto.getId(), vendedor.getId())) {
            throw new InventarioException(InventarioException.MSG_INVENTARIO_JA_EXISTE);
        }

        inventario.setHabilitado(Boolean.TRUE);

        return repository.save(inventario);
    }

    @Transactional
    public Inventario atualizarEstoque(Long produtoId, Long vendedorId, Integer quantidadeAdicional) {

        Inventario inventario = repository.findByProdutoIdAndVendedorId(produtoId, vendedorId)
                .orElseThrow(() -> new InventarioException(InventarioException.MSG_INVENTARIO_NAO_ENCONTRADO));

        if (quantidadeAdicional <= 0) {
            throw new InventarioException(InventarioException.MSG_QUANTIDADE_INVALIDA);
        }

        inventario.setQuantidade(inventario.getQuantidade() + quantidadeAdicional);

        return repository.save(inventario);
    }

    public List<Inventario> listarTodos() {
        return repository.findAll();
    }

    public List<Inventario> listarPorVendedor(Long vendedorId) {
        vendedorService.obterPorID(vendedorId); // Verifica se existe
        return repository.findByVendedorId(vendedorId);
    }

    public List<Inventario> listarPorProduto(Long produtoId) {
        produtoService.obterPorID(produtoId); // Verifica se existe
        return repository.findByProdutoId(produtoId);
    }

    public List<Inventario> listarMelhoresPrecosPorProduto(Long produtoId) {
        produtoService.obterPorID(produtoId);
        return repository.findMelhoresPrecosPorProduto(produtoId);
    }

    public Inventario obterPorID(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new InventarioException(
                        String.format(InventarioException.MSG_INVENTARIO_NAO_ENCONTRADO_ID, id)));
    }

    public Inventario obterPorProdutoEVendedor(Long produtoId, Long vendedorId) {
        return repository.findByProdutoIdAndVendedorId(produtoId, vendedorId)
                .orElseThrow(() -> new InventarioException(InventarioException.MSG_INVENTARIO_NAO_ENCONTRADO));
    }

    @Transactional
    public void diminuirQuantidade(Long id, Integer quantidade) {

        if (quantidade <= 0) {
            throw new InventarioException(InventarioException.MSG_QUANTIDADE_INVALIDA);
        }

        int updated = repository.diminuirQuantidade(id, quantidade);

        if (updated == 0) {
            throw new InventarioException(InventarioException.MSG_ESTOQUE_INSUFICIENTE);
        }
    }

    @Transactional
    public Inventario update(Long id, Inventario inventarioAlterado) {

        Inventario inventario = obterPorID(id);

        if (inventarioAlterado.getQuantidade() != null && inventarioAlterado.getQuantidade() >= 0) {
            inventario.setQuantidade(inventarioAlterado.getQuantidade());
        }

        if (inventarioAlterado.getPreco() != null && inventarioAlterado.getPreco() > 0) {
            inventario.setPreco(inventarioAlterado.getPreco());
        }

        inventario.setHabilitado(Boolean.TRUE);

        return repository.save(inventario);
    }

    @Transactional
    public void delete(Long id) {
        Inventario inventario = obterPorID(id);
        inventario.setHabilitado(Boolean.FALSE);
        repository.save(inventario);
    }

    public Integer somaQuantidadePorVendedor(Long vendedorId) {
        Integer soma = repository.somaQuantidadePorVendedor(vendedorId);
        return soma != null ? soma : 0;
    }
}