package br.com.ifpe.intelifones.model.produto;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoriaProdutoRepository extends JpaRepository<CategoriaProduto, Long> {

    // Busca categoria por nome (ignorando maiúsculas/minúsculas)
    Optional<CategoriaProduto> findByNomeIgnoreCase(String nome);

    // Verifica se já existe categoria com o mesmo nome
    boolean existsByNomeIgnoreCase(String nome);

    // Busca categorias que contenham determinado texto no nome
    List<CategoriaProduto> findByNomeContainingIgnoreCase(String nome);

    // Busca categorias ordenadas por nome
    List<CategoriaProduto> findAllByOrderByNomeAsc();

    // Busca categorias habilitadas (já com o SQLRestriction)
    // O SQLRestriction já filtra apenas habilitado = true

    // Busca categorias com produtos associados (JPQL)
    @Query("SELECT DISTINCT c FROM CategoriaProduto c JOIN c.produtos p WHERE p.habilitado = true")
    List<CategoriaProduto> findCategoriasComProdutos();

    // Conta quantos produtos estão associados a uma categoria
    @Query("SELECT COUNT(p) FROM Produto p WHERE p.categoria.id = :categoriaId AND p.habilitado = true")
    long countProdutosByCategoriaId(@Param("categoriaId") Long categoriaId);
}