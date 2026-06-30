package br.com.ifpe.intelifones.model.usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EnderecoRepository extends JpaRepository<Endereco, Long> {

    List<Endereco> findByUsuarioId(Long usuarioId);

    Optional<Endereco> findByUsuarioIdAndPrincipalTrue(Long usuarioId);

    int countByUsuarioId(Long usuarioId);

    @Modifying
    @Query("UPDATE Endereco e SET e.principal = false WHERE e.usuario.id = :usuarioId")
    void desativarTodosPrincipais(Long usuarioId);
}
