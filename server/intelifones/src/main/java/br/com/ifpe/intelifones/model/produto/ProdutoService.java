package br.com.ifpe.intelifones.model.produto;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ifpe.intelifones.util.exception.ProdutoException;
import jakarta.transaction.Transactional;

@Service
public class ProdutoService {
    @Autowired
    private ProdutoRepository repository;

    @Transactional
    public Produto save(Produto produto) {

        if (produto.getValorUnitario() < 10) {
	    throw new ProdutoException(ProdutoException.MSG_VALOR_MINIMO_PRODUTO);
	}

        produto.setHabilitado(Boolean.TRUE);
        return repository.save(produto);
    }

    public List<Produto> listarTodos() {

        return repository.findAll();
    }

    public Produto obterPorID(Long id) {

        return repository.findById(id).get();
    }

    @Transactional
    public void update(Long id, Produto produtoAlterado) {

        Produto produto = repository.findById(id).get();
        produto.setTitulo(produtoAlterado.getTitulo());
        produto.setCodigo(produtoAlterado.getCodigo());
        produto.setDescricao(produtoAlterado.getDescricao());
        produto.setValorUnitario(produtoAlterado.getValorUnitario());
        produto.setTempoEntregaMinimo(produtoAlterado.getTempoEntregaMinimo());
        produto.setTempoEntregaMaximo(produtoAlterado.getTempoEntregaMaximo());

        repository.save(produto);
    }

    @Transactional
    public void delete(Long id) {

        Produto produto = repository.findById(id).get();
        produto.setHabilitado(Boolean.FALSE);

        repository.save(produto);
    }
}