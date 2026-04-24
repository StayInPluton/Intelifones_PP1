package br.com.ifpe.intelifones.model.comprador;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CompradorRepository extends JpaRepository<Comprador, Long> {

    Optional<Comprador> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    List<Comprador> findByNomeContainingIgnoreCase(String nome);

    List<Comprador> findByAtivoTrue();

    @Query("SELECT c FROM Comprador c WHERE c.ativo = true AND c.habilitado = true")
    List<Comprador> findCompradoresAtivos();
}