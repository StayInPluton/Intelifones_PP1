package br.com.ifpe.intelifones.model.produto;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ifpe.intelifones.util.exception.CategoriaProdutoException;
import jakarta.transaction.Transactional;

@Service
public class CategoriaProdutoService {

    @Autowired
    private CategoriaProdutoRepository repository;

    @Transactional
    public CategoriaProduto save(CategoriaProduto categoria) {

        // Validação: nome não pode ser nulo ou vazio
        if (categoria.getNome() == null || categoria.getNome().trim().isEmpty()) {
            throw new CategoriaProdutoException(CategoriaProdutoException.MSG_NOME_OBRIGATORIO);
        }

        // Validação: nome deve ter pelo menos 3 caracteres
        if (categoria.getNome().length() < 3) {
            throw new CategoriaProdutoException(CategoriaProdutoException.MSG_NOME_MINIMO_CARACTERES);
        }

        // Validação: nome único (case insensitive)
        if (repository.existsByNomeIgnoreCase(categoria.getNome())) {
            throw new CategoriaProdutoException(CategoriaProdutoException.MSG_NOME_JA_EXISTE);
        }

        // Validação: descrição não pode ser muito longa
        if (categoria.getDescricao() != null && categoria.getDescricao().length() > 500) {
            throw new CategoriaProdutoException(CategoriaProdutoException.MSG_DESCRICAO_MUITO_LONGA);
        }

        categoria.setHabilitado(Boolean.TRUE);
        return repository.save(categoria);
    }

    public List<CategoriaProduto> listarTodos() {
        return repository.findAll();
    }

    public List<CategoriaProduto> listarTodosOrdenadosPorNome() {
        return repository.findAllByOrderByNomeAsc();
    }

    public List<CategoriaProduto> listarCategoriasComProdutos() {
        return repository.findCategoriasComProdutos();
    }

    public List<CategoriaProduto> buscarPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return listarTodosOrdenadosPorNome();
        }
        return repository.findByNomeContainingIgnoreCase(nome);
    }

    public CategoriaProduto obterPorID(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new CategoriaProdutoException(
                        String.format(CategoriaProdutoException.MSG_CATEGORIA_NAO_ENCONTRADA, id)));
    }

    public CategoriaProduto obterPorNome(String nome) {
        return repository.findByNomeIgnoreCase(nome)
                .orElseThrow(() -> new CategoriaProdutoException(
                        String.format(CategoriaProdutoException.MSG_CATEGORIA_NAO_ENCONTRADA_POR_NOME, nome)));
    }

    @Transactional
    public CategoriaProduto update(Long id, CategoriaProduto categoriaAlterada) {

        CategoriaProduto categoria = obterPorID(id);

        // Validação do novo nome
        if (categoriaAlterada.getNome() == null || categoriaAlterada.getNome().trim().isEmpty()) {
            throw new CategoriaProdutoException(CategoriaProdutoException.MSG_NOME_OBRIGATORIO);
        }

        if (categoriaAlterada.getNome().length() < 3) {
            throw new CategoriaProdutoException(CategoriaProdutoException.MSG_NOME_MINIMO_CARACTERES);
        }

        // Verifica se outro registro já usa o mesmo nome
        if (!categoria.getNome().equalsIgnoreCase(categoriaAlterada.getNome()) &&
                repository.existsByNomeIgnoreCase(categoriaAlterada.getNome())) {
            throw new CategoriaProdutoException(CategoriaProdutoException.MSG_NOME_JA_EXISTE);
        }

        if (categoriaAlterada.getDescricao() != null && categoriaAlterada.getDescricao().length() > 500) {
            throw new CategoriaProdutoException(CategoriaProdutoException.MSG_DESCRICAO_MUITO_LONGA);
        }

        categoria.setNome(categoriaAlterada.getNome());
        categoria.setDescricao(categoriaAlterada.getDescricao());
        categoria.setHabilitado(Boolean.TRUE);

        return repository.save(categoria);
    }

    @Transactional
    public void delete(Long id) {

        CategoriaProduto categoria = obterPorID(id);

        // Verifica se existem produtos associados a esta categoria
        long quantidadeProdutos = repository.countProdutosByCategoriaId(id);

        if (quantidadeProdutos > 0) {
            throw new CategoriaProdutoException(
                    String.format(CategoriaProdutoException.MSG_CATEGORIA_COM_PRODUTOS, quantidadeProdutos));
        }

        categoria.setHabilitado(Boolean.FALSE);
        repository.save(categoria);
    }

    @Transactional
    public void deleteForce(Long id) {
        // Delete físico (caso necessário, para remoção permanente)
        CategoriaProduto categoria = obterPorID(id);

        long quantidadeProdutos = repository.countProdutosByCategoriaId(id);
        if (quantidadeProdutos > 0) {
            throw new CategoriaProdutoException(
                    String.format(CategoriaProdutoException.MSG_CATEGORIA_COM_PRODUTOS, quantidadeProdutos));
        }

        repository.delete(categoria);
    }
}