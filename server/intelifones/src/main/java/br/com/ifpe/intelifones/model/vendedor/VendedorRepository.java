package br.com.ifpe.intelifones.model.vendedor;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VendedorRepository extends JpaRepository<Vendedor, Long> {

    Optional<Vendedor> findByEmailIgnoreCase(String email);

    Optional<Vendedor> findByCpfCnpj(String cpfCnpj);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByCpfCnpj(String cpfCnpj);

    List<Vendedor> findByNomeContainingIgnoreCase(String nome);

    List<Vendedor> findByAtivoTrue();

    @Query("SELECT v FROM Vendedor v WHERE v.ativo = true AND v.habilitado = true")
    List<Vendedor> findVendedoresAtivos();

    @Query("SELECT COUNT(i) FROM Inventario i WHERE i.vendedor.id = :vendedorId AND i.habilitado = true")
    long countProdutosEmInventario(@Param("vendedorId") Long vendedorId);
}