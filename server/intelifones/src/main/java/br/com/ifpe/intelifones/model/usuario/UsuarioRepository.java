package br.com.ifpe.intelifones.model.usuario;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    // Para recuperação de senha
    Optional<Usuario> findByTokenRecuperacaoSenha(String token);
}
