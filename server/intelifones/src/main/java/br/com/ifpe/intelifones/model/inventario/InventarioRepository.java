package br.com.ifpe.intelifones.model.inventario;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {

    Optional<Inventario> findByProdutoIdAndVendedorId(Long produtoId, Long vendedorId);

    List<Inventario> findByVendedorId(Long vendedorId);

    List<Inventario> findByProdutoId(Long produtoId);

    List<Inventario> findByQuantidadeGreaterThan(Integer quantidade);

    boolean existsByProdutoIdAndVendedorId(Long produtoId, Long vendedorId);

    @Query("SELECT i FROM Inventario i WHERE i.produto.id = :produtoId AND i.quantidade > 0 ORDER BY i.preco ASC")
    List<Inventario> findMelhoresPrecosPorProduto(@Param("produtoId") Long produtoId);

    @Modifying
    @Query("UPDATE Inventario i SET i.quantidade = i.quantidade - :quantidade WHERE i.id = :id AND i.quantidade >= :quantidade")
    int diminuirQuantidade(@Param("id") Long id, @Param("quantidade") Integer quantidade);

    @Query("SELECT SUM(i.quantidade) FROM Inventario i WHERE i.vendedor.id = :vendedorId AND i.habilitado = true")
    Integer somaQuantidadePorVendedor(@Param("vendedorId") Long vendedorId);
}